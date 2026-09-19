import 'package:intl/intl.dart';
import 'package:sahamku/data/db/database_helper.dart';
import 'package:sahamku/data/model/trade_models.dart';

class PortfolioRepository {
  final DatabaseHelper _dbHelper;

  PortfolioRepository({DatabaseHelper? dbHelper})
      : _dbHelper = dbHelper ?? DatabaseHelper.instance;

  Future<double> getCashBalance() async {
    return await _dbHelper.getCash();
  }

  Future<double> getInitialCapital() async {
    return await _dbHelper.getInitialCapital();
  }

  Future<List<PortfolioPosition>> getPositions() async {
    return await _dbHelper.getPortfolioPositions();
  }

  Future<List<TradeOrder>> getHistory() async {
    return await _dbHelper.getTradeHistory();
  }

  Future<List<WatchlistItem>> getWatchlist() async {
    return await _dbHelper.getWatchlist();
  }

  Future<bool> isWatchlisted(String ticker) async {
    return await _dbHelper.isInWatchlist(ticker);
  }

  Future<bool> toggleWatchlist(String ticker) async {
    final currentlyIn = await _dbHelper.isInWatchlist(ticker);
    if (currentlyIn) {
      await _dbHelper.removeFromWatchlist(ticker);
      return false;
    } else {
      await _dbHelper.addToWatchlist(ticker);
      return true;
    }
  }

  Future<bool> buyStock(String ticker, double shares, double price) async {
    if (shares <= 0 || price <= 0) return false;

    final upperTicker = ticker.trim().toUpperCase();
    final totalCost = shares * price;
    final currentCash = await _dbHelper.getCash();

    if (currentCash < totalCost) {
      return false; // Insufficient cash
    }

    final newCash = currentCash - totalCost;
    await _dbHelper.updateCash(newCash);

    final existingPos = await _dbHelper.getPosition(upperTicker);
    if (existingPos != null) {
      final totalShares = existingPos.shares + shares;
      final totalSpent = (existingPos.shares * existingPos.avgBuyPrice) + totalCost;
      final newAvgPrice = totalSpent / totalShares;

      await _dbHelper.savePosition(PortfolioPosition(
        ticker: upperTicker,
        shares: totalShares,
        avgBuyPrice: newAvgPrice,
      ));
    } else {
      await _dbHelper.savePosition(PortfolioPosition(
        ticker: upperTicker,
        shares: shares,
        avgBuyPrice: price,
      ));
    }

    final timestamp = DateFormat('yyyy-MM-dd HH:mm:ss').format(DateTime.now());
    await _dbHelper.recordTrade(TradeOrder(
      ticker: upperTicker,
      type: 'BUY',
      shares: shares,
      price: price,
      totalAmount: totalCost,
      timestamp: timestamp,
    ));

    return true;
  }

  Future<bool> sellStock(String ticker, double shares, double price) async {
    if (shares <= 0 || price <= 0) return false;

    final upperTicker = ticker.trim().toUpperCase();
    final existingPos = await _dbHelper.getPosition(upperTicker);

    if (existingPos == null || existingPos.shares < shares) {
      return false; // Insufficient shares
    }

    final totalProceeds = shares * price;
    final currentCash = await _dbHelper.getCash();
    final newCash = currentCash + totalProceeds;

    await _dbHelper.updateCash(newCash);

    final remainingShares = existingPos.shares - shares;
    if (remainingShares <= 0.000001) {
      await _dbHelper.deletePosition(upperTicker);
    } else {
      await _dbHelper.savePosition(PortfolioPosition(
        ticker: upperTicker,
        shares: remainingShares,
        avgBuyPrice: existingPos.avgBuyPrice,
      ));
    }

    final timestamp = DateFormat('yyyy-MM-dd HH:mm:ss').format(DateTime.now());
    await _dbHelper.recordTrade(TradeOrder(
      ticker: upperTicker,
      type: 'SELL',
      shares: shares,
      price: price,
      totalAmount: totalProceeds,
      timestamp: timestamp,
    ));

    return true;
  }

  Future<void> resetData() async {
    await _dbHelper.resetAllData();
  }
}
