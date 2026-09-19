import 'package:path/path.dart';
import 'package:sqflite_common_ffi/sqflite_ffi.dart';
import 'package:sahamku/data/model/trade_models.dart';
import 'package:flutter/foundation.dart';

class DatabaseHelper {
  static final DatabaseHelper instance = DatabaseHelper._init();
  static Database? _database;

  DatabaseHelper._init();

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDB('sahamku.db');
    return _database!;
  }

  Future<Database> _initDB(String filePath) async {
    if (!kIsWeb && (defaultTargetPlatform == TargetPlatform.windows || defaultTargetPlatform == TargetPlatform.linux || defaultTargetPlatform == TargetPlatform.macOS)) {
      sqfliteFfiInit();
      databaseFactory = databaseFactoryFfi;
    }

    final dbPath = await getDatabasesPath();
    final path = join(dbPath, filePath);

    return await openDatabase(
      path,
      version: 1,
      onCreate: _createDB,
    );
  }

  Future _createDB(Database db, int version) async {
    await db.execute('''
      CREATE TABLE portfolio (
        ticker TEXT PRIMARY KEY,
        shares REAL NOT NULL,
        avgBuyPrice REAL NOT NULL
      )
    ''');

    await db.execute('''
      CREATE TABLE trade_history (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        ticker TEXT NOT NULL,
        type TEXT NOT NULL,
        shares REAL NOT NULL,
        price REAL NOT NULL,
        totalAmount REAL NOT NULL,
        timestamp TEXT NOT NULL
      )
    ''');

    await db.execute('''
      CREATE TABLE watchlist (
        ticker TEXT PRIMARY KEY,
        addedAt TEXT NOT NULL
      )
    ''');

    await db.execute('''
      CREATE TABLE user_account (
        id INTEGER PRIMARY KEY,
        cash REAL NOT NULL,
        initialCapital REAL NOT NULL
      )
    ''');

    await db.insert('user_account', {
      'id': 1,
      'cash': 10000.0,
      'initialCapital': 10000.0,
    });
  }

  Future<double> getCash() async {
    final db = await instance.database;
    final result = await db.query('user_account', where: 'id = ?', whereArgs: [1]);
    if (result.isNotEmpty) {
      return (result.first['cash'] as num).toDouble();
    }
    return 10000.0;
  }

  Future<double> getInitialCapital() async {
    final db = await instance.database;
    final result = await db.query('user_account', where: 'id = ?', whereArgs: [1]);
    if (result.isNotEmpty) {
      return (result.first['initialCapital'] as num).toDouble();
    }
    return 10000.0;
  }

  Future<void> updateCash(double newCash) async {
    final db = await instance.database;
    await db.update(
      'user_account',
      {'cash': newCash},
      where: 'id = ?',
      whereArgs: [1],
    );
  }

  Future<List<PortfolioPosition>> getPortfolioPositions() async {
    final db = await instance.database;
    final result = await db.query('portfolio');
    return result.map((map) => PortfolioPosition.fromMap(map)).toList();
  }

  Future<PortfolioPosition?> getPosition(String ticker) async {
    final db = await instance.database;
    final result = await db.query(
      'portfolio',
      where: 'ticker = ?',
      whereArgs: [ticker.toUpperCase()],
    );
    if (result.isNotEmpty) {
      return PortfolioPosition.fromMap(result.first);
    }
    return null;
  }

  Future<void> savePosition(PortfolioPosition position) async {
    final db = await instance.database;
    await db.insert(
      'portfolio',
      position.toMap(),
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

  Future<void> deletePosition(String ticker) async {
    final db = await instance.database;
    await db.delete(
      'portfolio',
      where: 'ticker = ?',
      whereArgs: [ticker.toUpperCase()],
    );
  }

  Future<void> recordTrade(TradeOrder order) async {
    final db = await instance.database;
    await db.insert('trade_history', order.toMap());
  }

  Future<List<TradeOrder>> getTradeHistory() async {
    final db = await instance.database;
    final result = await db.query('trade_history', orderBy: 'id DESC');
    return result.map((map) => TradeOrder.fromMap(map)).toList();
  }

  Future<List<WatchlistItem>> getWatchlist() async {
    final db = await instance.database;
    final result = await db.query('watchlist', orderBy: 'addedAt DESC');
    return result.map((map) => WatchlistItem.fromMap(map)).toList();
  }

  Future<bool> isInWatchlist(String ticker) async {
    final db = await instance.database;
    final result = await db.query(
      'watchlist',
      where: 'ticker = ?',
      whereArgs: [ticker.toUpperCase()],
    );
    return result.isNotEmpty;
  }

  Future<void> addToWatchlist(String ticker) async {
    final db = await instance.database;
    await db.insert(
      'watchlist',
      WatchlistItem(
        ticker: ticker.toUpperCase(),
        addedAt: DateTime.now().toIso8601String(),
      ).toMap(),
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

  Future<void> removeFromWatchlist(String ticker) async {
    final db = await instance.database;
    await db.delete(
      'watchlist',
      where: 'ticker = ?',
      whereArgs: [ticker.toUpperCase()],
    );
  }

  Future<void> resetAllData() async {
    final db = await instance.database;
    await db.delete('portfolio');
    await db.delete('trade_history');
    await db.delete('watchlist');
    await db.update(
      'user_account',
      {
        'cash': 10000.0,
        'initialCapital': 10000.0,
      },
      where: 'id = ?',
      whereArgs: [1],
    );
  }
}
