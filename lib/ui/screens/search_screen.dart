import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:sahamku/ui/components/stock_logo_image.dart';
import 'package:sahamku/ui/main_viewmodel.dart';
import 'package:sahamku/ui/screens/stock_detail_screen.dart';

class SearchScreen extends StatefulWidget {
  const SearchScreen({super.key});

  @override
  State<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends State<SearchScreen> {
  final TextEditingController _searchController = TextEditingController();

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final viewModel = Provider.of<MainViewModel>(context);
    final currencyFormatter = NumberFormat.currency(symbol: '\$', decimalDigits: 2);

    return Scaffold(
      appBar: AppBar(
        title: TextField(
          controller: _searchController,
          autofocus: true,
          decoration: const InputDecoration(
            hintText: 'Cari Saham US (e.g. NVDA, AAPL, TSLA)...',
            border: InputBorder.none,
          ),
          onChanged: (val) {
            viewModel.search(val);
          },
        ),
        actions: [
          if (_searchController.text.isNotEmpty)
            IconButton(
              icon: const Icon(Icons.clear),
              onPressed: () {
                _searchController.clear();
                viewModel.search('');
              },
            ),
        ],
      ),
      body: viewModel.isSearching
          ? const Center(child: CircularProgressIndicator(color: Color(0xFF10B981)))
          : viewModel.searchResults.isEmpty
              ? Center(
                  child: Text(
                    viewModel.searchQuery.isEmpty
                        ? 'Ketik simbol ticker atau nama perusahaan.'
                        : 'Hasil pencarian untuk "${viewModel.searchQuery}" tidak ditemukan.',
                    style: const TextStyle(color: Colors.grey),
                  ),
                )
              : ListView.separated(
                  itemCount: viewModel.searchResults.length,
                  separatorBuilder: (_, __) => const Divider(height: 1, color: Color(0xFF334155)),
                  itemBuilder: (context, index) {
                    final stock = viewModel.searchResults[index];
                    final isPositive = stock.changePercent >= 0;
                    final color = isPositive ? const Color(0xFF10B981) : const Color(0xFFEF4444);

                    return ListTile(
                      leading: StockLogoImage(ticker: stock.ticker, logoUrl: stock.logoUrl),
                      title: Text(stock.ticker, style: const TextStyle(fontWeight: FontWeight.bold)),
                      subtitle: Text(stock.name, maxLines: 1, overflow: TextOverflow.ellipsis),
                      trailing: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        crossAxisAlignment: CrossAxisAlignment.end,
                        children: [
                          Text(currencyFormatter.format(stock.price), style: const TextStyle(fontWeight: FontWeight.bold)),
                          Text(
                            '${isPositive ? '+' : ''}${stock.changePercent.toStringAsFixed(2)}%',
                            style: TextStyle(color: color, fontWeight: FontWeight.bold, fontSize: 12),
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
