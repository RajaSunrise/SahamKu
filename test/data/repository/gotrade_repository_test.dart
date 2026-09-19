import 'package:flutter_test/flutter_test.dart';
import 'package:sahamku/data/repository/gotrade_repository.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('GotradeRepository', () {
    final repository = GotradeRepository();

    test('getFallbackStocks returns base stocks list including NVDA', () {
      final gainers = repository.getFallbackStocks('gainers');
      expect(gainers.isNotEmpty, isTrue);
      expect(gainers.any((s) => s.ticker == 'NVDA'), isTrue);
    });

    test('fetchRealStockQuote retrieves stock quote for NVDA', () async {
      final stock = await repository.fetchRealStockQuote('NVDA');
      expect(stock, isNotNull);
      expect(stock!.ticker, equals('NVDA'));
      expect(stock.price, greaterThan(0.0));
    });

    test('scrapeGotradeData scrapes Gotrade web page for real stock tickers', () async {
      final stocks = await repository.scrapeGotradeData();
      expect(stocks.isNotEmpty, isTrue);
      expect(stocks.first.price, greaterThan(0.0));
    });

    test('searchStocks finds matching stocks', () async {
      final results = await repository.searchStocks('AAPL');
      expect(results.isNotEmpty, isTrue);
      expect(results.first.ticker, equals('AAPL'));
    });
  });
}
