import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:sahamku/data/model/stock.dart';
import 'package:sahamku/ui/components/stock_logo_image.dart';
import 'package:sahamku/ui/main_viewmodel.dart';
import 'package:sahamku/ui/screens/stock_detail_screen.dart';

class PasarScreen extends StatefulWidget {
  const PasarScreen({super.key});

  @override
  State<PasarScreen> createState() => _PasarScreenState();
}

class _PasarScreenState extends State<PasarScreen> with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 4, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final viewModel = Provider.of<MainViewModel>(context);
    final currencyFormatter = NumberFormat.currency(symbol: '\$', decimalDigits: 2);

    return Scaffold(
      appBar: AppBar(
        title: Row(
          children: [
            Image.asset(
              'app/src/main/res/drawable/ic_sahamku_logo.png',
              width: 28,
              height: 28,
              errorBuilder: (_, __, ___) => const Icon(Icons.show_chart, color: Color(0xFF10B981)),
            ),
            const SizedBox(width: 8),
            const Text('SahamKu Pasar AS', style: TextStyle(fontWeight: FontWeight.bold)),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () => viewModel.refreshMarketData(),
          ),
        ],
        bottom: TabBar(
          controller: _tabController,
          isScrollable: true,
          indicatorColor: const Color(0xFF10B981),
          tabs: const [
            Tab(text: 'Gotrade Top Movers'),
            Tab(text: 'Top Gainers'),
            Tab(text: 'Top Losers'),
            Tab(text: 'Top Aktif'),
          ],
        ),
      ),
      body: viewModel.isLoadingMarket
          ? const Center(child: CircularProgressIndicator(color: Color(0xFF10B981)))
          : TabBarView(
              controller: _tabController,
              children: [
                _buildStockList(context, viewModel.gotradeTopMovers, currencyFormatter, isGotrade: true),
                _buildStockList(context, viewModel.topGainers, currencyFormatter),
                _buildStockList(context, viewModel.topLosers, currencyFormatter),
                _buildStockList(context, viewModel.topActive, currencyFormatter),
              ],
            ),
    );
  }

  Widget _buildStockList(BuildContext context, List<Stock> stocks, NumberFormat formatter, {bool isGotrade = false}) {
    if (stocks.isEmpty) {
      return const Center(child: Text('Tidak ada data pasar tersedia.'));
    }

    return RefreshIndicator(
      onRefresh: () => Provider.of<MainViewModel>(context, listen: false).refreshMarketData(),
      child: ListView.separated(
        itemCount: stocks.length,
        separatorBuilder: (_, __) => const Divider(height: 1, color: Color(0xFF334155)),
        itemBuilder: (context, index) {
          final stock = stocks[index];
          final isPositive = stock.changePercent >= 0;
          final color = isPositive ? const Color(0xFF10B981) : const Color(0xFFEF4444);

          return ListTile(
            contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            leading: StockLogoImage(ticker: stock.ticker, logoUrl: stock.logoUrl),
            title: Row(
              children: [
                Text(stock.ticker, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                if (isGotrade) ...[
                  const SizedBox(width: 6),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                    decoration: BoxDecoration(
                      color: const Color(0xFF10B981).withValues(alpha: 0.2),
                      borderRadius: BorderRadius.circular(4),
                    ),
                    child: const Text(
                      'GOTRADE',
                      style: TextStyle(color: Color(0xFF10B981), fontSize: 10, fontWeight: FontWeight.bold),
                    ),
                  )
                ]
              ],
            ),
            subtitle: Text(
              stock.name,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: const TextStyle(color: Colors.grey, fontSize: 12),
            ),
            trailing: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Text(
                  formatter.format(stock.price),
                  style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                ),
                const SizedBox(height: 2),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                  decoration: BoxDecoration(
                    color: color.withValues(alpha: 0.15),
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: Text(
                    '${isPositive ? '+' : ''}${stock.changePercent.toStringAsFixed(2)}%',
                    style: TextStyle(color: color, fontWeight: FontWeight.bold, fontSize: 12),
                  ),
                ),
              ],
            ),
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (_) => StockDetailScreen(ticker: stock.ticker),
                ),
              );
            },
          );
        },
      ),
    );
  }
}
