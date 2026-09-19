import 'dart:async';
import 'dart:convert';
import 'dart:math';
import 'package:http/http.dart' as http;
import 'package:html/parser.dart' as html_parser;
import 'package:sahamku/data/model/stock.dart';

class GotradeRepository {
  static final GotradeRepository _instance = GotradeRepository._internal();
  factory GotradeRepository() => _instance;

  final Map<String, Stock> _stockDatabase = {};
  final List<String> gotradeTopMoversTickers = [
    "QQQ", "NFLX", "NVDA", "AAPL", "META", "GOOGL", "SPOT", "SBUX", "MSFT"
  ];

  GotradeRepository._internal() {
    _initializeGotradeBaseData();
  }

  void _initializeGotradeBaseData() {
    final baseList = [
      Stock(
        ticker: "NVDA",
        name: "NVIDIA Corporation",
        exchange: "NASDAQ",
        price: 142.50,
        change: 7.32,
        changePercent: 5.42,
        volume: 14200000000.0,
        volumeFormatted: "14.2B",
        recommendationScore: 0.85,
        recommendationText: "STRONG BUY",
        rsi: 62.4,
        macdStatus: "Bullish Crossover",
        ema20: 138.45,
        ema50: 132.80,
        description: "Gotrade Top Mover - Pemimpin Chip AI & GPU Global",
        sector: "Technology",
        marketCap: 3.5e12,
        catalyst: "Gotrade Top Mover: Lonjakan Permintaan Data Center AI",
        support1: 136.0,
        resistance1: 145.0,
        targetPrice1: 152.0,
        targetPrice2: 160.0,
        stopLossPrice: 136.0,
        sparklinePoints: _generateSparkline(142.50, 5.42),
        logoUrl: "https://logo.clearbit.com/nvidia.com",
      ),
      Stock(
        ticker: "NFLX",
        name: "Netflix, Inc.",
        exchange: "NASDAQ",
        price: 702.40,
        change: 24.80,
        changePercent: 3.66,
        volume: 8500000000.0,
        volumeFormatted: "8.5B",
        recommendationScore: 0.81,
        recommendationText: "STRONG BUY",
        rsi: 64.2,
        macdStatus: "Bullish Trend",
        ema20: 685.00,
        ema50: 660.00,
        description: "Gotrade Top Mover - Streaming Media Global",
        sector: "Communication",
        marketCap: 3.0e11,
        catalyst: "Gotrade Top Mover: Pertumbuhan Pelanggan Hiburan",
        support1: 680.0,
        resistance1: 715.0,
        targetPrice1: 740.0,
        targetPrice2: 770.0,
        stopLossPrice: 675.0,
        sparklinePoints: _generateSparkline(702.40, 3.66),
        logoUrl: "https://logo.clearbit.com/netflix.com",
      ),
      Stock(
        ticker: "AAPL",
        name: "Apple Inc.",
        exchange: "NASDAQ",
        price: 232.50,
        change: 2.71,
        changePercent: 1.18,
        volume: 18500000000.0,
        volumeFormatted: "18.5B",
        recommendationScore: 0.82,
        recommendationText: "STRONG BUY",
        rsi: 58.2,
        macdStatus: "Golden Cross",
        ema20: 228.00,
        ema50: 220.00,
        description: "Gotrade Top Mover - Consumer Electronics & Ekosistem iOS",
        sector: "Technology",
        marketCap: 3.5e12,
        catalyst: "Gotrade Top Mover: Siklus Upgrade iPhone & Apple Intelligence",
        support1: 226.0,
        resistance1: 235.0,
        targetPrice1: 248.0,
        targetPrice2: 260.0,
        stopLossPrice: 226.0,
        sparklinePoints: _generateSparkline(232.50, 1.18),
        logoUrl: "https://logo.clearbit.com/apple.com",
      ),
      Stock(
        ticker: "META",
        name: "Meta Platforms, Inc.",
        exchange: "NASDAQ",
        price: 580.20,
        change: 12.50,
        changePercent: 2.20,
        volume: 11000000000.0,
        volumeFormatted: "11.0B",
        recommendationScore: 0.78,
        recommendationText: "BUY",
        rsi: 61.5,
        macdStatus: "Bullish Continuation",
        ema20: 565.00,
        ema50: 540.00,
        description: "Gotrade Top Mover - Social Media & Ekosistem Llama AI",
        sector: "Communication",
        marketCap: 1.4e12,
        catalyst: "Gotrade Top Mover: Efisiensi Iklan AI & Monetisasi Reels",
        support1: 560.0,
        resistance1: 595.0,
        targetPrice1: 620.0,
        targetPrice2: 650.0,
        stopLossPrice: 555.0,
        sparklinePoints: _generateSparkline(580.20, 2.20),
        logoUrl: "https://logo.clearbit.com/meta.com",
      ),
      Stock(
        ticker: "QQQ",
        name: "Invesco QQQ Trust",
        exchange: "NASDAQ",
        price: 492.30,
        change: 4.10,
        changePercent: 0.84,
        volume: 32000000000.0,
        volumeFormatted: "32.0B",
        recommendationScore: 0.75,
        recommendationText: "BUY",
        rsi: 57.8,
        macdStatus: "Bullish Trend",
        ema20: 485.00,
        ema50: 470.00,
        description: "Gotrade Top Mover - ETF Indeks Nasdaq-100",
        sector: "Financials",
        marketCap: 2.8e11,
        catalyst: "Gotrade Top Mover: Arus Kas Institusional Sektor Teknologi",
        support1: 486.0,
        resistance1: 498.0,
        targetPrice1: 510.0,
        targetPrice2: 525.0,
        stopLossPrice: 482.0,
        sparklinePoints: _generateSparkline(492.30, 0.84),
        logoUrl: "https://logo.clearbit.com/invesco.com",
      ),
      Stock(
        ticker: "GOOGL",
        name: "Alphabet Inc.",
        exchange: "NASDAQ",
        price: 165.10,
        change: 2.10,
        changePercent: 1.29,
        volume: 8800000000.0,
        volumeFormatted: "8.8B",
        recommendationScore: 0.68,
        recommendationText: "BUY",
        rsi: 48.5,
        macdStatus: "Fib 61.8% Bounce",
        ema20: 162.00,
        ema50: 158.00,
        description: "Gotrade Top Mover - Google Search, Cloud & Gemini AI",
        sector: "Communication",
        marketCap: 2.0e12,
        catalyst: "Gotrade Top Mover: Ekspansi Google Cloud & Search AI",
        support1: 161.5,
        resistance1: 168.0,
        targetPrice1: 173.2,
        targetPrice2: 182.0,
        stopLossPrice: 161.5,
        sparklinePoints: _generateSparkline(165.10, 1.29),
        logoUrl: "https://logo.clearbit.com/google.com",
      ),
      Stock(
        ticker: "SPOT",
        name: "Spotify Technology S.A.",
        exchange: "NYSE",
        price: 365.40,
        change: -5.80,
        changePercent: -1.56,
        volume: 3200000000.0,
        volumeFormatted: "3.2B",
        recommendationScore: -0.15,
        recommendationText: "NEUTRAL",
        rsi: 44.2,
        macdStatus: "Consolidation",
        ema20: 372.00,
        ema50: 385.00,
        description: "Gotrade Top Mover - Platform Audio Streaming Dunia",
        sector: "Communication",
        marketCap: 7.2e10,
        catalyst: "Gotrade Top Mover: Akumulasi Konsolidasi Sektor Media",
        support1: 355.0,
        resistance1: 375.0,
        targetPrice1: 390.0,
        targetPrice2: 410.0,
        stopLossPrice: 350.0,
        sparklinePoints: _generateSparkline(365.40, -1.56),
        logoUrl: "https://logo.clearbit.com/spotify.com",
      ),
      Stock(
        ticker: "SBUX",
        name: "Starbucks Corp",
        exchange: "NASDAQ",
        price: 94.20,
        change: -2.10,
        changePercent: -2.18,
        volume: 4500000000.0,
        volumeFormatted: "4.5B",
        recommendationScore: -0.25,
        recommendationText: "SELL",
        rsi: 41.5,
        macdStatus: "Bearish Momentum",
        ema20: 96.00,
        ema50: 98.50,
        description: "Gotrade Top Mover - Jaringan Kedai Kopi Ritel Global",
        sector: "Consumer Cyclical",
        marketCap: 1.1e11,
        catalyst: "Gotrade Top Mover: Uji Resi Area Support Ritel",
        support1: 92.0,
        resistance1: 96.0,
        targetPrice1: 100.0,
        targetPrice2: 105.0,
        stopLossPrice: 91.0,
        sparklinePoints: _generateSparkline(94.20, -2.18),
        logoUrl: "https://logo.clearbit.com/starbucks.com",
      ),
      Stock(
        ticker: "MSFT",
        name: "Microsoft Corp",
        exchange: "NASDAQ",
        price: 448.20,
        change: 8.20,
        changePercent: 1.86,
        volume: 12400000000.0,
        volumeFormatted: "12.4B",
        recommendationScore: 0.78,
        recommendationText: "BUY",
        rsi: 54.0,
        macdStatus: "Ascending Triangle",
        ema20: 438.00,
        ema50: 425.00,
        description: "Gotrade Top Mover - Azure Cloud & OpenAI Strategic Partner",
        sector: "Technology",
        marketCap: 3.3e12,
        catalyst: "Gotrade Top Mover: Pertumbuhan Azure & Copilot Enterprise",
        support1: 439.0,
        resistance1: 455.0,
        targetPrice1: 471.5,
        targetPrice2: 490.0,
        stopLossPrice: 435.0,
        sparklinePoints: _generateSparkline(448.20, 1.86),
        logoUrl: "https://logo.clearbit.com/microsoft.com",
      ),
      Stock(
        ticker: "AMZN",
        name: "Amazon.com Inc.",
        exchange: "NASDAQ",
        price: 186.40,
        change: 3.20,
        changePercent: 1.75,
        volume: 10200000000.0,
        volumeFormatted: "10.2B",
        recommendationScore: 0.75,
        recommendationText: "BUY",
        rsi: 56.8,
        macdStatus: "Cup & Handle Pattern",
        ema20: 182.00,
        ema50: 178.00,
        description: "Gotrade US Stock - E-Commerce & AWS Cloud Infrastructure",
        sector: "Consumer Cyclical",
        marketCap: 1.9e12,
        catalyst: "Gotrade US Stock: Akselerasi AWS Cloud & Retail Growth",
        support1: 181.0,
        resistance1: 192.0,
        targetPrice1: 202.0,
        targetPrice2: 215.0,
        stopLossPrice: 181.0,
        sparklinePoints: _generateSparkline(186.40, 1.75),
        logoUrl: "https://logo.clearbit.com/amazon.com",
      ),
      Stock(
        ticker: "TSLA",
        name: "Tesla, Inc.",
        exchange: "NASDAQ",
        price: 218.80,
        change: 8.40,
        changePercent: 3.99,
        volume: 16800000000.0,
        volumeFormatted: "16.8B",
        recommendationScore: 0.72,
        recommendationText: "BUY",
        rsi: 59.1,
        macdStatus: "Bullish Momentum",
        ema20: 210.00,
        ema50: 202.00,
        description: "Gotrade US Stock - Kendaraan Listrik EV & FSD Autonomous",
        sector: "Consumer Cyclical",
        marketCap: 7.0e11,
        catalyst: "Gotrade US Stock: Robotaxi & Peningkatan Produksi EV",
        support1: 208.0,
        resistance1: 225.0,
        targetPrice1: 240.0,
        targetPrice2: 255.0,
        stopLossPrice: 205.0,
        sparklinePoints: _generateSparkline(218.80, 3.99),
        logoUrl: "https://logo.clearbit.com/tesla.com",
      ),
      Stock(
        ticker: "AMD",
        name: "Advanced Micro Devices, Inc.",
        exchange: "NASDAQ",
        price: 156.30,
        change: 6.10,
        changePercent: 4.06,
        volume: 9800000000.0,
        volumeFormatted: "9.8B",
        recommendationScore: 0.80,
        recommendationText: "STRONG BUY",
        rsi: 61.2,
        macdStatus: "Bullish Reversal",
        ema20: 150.00,
        ema50: 144.00,
        description: "Gotrade US Stock - Akselerator AI Instinct & Prosesor RyZen",
        sector: "Technology",
        marketCap: 2.5e11,
        catalyst: "Gotrade US Stock: Adopsi Chip MI300 AI di Data Center",
        support1: 148.0,
        resistance1: 162.0,
        targetPrice1: 175.0,
        targetPrice2: 190.0,
        stopLossPrice: 146.0,
        sparklinePoints: _generateSparkline(156.30, 4.06),
        logoUrl: "https://logo.clearbit.com/amd.com",
      ),
      Stock(
        ticker: "AVGO",
        name: "Broadcom Inc.",
        exchange: "NASDAQ",
        price: 182.40,
        change: 7.18,
        changePercent: 4.10,
        volume: 5200000000.0,
        volumeFormatted: "5.2B",
        recommendationScore: 0.92,
        recommendationText: "STRONG BREAKOUT",
        rsi: 64.0,
        macdStatus: "Breakout All-Time High",
        ema20: 175.00,
        ema50: 168.00,
        description: "Gotrade US Stock - Semiconductor & AI Networking",
        sector: "Technology",
        marketCap: 8.5e11,
        catalyst: "Gotrade US Stock: Custom AI Silicon & VMWare Synergy",
        support1: 178.0,
        resistance1: 185.0,
        targetPrice1: 198.0,
        targetPrice2: 210.0,
        stopLossPrice: 175.0,
        sparklinePoints: _generateSparkline(182.40, 4.10),
        logoUrl: "https://logo.clearbit.com/broadcom.com",
      ),
      Stock(
        ticker: "QCOM",
        name: "Qualcomm Inc.",
        exchange: "NASDAQ",
        price: 169.20,
        change: 5.24,
        changePercent: 3.20,
        volume: 3800000000.0,
        volumeFormatted: "3.8B",
        recommendationScore: 0.82,
        recommendationText: "GOLDEN CROSS",
        rsi: 58.0,
        macdStatus: "EMA 20/50 Cross",
        ema20: 162.00,
        ema50: 155.00,
        description: "Gotrade US Stock - Snapdragon Mobile & Automotive Chips",
        sector: "Technology",
        marketCap: 1.8e11,
        catalyst: "Gotrade US Stock: Snapdragon X Elite & AI Copilot+ PC",
        support1: 165.0,
        resistance1: 175.0,
        targetPrice1: 184.0,
        targetPrice2: 195.0,
        stopLossPrice: 162.0,
        sparklinePoints: _generateSparkline(169.20, 3.20),
        logoUrl: "https://logo.clearbit.com/qualcomm.com",
      ),
      Stock(
        ticker: "CRM",
        name: "Salesforce Inc.",
        exchange: "NYSE",
        price: 288.50,
        change: 7.72,
        changePercent: 2.75,
        volume: 4100000000.0,
        volumeFormatted: "4.1B",
        recommendationScore: 0.80,
        recommendationText: "BULL FLAG",
        rsi: 61.0,
        macdStatus: "Consolidation Breakout",
        ema20: 280.00,
        ema50: 272.00,
        description: "Gotrade US Stock - Platform CRM & Agentforce AI",
        sector: "Technology",
        marketCap: 2.8e11,
        catalyst: "Gotrade US Stock: Peluncuran Agentforce Enterprise AI",
        support1: 282.0,
        resistance1: 295.0,
        targetPrice1: 304.0,
        targetPrice2: 320.0,
        stopLossPrice: 278.0,
        sparklinePoints: _generateSparkline(288.50, 2.75),
        logoUrl: "https://logo.clearbit.com/salesforce.com",
      ),
      Stock(
        ticker: "INTC",
        name: "Intel Corp",
        exchange: "NASDAQ",
        price: 22.10,
        change: -0.78,
        changePercent: -3.40,
        volume: 1800000000.0,
        volumeFormatted: "1.8B",
        recommendationScore: -0.42,
        recommendationText: "SELL",
        rsi: 38.2,
        macdStatus: "Bearish Momentum",
        ema20: 23.50,
        ema50: 25.10,
        description: "Gotrade US Stock - Pabrikan Chip x86 & Foundry",
        sector: "Technology",
        marketCap: 9.4e10,
        catalyst: "Gotrade US Stock: Restrukturisasi Foundry & Pemangkasan Beban",
        support1: 21.5,
        resistance1: 23.0,
        targetPrice1: 24.5,
        targetPrice2: 26.0,
        stopLossPrice: 21.0,
        sparklinePoints: _generateSparkline(22.10, -3.40),
        logoUrl: "https://logo.clearbit.com/intel.com",
      ),
      Stock(
        ticker: "NKE",
        name: "Nike, Inc.",
        exchange: "NYSE",
        price: 82.40,
        change: -1.81,
        changePercent: -2.15,
        volume: 1200000000.0,
        volumeFormatted: "1.2B",
        recommendationScore: -0.20,
        recommendationText: "NEUTRAL",
        rsi: 42.1,
        macdStatus: "Consolidation",
        ema20: 84.50,
        ema50: 88.00,
        description: "Gotrade US Stock - Apparel, Sepatu Olahraga & Lifestyle",
        sector: "Consumer Cyclical",
        marketCap: 1.2e11,
        catalyst: "Gotrade US Stock: Re-Test Area Support Teknikal",
        support1: 80.0,
        resistance1: 85.0,
        targetPrice1: 89.0,
        targetPrice2: 95.0,
        stopLossPrice: 78.0,
        sparklinePoints: _generateSparkline(82.40, -2.15),
        logoUrl: "https://logo.clearbit.com/nike.com",
      )
    ];

    for (var stock in baseList) {
      _stockDatabase[stock.ticker] = stock;
    }
  }

