import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:fl_chart/fl_chart.dart';
import 'package:sahamku/data/model/stock.dart';
import 'package:sahamku/ui/components/stock_logo_image.dart';
import 'package:sahamku/ui/main_viewmodel.dart';

class StockDetailScreen extends StatefulWidget {
  final String ticker;

  const StockDetailScreen({super.key, required this.ticker});

  @override
  State<StockDetailScreen> createState() => _StockDetailScreenState();
}

class _StockDetailScreenState extends State<StockDetailScreen> {
  final TextEditingController _buyAmountController = TextEditingController();
  final TextEditingController _sellSharesController = TextEditingController();

  @override
  void dispose() {
    _buyAmountController.dispose();
    _sellSharesController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final viewModel = Provider.of<MainViewModel>(context);
    final stock = viewModel.liveQuotes[widget.ticker.toUpperCase()] ??
        Stock(
          ticker: widget.ticker.toUpperCase(),
          name: '${widget.ticker.toUpperCase()} Inc.',
          price: 150.0,
          change: 2.5,
          changePercent: 1.69,
          volume: 5e9,
        );

    final currencyFormatter = NumberFormat.currency(symbol: '\$', decimalDigits: 2);
    final isPositive = stock.changePercent >= 0;
    final themeColor = isPositive ? const Color(0xFF10B981) : const Color(0xFFEF4444);
    final isWatchlisted = viewModel.isWatchlisted(stock.ticker);

    final positions = viewModel.portfolioPositions.where((p) => p.ticker == stock.ticker).toList();
    final userPosition = positions.isNotEmpty ? positions.first : null;

    return Scaffold(
      appBar: AppBar(
        title: Text(stock.ticker),
        actions: [
          IconButton(
            icon: Icon(
              isWatchlisted ? Icons.star : Icons.star_border,
              color: isWatchlisted ? Colors.amber : null,
            ),
            onPressed: () => viewModel.toggleWatchlist(stock.ticker),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Header Info
            Row(
              children: [
                StockLogoImage(ticker: stock.ticker, logoUrl: stock.logoUrl, size: 52),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(stock.name, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                      Text('${stock.exchange} • ${stock.sector}', style: const TextStyle(color: Colors.grey, fontSize: 13)),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),

            // Live Price
            Row(
              crossAxisAlignment: CrossAxisAlignment.baseline,
              textBaseline: TextBaseline.alphabetic,
              children: [
                Text(
                  currencyFormatter.format(stock.price),
                  style: const TextStyle(fontSize: 32, fontWeight: FontWeight.bold),
                ),
                const SizedBox(width: 12),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: themeColor.withValues(alpha: 0.15),
                    borderRadius: BorderRadius.circular(6),
                  ),
                  child: Text(
                    '${isPositive ? '+' : ''}${currencyFormatter.format(stock.change)} (${isPositive ? '+' : ''}${stock.changePercent.toStringAsFixed(2)}%)',
                    style: TextStyle(color: themeColor, fontWeight: FontWeight.bold, fontSize: 14),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 24),

            // Interactive Price Chart
            const Text('Grafik Pergerakan Realtime', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            const SizedBox(height: 12),
            SizedBox(
              height: 200,
              child: stock.sparklinePoints.isNotEmpty
                  ? LineChart(
                      LineChartData(
                        gridData: const FlGridData(show: false),
                        titlesData: const FlTitlesData(show: false),
                        borderData: FlBorderData(show: false),
                        lineBarsData: [
                          LineChartBarData(
                            spots: stock.sparklinePoints
                                .asMap()
                                .entries
                                .map((e) => FlSpot(e.key.toDouble(), e.value))
                                .toList(),
                            isCurved: true,
                            color: themeColor,
                            barWidth: 2.5,
                            isStrokeCapRound: true,
                            dotData: const FlDotData(show: false),
                            belowBarData: BarAreaData(
                              show: true,
                              color: themeColor.withValues(alpha: 0.15),
                            ),
                          ),
                        ],
                      ),
                    )
                  : const Center(child: Text('Data grafik tidak tersedia')),
            ),
            const SizedBox(height: 24),

            // User Position Card if owned
            if (userPosition != null) ...[
              Card(
                color: const Color(0xFF1E293B),
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text('Posisi Saham Anda', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                      const SizedBox(height: 8),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text('Jumlah Lembar: ${userPosition.shares.toStringAsFixed(4)}'),
                          Text('Rata-rata: ${currencyFormatter.format(userPosition.avgBuyPrice)}'),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 24),
            ],

            // Action Buttons (Beli & Jual)
            Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFF10B981),
                      padding: const EdgeInsets.symmetric(vertical: 14),
                    ),
                    onPressed: () => _showBuyDialog(context, viewModel, stock),
                    child: const Text('Beli', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 16)),
                  ),
                ),
                if (userPosition != null) ...[
                  const SizedBox(width: 12),
                  Expanded(
                    child: ElevatedButton(
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFFEF4444),
                        padding: const EdgeInsets.symmetric(vertical: 14),
                      ),
                      onPressed: () => _showSellDialog(context, viewModel, stock, userPosition.shares),
                      child: const Text('Jual', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 16)),
                    ),
                  ),
                ]
              ],
            ),
            const SizedBox(height: 24),

