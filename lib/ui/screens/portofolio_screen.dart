import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:sahamku/ui/components/stock_logo_image.dart';
import 'package:sahamku/ui/main_viewmodel.dart';
import 'package:sahamku/ui/screens/stock_detail_screen.dart';

class PortofolioScreen extends StatelessWidget {
  const PortofolioScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final viewModel = Provider.of<MainViewModel>(context);
    final currencyFormatter = NumberFormat.currency(symbol: '\$', decimalDigits: 2);
    final isPnlPositive = viewModel.totalUnrealizedPnL >= 0;
    final pnlColor = isPnlPositive ? const Color(0xFF10B981) : const Color(0xFFEF4444);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Portofolio & Watchlist', style: TextStyle(fontWeight: FontWeight.bold)),
      ),
      body: RefreshIndicator(
        onRefresh: () => viewModel.refreshPortfolioData(),
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16.0),
          physics: const AlwaysScrollableScrollPhysics(),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Total Equity Card
              Card(
                color: const Color(0xFF1E293B),
                child: Padding(
                  padding: const EdgeInsets.all(20.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text('Total Nilai Portofolio', style: TextStyle(color: Colors.grey, fontSize: 13)),
                      const SizedBox(height: 4),
                      Text(
                        currencyFormatter.format(viewModel.totalPortfolioValue),
                        style: const TextStyle(fontSize: 28, fontWeight: FontWeight.bold, color: Colors.white),
                      ),
                      const SizedBox(height: 16),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              const Text('Saldo Kas Demo', style: TextStyle(color: Colors.grey, fontSize: 12)),
                              Text(
                                currencyFormatter.format(viewModel.cashBalance),
                                style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.white),
                              ),
                            ],
                          ),
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.end,
                            children: [
                              const Text('Unrealized P/L', style: TextStyle(color: Colors.grey, fontSize: 12)),
                              Text(
                                '${isPnlPositive ? '+' : ''}${currencyFormatter.format(viewModel.totalUnrealizedPnL)}',
                                style: TextStyle(fontWeight: FontWeight.bold, color: pnlColor),
                              ),
                            ],
                          ),
                        ],
                      )
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 24),

              // Open Positions Section
              const Text('Posisi Saham Terbuka', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
              const SizedBox(height: 12),
              viewModel.portfolioPositions.isEmpty
                  ? const Card(
                      child: Padding(
                        padding: EdgeInsets.all(16.0),
                        child: Center(child: Text('Belum ada posisi terbuka. Beli saham di Pasar AS.')),
                      ),
                    )
                  : ListView.separated(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: viewModel.portfolioPositions.length,
                      separatorBuilder: (_, __) => const SizedBox(height: 8),
                      itemBuilder: (context, index) {
                        final pos = viewModel.portfolioPositions[index];
                        final liveStock = viewModel.liveQuotes[pos.ticker];
                        final currentPrice = liveStock?.price ?? pos.avgBuyPrice;
                        final currentVal = pos.shares * currentPrice;
                        final pnl = pos.shares * (currentPrice - pos.avgBuyPrice);
                        final isPosPnl = pnl >= 0;
                        final color = isPosPnl ? const Color(0xFF10B981) : const Color(0xFFEF4444);

                        return Card(
                          child: ListTile(
                            leading: StockLogoImage(
                              ticker: pos.ticker,
                              logoUrl: liveStock?.logoUrl ?? '',
                            ),
                            title: Text(pos.ticker, style: const TextStyle(fontWeight: FontWeight.bold)),
                            subtitle: Text('${pos.shares.toStringAsFixed(4)} lembar @ ${currencyFormatter.format(pos.avgBuyPrice)}'),
                            trailing: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              crossAxisAlignment: CrossAxisAlignment.end,
                              children: [
                                Text(currencyFormatter.format(currentVal), style: const TextStyle(fontWeight: FontWeight.bold)),
                                Text(
                                  '${isPosPnl ? '+' : ''}${currencyFormatter.format(pnl)}',
                                  style: TextStyle(color: color, fontWeight: FontWeight.bold, fontSize: 12),
                                ),
                              ],
                            ),
                            onTap: () {
                              Navigator.push(
                                context,
                                MaterialPageRoute(
                                  builder: (_) => StockDetailScreen(ticker: pos.ticker),
                                ),
                              );
                            },
                          ),
                        );
                      },
                    ),
              const SizedBox(height: 24),

              // Watchlist Section
              const Text('Watchlist Favorit', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
              const SizedBox(height: 12),
              viewModel.watchlistItems.isEmpty
                  ? const Card(
                      child: Padding(
                        padding: EdgeInsets.all(16.0),
                        child: Center(child: Text('Watchlist masih kosong. Tambahkan saham favorit Anda.')),
                      ),
                    )
                  : ListView.separated(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: viewModel.watchlistItems.length,
                      separatorBuilder: (_, __) => const SizedBox(height: 8),
                      itemBuilder: (context, index) {
                        final item = viewModel.watchlistItems[index];
                        final stock = viewModel.liveQuotes[item.ticker];

                        return Card(
                          child: ListTile(
                            leading: StockLogoImage(
                              ticker: item.ticker,
                              logoUrl: stock?.logoUrl ?? '',
                            ),
                            title: Text(item.ticker, style: const TextStyle(fontWeight: FontWeight.bold)),
                            subtitle: Text(stock?.name ?? '${item.ticker} Inc.'),
                            trailing: Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                if (stock != null) ...[
                                  Text(
                                    currencyFormatter.format(stock.price),
                                    style: const TextStyle(fontWeight: FontWeight.bold),
                                  ),
                                  const SizedBox(width: 8),
                                ],
                                IconButton(
                                  icon: const Icon(Icons.star, color: Colors.amber),
                                  onPressed: () => viewModel.toggleWatchlist(item.ticker),
                                ),
                              ],
                            ),
                            onTap: () {
                              Navigator.push(
                                context,
                                MaterialPageRoute(
                                  builder: (_) => StockDetailScreen(ticker: item.ticker),
                                ),
                              );
                            },
                          ),
                        );
                      },
                    ),
            ],
          ),
        ),
      ),
    );
  }
}