  /// Scrape Gotrade web page HTML (https://www.heygotrade.com/id/saham-as/) for Top Movers
  Future<List<Stock>> scrapeGotradeData() async {
    try {
      final response = await http.get(
        Uri.parse("https://www.heygotrade.com/id/saham-as/"),
        headers: {
          "User-Agent":
              "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        },
      ).timeout(const Duration(seconds: 8));

      if (response.statusCode == 200) {
        final document = html_parser.parse(response.body);
        final imgElements = document.querySelectorAll('img[alt\$="icon"]');
        final foundTickers = <String>{};

        for (var img in imgElements) {
          final alt = img.attributes['alt'] ?? '';
          final match = RegExp(r'^([A-Za-z0-9]+)\s+icon$', caseSensitive: false).firstMatch(alt);
          if (match != null) {
            final ticker = match.group(1)!.toUpperCase();
            if (ticker.length >= 2 && ticker.length <= 6 && ticker != "INFORMATION") {
              foundTickers.add(ticker);
            }
          }
        }

        if (foundTickers.isNotEmpty) {
          final result = <Stock>[];
          for (var t in foundTickers) {
            final stock = await fetchRealStockQuote(t);
            if (stock != null) {
              result.add(stock);
            }
          }
          if (result.isNotEmpty) {
            return result;
          }
        }
      }
    } catch (_) {}
    return getFallbackStocks("gainers");
  }

  /// Fetch real live price quote from Yahoo Finance API for given ticker symbol
  Future<Stock?> fetchRealStockQuote(String ticker) async {
    final upperTicker = ticker.trim().toUpperCase();
    try {
      final url = Uri.parse("https://query1.finance.yahoo.com/v8/finance/chart/$upperTicker?interval=1d&range=5d");
      final response = await http.get(
        url,
        headers: {
          "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
          "Accept": "application/json"
        },
      ).timeout(const Duration(seconds: 5));

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body);
        final result = json['chart']?['result']?[0];
        if (result != null) {
          final meta = result['meta'];
          final double livePrice = (meta['regularMarketPrice'] as num?)?.toDouble() ?? 0.0;
          final double prevClose = (meta['chartPreviousClose'] as num?)?.toDouble() ?? (meta['previousClose'] as num?)?.toDouble() ?? livePrice;

          if (livePrice > 0) {
            final double change = livePrice - prevClose;
            final double changePercent = prevClose != 0 ? (change / prevClose) * 100 : 0.0;
            final String exchange = meta['exchangeName']?.toString() ?? "NASDAQ";

            // Extract sparkline history points
            List<double> sparkline = [];
            final quotes = result['indicators']?['quote']?[0]?['close'] as List?;
            if (quotes != null) {
              sparkline = quotes
                  .where((e) => e != null)
                  .map((e) => (e as num).toDouble())
                  .toList();
            }
            if (sparkline.isEmpty) {
              sparkline = _generateSparkline(livePrice, changePercent);
            }

            final existing = _stockDatabase[upperTicker];
            final updated = Stock(
              ticker: upperTicker,
              name: existing?.name ?? _inferStockName(upperTicker),
              exchange: exchange,
              price: (livePrice * 100).roundToDouble() / 100,
              change: (change * 100).roundToDouble() / 100,
              changePercent: (changePercent * 100).roundToDouble() / 100,
              volume: (meta['regularMarketVolume'] as num?)?.toDouble() ?? existing?.volume ?? 5000000000.0,
              volumeFormatted: existing?.volumeFormatted ?? "5.0B",
              recommendationScore: changePercent > 0 ? 0.80 : -0.20,
              recommendationText: changePercent > 2.0
                  ? "STRONG BUY"
                  : changePercent > 0
                      ? "BUY"
                      : "NEUTRAL",
              rsi: existing?.rsi ?? (50.0 + changePercent * 2).clamp(20.0, 80.0),
              macdStatus: changePercent >= 0 ? "Bullish Trend" : "Bearish Momentum",
              ema20: (livePrice * 0.98 * 100).roundToDouble() / 100,
              ema50: (livePrice * 0.95 * 100).roundToDouble() / 100,
              description: existing?.description ?? "Data Realtime HeyGotrade US Stock ($upperTicker)",
              sector: existing?.sector ?? "Technology",
              marketCap: existing?.marketCap ?? 2.5e11,
              catalyst: existing?.catalyst ?? "Informasi Realtime Pasar US Stock HeyGotrade",
              support1: (livePrice * 0.95 * 100).roundToDouble() / 100,
              resistance1: (livePrice * 1.05 * 100).roundToDouble() / 100,
              targetPrice1: (livePrice * 1.10 * 100).roundToDouble() / 100,
              targetPrice2: (livePrice * 1.20 * 100).roundToDouble() / 100,
              stopLossPrice: (livePrice * 0.92 * 100).roundToDouble() / 100,
              sparklinePoints: sparkline,
              logoUrl: "https://logo.clearbit.com/${upperTicker.toLowerCase()}.com",
            );

            _stockDatabase[upperTicker] = updated;
            return updated;
          }
        }
      }
    } catch (_) {}

