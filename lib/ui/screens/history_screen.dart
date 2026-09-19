import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:sahamku/ui/components/stock_logo_image.dart';
import 'package:sahamku/ui/main_viewmodel.dart';

class HistoryScreen extends StatelessWidget {
  const HistoryScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final viewModel = Provider.of<MainViewModel>(context);
    final currencyFormatter = NumberFormat.currency(symbol: '\$', decimalDigits: 2);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Riwayat Transaksi', style: TextStyle(fontWeight: FontWeight.bold)),
      ),
      body: viewModel.tradeHistory.isEmpty
          ? const Center(child: Text('Belum ada riwayat transaksi demo.'))
          : ListView.separated(
              padding: const EdgeInsets.all(16.0),
              itemCount: viewModel.tradeHistory.length,
              separatorBuilder: (_, __) => const SizedBox(height: 8),
              itemBuilder: (context, index) {
                final trade = viewModel.tradeHistory[index];
                final isBuy = trade.type == 'BUY';
                final color = isBuy ? const Color(0xFF10B981) : const Color(0xFFEF4444);

                return Card(
                  child: ListTile(
                    leading: StockLogoImage(
                      ticker: trade.ticker,
                      logoUrl: viewModel.liveQuotes[trade.ticker]?.logoUrl ?? '',
                    ),
                    title: Row(
                      children: [
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                          decoration: BoxDecoration(
                            color: color.withValues(alpha: 0.15),
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: Text(
                            trade.type,
                            style: TextStyle(color: color, fontWeight: FontWeight.bold, fontSize: 12),
                          ),
                        ),
                        const SizedBox(width: 8),
                        Text(trade.ticker, style: const TextStyle(fontWeight: FontWeight.bold)),
                      ],
                    ),
                    subtitle: Text('${trade.shares.toStringAsFixed(4)} lembar @ ${currencyFormatter.format(trade.price)}\n${trade.timestamp}'),
                    trailing: Text(
                      currencyFormatter.format(trade.totalAmount),
                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                    ),
                  ),
                );
              },
            ),
    );
  }
}
