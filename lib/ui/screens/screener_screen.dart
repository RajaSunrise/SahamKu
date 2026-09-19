import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:sahamku/ui/components/stock_logo_image.dart';
import 'package:sahamku/ui/main_viewmodel.dart';
import 'package:sahamku/ui/screens/stock_detail_screen.dart';

class ScreenerScreen extends StatefulWidget {
  const ScreenerScreen({super.key});

  @override
  State<ScreenerScreen> createState() => _ScreenerScreenState();
}

class _ScreenerScreenState extends State<ScreenerScreen> {
  String _selectedSector = 'Semua';
  double _minRsi = 0;
  double _maxRsi = 100;

  @override
  Widget build(BuildContext context) {
    final viewModel = Provider.of<MainViewModel>(context);
    final currencyFormatter = NumberFormat.currency(symbol: '\$', decimalDigits: 2);

    final allStocksMap = {...viewModel.liveQuotes};
    final allStocks = allStocksMap.values.toList();

    final sectors = ['Semua', ...allStocks.map((s) => s.sector).toSet()];

    final filtered = allStocks.where((s) {
      final matchesSector = _selectedSector == 'Semua' || s.sector == _selectedSector;
      final matchesRsi = s.rsi >= _minRsi && s.rsi <= _maxRsi;
      return matchesSector && matchesRsi;
    }).toList();

    return Scaffold(
      appBar: AppBar(
        title: const Text('Screener Saham', style: TextStyle(fontWeight: FontWeight.bold)),
      ),
      body: Column(
        children: [
          ExpansionTile(
            title: const Text('Filter & Indikator Teknikal', style: TextStyle(fontWeight: FontWeight.bold)),
            children: [
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
                child: Column(
                  children: [
                    DropdownButtonFormField<String>(
                      initialValue: _selectedSector,
                      decoration: const InputDecoration(labelText: 'Sektor', border: OutlineInputBorder()),
                      items: sectors
                          .map((sec) => DropdownMenuItem(value: sec, child: Text(sec)))
                          .toList(),
                      onChanged: (val) {
                        if (val != null) setState(() => _selectedSector = val);
                      },
                    ),
                    const SizedBox(height: 16),
                    Text('Rentang RSI: ${_minRsi.round()} - ${_maxRsi.round()}'),
                    RangeSlider(
                      values: RangeValues(_minRsi, _maxRsi),
                      min: 0,
                      max: 100,
                      divisions: 100,
                      activeColor: const Color(0xFF10B981),
                      labels: RangeLabels('${_minRsi.round()}', '${_maxRsi.round()}'),
                      onChanged: (values) {
                        setState(() {
                          _minRsi = values.start;
                          _maxRsi = values.end;
                        });
                      },
                    ),
                  ],
                ),
              ),
            ],
          ),
          const Divider(),
          Expanded(
            child: filtered.isEmpty
                ? const Center(child: Text('Tidak ada saham yang memenuhi kriteria screener.'))
                : ListView.separated(
                    itemCount: filtered.length,
                    separatorBuilder: (_, __) => const Divider(height: 1),
                    itemBuilder: (context, index) {
                      final stock = filtered[index];
                      return ListTile(
                        leading: StockLogoImage(ticker: stock.ticker, logoUrl: stock.logoUrl),
                        title: Text(stock.ticker, style: const TextStyle(fontWeight: FontWeight.bold)),
                        subtitle: Text('${stock.sector} • RSI: ${stock.rsi.toStringAsFixed(1)}'),
                        trailing: Text(
                          currencyFormatter.format(stock.price),
                          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
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
          ),
        ],
      ),
    );
  }
}