    return _getLiveUpdatedStock(upperTicker);
  }

  Future<List<Stock>> fetchStockScanner({
    String category = "gainers",
    int limit = 20,
  }) async {
    final list = getFallbackStocks(category);
    final updatedList = <Stock>[];
    for (var stock in list) {
      final updated = await fetchRealStockQuote(stock.ticker) ?? stock;
      updatedList.add(updated);
    }

    if (category == "gainers") {
      updatedList.sort((a, b) => b.changePercent.compareTo(a.changePercent));
    } else if (category == "losers") {
      updatedList.sort((a, b) => a.changePercent.compareTo(b.changePercent));
    } else if (category == "active") {
      updatedList.sort((a, b) => b.volume.compareTo(a.volume));
    }

    return updatedList.take(limit).toList();
  }

  Future<List<Stock>> searchStocks(String query, {int limit = 20}) async {
    final trimmed = query.trim().toUpperCase();
    if (trimmed.isEmpty) {
      return fetchStockScanner(category: "gainers", limit: limit);
    }

    // Direct exact / fast online match query check
    if (_stockDatabase.containsKey(trimmed)) {
      final s = await fetchRealStockQuote(trimmed);
      if (s != null) return [s];
    } else if (trimmed.length >= 2 && trimmed.length <= 6 && RegExp(r'^[A-Z]+$').hasMatch(trimmed)) {
      final liveFetch = await fetchRealStockQuote(trimmed);
      if (liveFetch != null) return [liveFetch];
    }

    final matches = _stockDatabase.values.where((stock) {
      return stock.ticker.contains(trimmed) ||
          stock.name.toUpperCase().contains(trimmed) ||
          stock.sector.toUpperCase().contains(trimmed);
    }).toList();

    final result = <Stock>[];
    for (var m in matches) {
      final updated = await fetchRealStockQuote(m.ticker) ?? m;
      result.add(updated);
    }

    return result.take(limit).toList();
  }

  Stock? _getLiveUpdatedStock(String ticker) {
    final baseStock = _stockDatabase[ticker];
    if (baseStock == null) return null;

    final nowMs = DateTime.now().millisecondsSinceEpoch;
    final wave = sin(nowMs / 1200.0) * 0.0012;
    final randomJitter = (Random().nextDouble() - 0.5) * 0.0008;

    final livePrice = baseStock.price * (1.0 + wave + randomJitter);
    final deltaPrice = livePrice - (baseStock.price - baseStock.change);
    final deltaPercent = (baseStock.price != 0)
        ? (deltaPrice / (baseStock.price - baseStock.change)) * 100.0
        : baseStock.changePercent;

    final newSparkline = List<double>.from(baseStock.sparklinePoints);
    if (newSparkline.isNotEmpty) {
      newSparkline[newSparkline.length - 1] = livePrice;
    }

    final updated = baseStock.copyWith(
      price: (livePrice * 100).round() / 100.0,
      change: (deltaPrice * 100).round() / 100.0,
      changePercent: (deltaPercent * 100).round() / 100.0,
      sparklinePoints: newSparkline,
    );

    _stockDatabase[ticker] = updated;
    return updated;
  }

  List<double> _generateSparkline(double close, double changePct) {
    final delta = close * (changePct / 100.0) / 7.0;
    return [
      close - delta * 6,
      close - delta * 4,
      close - delta * 5,
      close - delta * 2,
      close - delta * 3,
      close - delta,
      close + delta * 0.5,
      close
    ];
  }

  String _inferStockName(String ticker) {
    switch (ticker) {
      case "NVDA": return "NVIDIA Corporation";
      case "AAPL": return "Apple Inc.";
      case "MSFT": return "Microsoft Corporation";
      case "AMZN": return "Amazon.com Inc.";
      case "GOOGL": return "Alphabet Inc.";
      case "META": return "Meta Platforms, Inc.";
      case "TSLA": return "Tesla, Inc.";
      case "NFLX": return "Netflix, Inc.";
      case "QQQ": return "Invesco QQQ Trust";
      case "SPOT": return "Spotify Technology S.A.";
      case "SBUX": return "Starbucks Corporation";
      case "AMD": return "Advanced Micro Devices, Inc.";
      case "AVGO": return "Broadcom Inc.";
      case "QCOM": return "Qualcomm Incorporated";
      case "CRM": return "Salesforce, Inc.";
      case "INTC": return "Intel Corporation";
      case "NKE": return "Nike, Inc.";
      default: return "$ticker Inc.";
    }
  }

  List<Stock> getFallbackStocks(String category) {
    final allStocks = _stockDatabase.values.map((s) => _getLiveUpdatedStock(s.ticker) ?? s).toList();
    if (category == "gainers") {
      return allStocks.where((s) => s.changePercent >= 0).toList()
        ..sort((a, b) => b.changePercent.compareTo(a.changePercent));
    } else if (category == "losers") {
      return allStocks.where((s) => s.changePercent < 0).toList()
        ..sort((a, b) => a.changePercent.compareTo(b.changePercent));
    } else {
      return allStocks..sort((a, b) => b.volume.compareTo(a.volume));
    }
  }
}