            // Technical Indicators & Description
            const Text('Statistik & Indikator Teknikal', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            const SizedBox(height: 12),
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  children: [
                    _buildStatRow('RSI (14)', stock.rsi.toStringAsFixed(1)),
                    _buildStatRow('Status MACD', stock.macdStatus),
                    _buildStatRow('EMA 20', currencyFormatter.format(stock.ema20)),
                    _buildStatRow('EMA 50', currencyFormatter.format(stock.ema50)),
                    _buildStatRow('Rekomendasi', stock.recommendationText),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),

            if (stock.description.isNotEmpty) ...[
              const Text('Deskripsi & Analisis Gotrade', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
              const SizedBox(height: 8),
              Text(stock.description, style: const TextStyle(color: Colors.grey, height: 1.4)),
            ]
          ],
        ),
      ),
    );
  }

  Widget _buildStatRow(String title, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(title, style: const TextStyle(color: Colors.grey)),
          Text(value, style: const TextStyle(fontWeight: FontWeight.bold)),
        ],
      ),
    );
  }

  void _showBuyDialog(BuildContext context, MainViewModel viewModel, Stock stock) {
    final currencyFormatter = NumberFormat.currency(symbol: '\$', decimalDigits: 2);
    _buyAmountController.clear();

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text('Beli ${stock.ticker}'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Saldo Kas Tersedia: ${currencyFormatter.format(viewModel.cashBalance)}'),
            Text('Harga Saat Ini: ${currencyFormatter.format(stock.price)}'),
            const SizedBox(height: 12),
            TextField(
              controller: _buyAmountController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: const InputDecoration(
                labelText: 'Jumlah Investasi (\$ USD)',
                border: OutlineInputBorder(),
                prefixText: '\$ ',
              ),
            ),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Batal')),
          ElevatedButton(
            style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF10B981)),
            onPressed: () async {
              final usdAmount = double.tryParse(_buyAmountController.text) ?? 0.0;
              if (usdAmount <= 0) return;
              final shares = usdAmount / stock.price;

              final ok = await viewModel.buyStock(stock.ticker, shares, stock.price);
              if (mounted) {
                Navigator.pop(ctx);
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(
                    content: Text(ok
                        ? 'Berhasil membeli ${shares.toStringAsFixed(4)} lembar ${stock.ticker}!'
                        : 'Gagal transaksi. Periksa saldo kas Anda.'),
                    backgroundColor: ok ? const Color(0xFF10B981) : Colors.red,
                  ),
                );
              }
            },
            child: const Text('Konfirmasi Beli', style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
    );
  }

  void _showSellDialog(BuildContext context, MainViewModel viewModel, Stock stock, double maxShares) {
    _sellSharesController.clear();

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text('Jual ${stock.ticker}'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Jumlah Lembar Dimiliki: ${maxShares.toStringAsFixed(4)}'),
            const SizedBox(height: 12),
            TextField(
              controller: _sellSharesController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: const InputDecoration(
                labelText: 'Jumlah Lembar yang Dijual',
                border: OutlineInputBorder(),
              ),
            ),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Batal')),
          ElevatedButton(
            style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFFEF4444)),
            onPressed: () async {
              final sharesToSell = double.tryParse(_sellSharesController.text) ?? 0.0;
              if (sharesToSell <= 0 || sharesToSell > maxShares) return;

              final ok = await viewModel.sellStock(stock.ticker, sharesToSell, stock.price);
              if (mounted) {
                Navigator.pop(ctx);
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(
                    content: Text(ok ? 'Berhasil menjual $sharesToSell lembar!' : 'Gagal menjual.'),
                    backgroundColor: ok ? const Color(0xFF10B981) : Colors.red,
                  ),
                );
              }
            },
            child: const Text('Konfirmasi Jual', style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
    );
  }
}
