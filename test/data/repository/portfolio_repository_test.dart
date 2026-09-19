import 'package:flutter_test/flutter_test.dart';
import 'package:sqflite_common_ffi/sqflite_ffi.dart';
import 'package:sahamku/data/db/database_helper.dart';
import 'package:sahamku/data/repository/portfolio_repository.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  setUpAll(() {
    sqfliteFfiInit();
    databaseFactory = databaseFactoryFfi;
  });

  group('PortfolioRepository Tests', () {
    late PortfolioRepository repository;
    late DatabaseHelper dbHelper;

    setUp(() async {
      dbHelper = DatabaseHelper.instance;
      await dbHelper.resetAllData();
      repository = PortfolioRepository(dbHelper: dbHelper);
    });

    test('Initial cash balance should be 10000.0', () async {
      final cash = await repository.getCashBalance();
      expect(cash, equals(10000.0));
    });

    test('Buy stock updates cash and portfolio position', () async {
      final success = await repository.buyStock('NVDA', 2.5, 140.0);
      expect(success, isTrue);

      final cash = await repository.getCashBalance();
      expect(cash, equals(10000.0 - (2.5 * 140.0)));

      final positions = await repository.getPositions();
      expect(positions.length, equals(1));
      expect(positions.first.ticker, equals('NVDA'));
      expect(positions.first.shares, equals(2.5));
      expect(positions.first.avgBuyPrice, equals(140.0));
    });

    test('Sell stock updates cash and reduces shares', () async {
      await repository.buyStock('NVDA', 5.0, 100.0);
      final sellSuccess = await repository.sellStock('NVDA', 2.0, 120.0);
      expect(sellSuccess, isTrue);

      final positions = await repository.getPositions();
      expect(positions.first.shares, equals(3.0));

      final history = await repository.getHistory();
      expect(history.length, equals(2));
      expect(history.first.type, equals('SELL'));
    });

    test('Watchlist toggle functionality', () async {
      final added = await repository.toggleWatchlist('AAPL');
      expect(added, isTrue);

      final isListed = await repository.isWatchlisted('AAPL');
      expect(isListed, isTrue);

      final removed = await repository.toggleWatchlist('AAPL');
      expect(removed, isFalse);
    });
  });
}
