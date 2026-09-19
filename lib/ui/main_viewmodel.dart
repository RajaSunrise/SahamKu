import 'dart:async';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sahamku/data/model/stock.dart';
import 'package:sahamku/data/model/trade_models.dart';
import 'package:sahamku/data/repository/gotrade_repository.dart';
import 'package:sahamku/data/repository/portfolio_repository.dart';

enum AppThemeMode { dark, light, system }

class MainViewModel extends ChangeNotifier {
  final GotradeRepository _gotradeRepository = GotradeRepository();
  final PortfolioRepository _portfolioRepository = PortfolioRepository();

  List<Stock> topGainers = [];
  List<Stock> topLosers = [];
  List<Stock> topActive = [];
  List<Stock> gotradeTopMovers = [];
  Map<String, Stock> liveQuotes = {};
  List<PortfolioPosition> portfolioPositions = [];
  List<TradeOrder> tradeHistory = [];
  List<WatchlistItem> watchlistItems = [];
  double cashBalance = 10000.0;
  double initialCapital = 10000.0;

  bool isLoadingMarket = false;
  String searchQuery = "";
  List<Stock> searchResults = [];
  bool isSearching = false;

  AppThemeMode _themeMode = AppThemeMode.dark;
  AppThemeMode get themeMode => _themeMode;

  Timer? _realtimeTimer;

  MainViewModel({bool autoStartRealtime = true}) {
    _loadThemePreference();
    refreshAllData();
    if (autoStartRealtime) {
      _startRealtimeStream();
    }
  }

  ThemeMode get currentThemeMode {
    switch (_themeMode) {
      case AppThemeMode.dark:
        return ThemeMode.dark;
      case AppThemeMode.light:
        return ThemeMode.light;
      case AppThemeMode.system:
        return ThemeMode.system;
    }
  }

  Future<void> _loadThemePreference() async {
    final prefs = await SharedPreferences.getInstance();
    final modeStr = prefs.getString('app_theme_mode') ?? 'dark';
    if (modeStr == 'light') {
      _themeMode = AppThemeMode.light;
    } else if (modeStr == 'system') {
      _themeMode = AppThemeMode.system;
    } else {
      _themeMode = AppThemeMode.dark;
    }
    notifyListeners();
  }

  Future<void> setThemeMode(AppThemeMode mode) async {
    _themeMode = mode;
    notifyListeners();
    final prefs = await SharedPreferences.getInstance();
    String modeStr = 'dark';
    if (mode == AppThemeMode.light) modeStr = 'light';
    if (mode == AppThemeMode.system) modeStr = 'system';
    await prefs.setString('app_theme_mode', modeStr);
  }

  Future<void> refreshAllData() async {
    await Future.wait([
      refreshMarketData(),
      refreshPortfolioData(),
    ]);
  }

  Future<void> refreshMarketData() async {
    isLoadingMarket = true;
    notifyListeners();

    try {
      final gainers = await _gotradeRepository.fetchStockScanner(category: "gainers", limit: 20);
      final losers = await _gotradeRepository.fetchStockScanner(category: "losers", limit: 20);
      final active = await _gotradeRepository.fetchStockScanner(category: "active", limit: 20);
      final topMovers = await _gotradeRepository.scrapeGotradeData();

      topGainers = gainers;
      topLosers = losers;
      topActive = active;
      gotradeTopMovers = topMovers;

      for (var s in [...gainers, ...losers, ...active, ...topMovers]) {
        liveQuotes[s.ticker] = s;
      }
    } catch (_) {}

    isLoadingMarket = false;
    notifyListeners();
  }

  Future<void> refreshPortfolioData() async {
    cashBalance = await _portfolioRepository.getCashBalance();
    initialCapital = await _portfolioRepository.getInitialCapital();
    portfolioPositions = await _portfolioRepository.getPositions();
    tradeHistory = await _portfolioRepository.getHistory();
    watchlistItems = await _portfolioRepository.getWatchlist();

    // Fetch live quote for all positions
    for (var pos in portfolioPositions) {
      final quote = await _gotradeRepository.fetchRealStockQuote(pos.ticker);
      if (quote != null) {
        liveQuotes[pos.ticker] = quote;
      }
    }

    notifyListeners();
  }

  void _startRealtimeStream() {
    _realtimeTimer?.cancel();
    _realtimeTimer = Timer.periodic(const Duration(milliseconds: 1500), (timer) async {
      final tickersToUpdate = liveQuotes.keys.toList();
      if (tickersToUpdate.isNotEmpty) {
        for (var t in tickersToUpdate) {
          final q = await _gotradeRepository.fetchRealStockQuote(t);
          if (q != null) {
            liveQuotes[t] = q;
          }
        }
        notifyListeners();
      }
    });
  }

  void stopRealtimeStream() {
    _realtimeTimer?.cancel();
    _realtimeTimer = null;
  }

  Future<void> search(String query) async {
    searchQuery = query;
    if (query.trim().isEmpty) {
      searchResults = [];
      isSearching = false;
      notifyListeners();
      return;
    }

    isSearching = true;
    notifyListeners();

    searchResults = await _gotradeRepository.searchStocks(query);
    isSearching = false;
    notifyListeners();
  }

  Future<bool> buyStock(String ticker, double shares, double price) async {
    final success = await _portfolioRepository.buyStock(ticker, shares, price);
    if (success) {
      await refreshPortfolioData();
    }
    return success;
  }

  Future<bool> sellStock(String ticker, double shares, double price) async {
    final success = await _portfolioRepository.sellStock(ticker, shares, price);
    if (success) {
      await refreshPortfolioData();
    }
    return success;
  }

  Future<bool> toggleWatchlist(String ticker) async {
    final res = await _portfolioRepository.toggleWatchlist(ticker);
    await refreshPortfolioData();
    return res;
  }

  bool isWatchlisted(String ticker) {
    return watchlistItems.any((w) => w.ticker == ticker.toUpperCase());
  }

  Future<void> resetAllData() async {
    await _portfolioRepository.resetData();
    await refreshAllData();
  }

  double get totalPortfolioValue {
    double stockVal = 0.0;
    for (var pos in portfolioPositions) {
      final currentPrice = liveQuotes[pos.ticker]?.price ?? pos.avgBuyPrice;
      stockVal += pos.shares * currentPrice;
    }
    return cashBalance + stockVal;
  }

  double get totalUnrealizedPnL {
    double pnl = 0.0;
    for (var pos in portfolioPositions) {
      final currentPrice = liveQuotes[pos.ticker]?.price ?? pos.avgBuyPrice;
      pnl += pos.shares * (currentPrice - pos.avgBuyPrice);
    }
    return pnl;
  }

  @override
  void dispose() {
    stopRealtimeStream();
    super.dispose();
  }
}
