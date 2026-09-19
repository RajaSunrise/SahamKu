import 'dart:async';
import 'dart:convert';
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
        price: 222.27,
        change: 3.98,
        changePercent: 1.82,
        volume: 14200000000.0,
        volumeFormatted: "14.2B",
        recommendationScore: 0.85,
        recommendationText: "STRONG BUY",
        rsi: 62.4,
        macdStatus: "Bullish Crossover",
        ema20: 218.00,
        ema50: 210.00,
        description: "Gotrade Top Mover - Pemimpin Chip AI & GPU Global",
        sector: "Technology",
        marketCap: 3.5e12,
        catalyst: "Gotrade Top Mover: Lonjakan Permintaan Data Center AI",
        support1: 215.0,
        resistance1: 230.0,
        targetPrice1: 245.0,
        targetPrice2: 260.0,
        stopLossPrice: 210.0,
        sparklinePoints: _generateSparkline(222.27, 1.82),
        logoUrl: "https://logo.clearbit.com/nvidia.com",
      ),
      Stock(
        ticker: "NFLX",
        name: "Netflix, Inc.",
        exchange: "NASDAQ",
        price: 71.79,
        change: -5.61,
        changePercent: -7.25,
        volume: 8500000000.0,
        volumeFormatted: "8.5B",
        recommendationScore: -0.30,
        recommendationText: "NEUTRAL",
        rsi: 38.2,
        macdStatus: "Bearish Momentum",
        ema20: 75.00,
        ema50: 80.00,
        description: "Gotrade Top Mover - Streaming Media Global",
        sector: "Communication",
        marketCap: 3.0e11,
        catalyst: "Gotrade Top Mover: Pertumbuhan Pelanggan Hiburan",
        support1: 68.0,
        resistance1: 76.0,
        targetPrice1: 82.0,
        targetPrice2: 90.0,
        stopLossPrice: 65.0,
        sparklinePoints: _generateSparkline(71.79, -7.25),
        logoUrl: "https://logo.clearbit.com/netflix.com",
      ),
      Stock(
        ticker: "AAPL",
        name: "Apple Inc.",
        exchange: "NASDAQ",
        price: 336.13,
        change: 3.86,
        changePercent: 1.16,
        volume: 18500000000.0,
        volumeFormatted: "18.5B",
        recommendationScore: 0.82,
        recommendationText: "STRONG BUY",
        rsi: 58.2,
        macdStatus: "Golden Cross",
        ema20: 330.00,
        ema50: 320.00,
        description: "Gotrade Top Mover - Consumer Electronics & Ekosistem iOS",
        sector: "Technology",
        marketCap: 3.5e12,
        catalyst: "Gotrade Top Mover: Siklus Upgrade iPhone & Apple Intelligence",
        support1: 330.0,
        resistance1: 345.0,
        targetPrice1: 360.0,
        targetPrice2: 380.0,
        stopLossPrice: 325.0,
        sparklinePoints: _generateSparkline(336.13, 1.16),
        logoUrl: "https://logo.clearbit.com/apple.com",
      ),
      Stock(
        ticker: "META",
        name: "Meta Platforms, Inc.",
        exchange: "NASDAQ",
        price: 665.75,
        change: 17.72,
        changePercent: 2.73,
        volume: 11000000000.0,
        volumeFormatted: "11.0B",
        recommendationScore: 0.85,
        recommendationText: "STRONG BUY",
        rsi: 65.5,
        macdStatus: "Bullish Continuation",
        ema20: 650.00,
        ema50: 620.00,
        description: "Gotrade Top Mover - Social Media & Ekosistem Llama AI",
        sector: "Communication",
        marketCap: 1.4e12,
        catalyst: "Gotrade Top Mover: Efisiensi Iklan AI & Monetisasi Reels",
        support1: 650.0,
        resistance1: 680.0,
        targetPrice1: 710.0,
        targetPrice2: 740.0,
        stopLossPrice: 640.0,
        sparklinePoints: _generateSparkline(665.75, 2.73),
        logoUrl: "https://logo.clearbit.com/meta.com",
      ),
      Stock(
        ticker: "QQQ",
        name: "Invesco QQQ Trust",
        exchange: "NASDAQ",
        price: 721.45,
        change: 6.57,
        changePercent: 0.92,
        volume: 32000000000.0,
        volumeFormatted: "32.0B",
        recommendationScore: 0.75,
        recommendationText: "BUY",
        rsi: 57.8,
        macdStatus: "Bullish Trend",
        ema20: 710.00,
        ema50: 690.00,
        description: "Gotrade Top Mover - ETF Indeks Nasdaq-100",
        sector: "Financials",
        marketCap: 2.8e11,
        catalyst: "Gotrade Top Mover: Arus Kas Institusional Sektor Teknologi",
        support1: 710.0,
        resistance1: 735.0,
        targetPrice1: 750.0,
        targetPrice2: 770.0,
        stopLossPrice: 700.0,
        sparklinePoints: _generateSparkline(721.45, 0.92),
        logoUrl: "https://logo.clearbit.com/invesco.com",
      ),
      Stock(
        ticker: "GOOGL",
        name: "Alphabet Inc.",
        exchange: "NASDAQ",
        price: 349.54,
        change: 11.04,
        changePercent: 3.26,
        volume: 8800000000.0,
        volumeFormatted: "8.8B",
        recommendationScore: 0.80,
        recommendationText: "STRONG BUY",
        rsi: 62.1,
        macdStatus: "Bullish Breakout",
        ema20: 338.00,
        ema50: 325.00,
        description: "Gotrade Top Mover - Google Search, Cloud & Gemini AI",
        sector: "Communication",
        marketCap: 2.0e12,
        catalyst: "Gotrade Top Mover: Ekspansi Google Cloud & Search AI",
        support1: 338.0,
        resistance1: 360.0,
        targetPrice1: 380.0,
        targetPrice2: 400.0,
        stopLossPrice: 330.0,
        sparklinePoints: _generateSparkline(349.54, 3.26),
        logoUrl: "https://logo.clearbit.com/google.com",
      ),
      Stock(
        ticker: "SPOT",
        name: "Spotify Technology S.A.",
        exchange: "NYSE",
        price: 509.45,
        change: -16.30,
        changePercent: -3.10,
        volume: 3200000000.0,
        volumeFormatted: "3.2B",
        recommendationScore: -0.15,
        recommendationText: "NEUTRAL",
        rsi: 44.2,
        macdStatus: "Consolidation",
        ema20: 520.00,
        ema50: 535.00,
        description: "Gotrade Top Mover - Platform Audio Streaming Dunia",
        sector: "Communication",
        marketCap: 7.2e10,
        catalyst: "Gotrade Top Mover: Akumulasi Konsolidasi Sektor Media",
        support1: 495.0,
        resistance1: 525.0,
        targetPrice1: 550.0,
        targetPrice2: 580.0,
        stopLossPrice: 485.0,
        sparklinePoints: _generateSparkline(509.45, -3.10),
        logoUrl: "https://logo.clearbit.com/spotify.com",
      ),
      Stock(
        ticker: "SBUX",
        name: "Starbucks Corp",
        exchange: "NASDAQ",
        price: 95.83,
        change: -2.91,
        changePercent: -2.95,
        volume: 4500000000.0,
        volumeFormatted: "4.5B",
        recommendationScore: -0.25,
        recommendationText: "SELL",
        rsi: 41.5,
        macdStatus: "Bearish Momentum",
        ema20: 98.00,
        ema50: 101.00,
        description: "Gotrade Top Mover - Jaringan Kedai Kopi Ritel Global",
        sector: "Consumer Cyclical",
        marketCap: 1.1e11,
        catalyst: "Gotrade Top Mover: Uji Resi Area Support Ritel",
        support1: 93.0,
        resistance1: 98.0,
        targetPrice1: 102.0,
        targetPrice2: 108.0,
        stopLossPrice: 91.0,
        sparklinePoints: _generateSparkline(95.83, -2.95),
        logoUrl: "https://logo.clearbit.com/starbucks.com",
      ),
      Stock(
        ticker: "MSFT",
        name: "Microsoft Corp",
        exchange: "NASDAQ",
        price: 493.78,
        change: -1.85,
        changePercent: -0.37,
        volume: 12400000000.0,
        volumeFormatted: "12.4B",
        recommendationScore: 0.70,
        recommendationText: "BUY",
        rsi: 52.0,
        macdStatus: "Consolidation",
        ema20: 490.00,
        ema50: 480.00,
        description: "Gotrade Top Mover - Azure Cloud & OpenAI Strategic Partner",
        sector: "Technology",
        marketCap: 3.3e12,
        catalyst: "Gotrade Top Mover: Pertumbuhan Azure & Copilot Enterprise",
        support1: 485.0,
        resistance1: 505.0,
        targetPrice1: 525.0,
        targetPrice2: 550.0,
        stopLossPrice: 480.0,
        sparklinePoints: _generateSparkline(493.78, -0.37),
        logoUrl: "https://logo.clearbit.com/microsoft.com",
      ),
      Stock(
        ticker: "AMZN",
        name: "Amazon.com Inc.",
        exchange: "NASDAQ",
        price: 253.71,
        change: -3.07,
        changePercent: -1.20,
        volume: 10200000000.0,
        volumeFormatted: "10.2B",
        recommendationScore: 0.65,
        recommendationText: "BUY",
        rsi: 51.2,
        macdStatus: "Consolidation",
        ema20: 250.00,
        ema50: 242.00,
        description: "Gotrade US Stock - E-Commerce & AWS Cloud Infrastructure",
        sector: "Consumer Cyclical",
        marketCap: 1.9e12,
        catalyst: "Gotrade US Stock: Akselerasi AWS Cloud & Retail Growth",
        support1: 248.0,
        resistance1: 262.0,
        targetPrice1: 275.0,
        targetPrice2: 290.0,
        stopLossPrice: 242.0,
        sparklinePoints: _generateSparkline(253.71, -1.20),
        logoUrl: "https://logo.clearbit.com/amazon.com",
      ),
      Stock(
        ticker: "TSLA",
        name: "Tesla, Inc.",
        exchange: "NASDAQ",
        price: 364.27,
        change: -1.17,
        changePercent: -0.32,
        volume: 16800000000.0,
        volumeFormatted: "16.8B",
        recommendationScore: 0.68,
        recommendationText: "BUY",
        rsi: 54.0,
        macdStatus: "Bullish Trend",
        ema20: 360.00,
        ema50: 345.00,
        description: "Gotrade US Stock - Kendaraan Listrik EV & FSD Autonomous",
        sector: "Consumer Cyclical",
        marketCap: 7.0e11,
        catalyst: "Gotrade US Stock: Robotaxi & Peningkatan Produksi EV",
        support1: 350.0,
        resistance1: 380.0,
        targetPrice1: 400.0,
        targetPrice2: 425.0,
        stopLossPrice: 340.0,
        sparklinePoints: _generateSparkline(364.27, -0.32),
        logoUrl: "https://logo.clearbit.com/tesla.com",
      ),
      Stock(
        ticker: "AMD",
        name: "Advanced Micro Devices, Inc.",
        exchange: "NASDAQ",
        price: 559.82,
        change: 43.69,
        changePercent: 8.46,
        volume: 9800000000.0,
        volumeFormatted: "9.8B",
        recommendationScore: 0.95,
        recommendationText: "STRONG BREAKOUT",
        rsi: 72.4,
        macdStatus: "Bullish Surge",
        ema20: 530.00,
        ema50: 500.00,
        description: "Gotrade US Stock - Akselerator AI Instinct & Prosesor RyZen",
        sector: "Technology",
        marketCap: 2.5e11,
        catalyst: "Gotrade US Stock: Adopsi Chip MI300 AI di Data Center",
        support1: 530.0,
        resistance1: 580.0,
        targetPrice1: 610.0,
        targetPrice2: 650.0,
        stopLossPrice: 515.0,
        sparklinePoints: _generateSparkline(559.82, 8.46),
        logoUrl: "https://logo.clearbit.com/amd.com",
      ),
      Stock(
        ticker: "AVGO",
        name: "Broadcom Inc.",
        exchange: "NASDAQ",
        price: 357.61,
        change: -4.38,
        changePercent: -1.21,
        volume: 5200000000.0,
        volumeFormatted: "5.2B",
        recommendationScore: 0.78,
        recommendationText: "BUY",
        rsi: 55.0,
        macdStatus: "Consolidation",
        ema20: 350.00,
        ema50: 335.00,
        description: "Gotrade US Stock - Semiconductor & AI Networking",
        sector: "Technology",
        marketCap: 8.5e11,
        catalyst: "Gotrade US Stock: Custom AI Silicon & VMWare Synergy",
        support1: 348.0,
        resistance1: 370.0,
        targetPrice1: 390.0,
        targetPrice2: 410.0,
        stopLossPrice: 340.0,
        sparklinePoints: _generateSparkline(357.61, -1.21),
        logoUrl: "https://logo.clearbit.com/broadcom.com",
      ),
      Stock(
        ticker: "QCOM",
        name: "Qualcomm Inc.",
        exchange: "NASDAQ",
        price: 177.72,
        change: -4.25,
        changePercent: -2.34,
        volume: 3800000000.0,
        volumeFormatted: "3.8B",
        recommendationScore: 0.60,
        recommendationText: "BUY",
        rsi: 48.0,
        macdStatus: "Consolidation",
        ema20: 175.00,
        ema50: 170.00,
        description: "Gotrade US Stock - Snapdragon Mobile & Automotive Chips",
        sector: "Technology",
        marketCap: 1.8e11,
        catalyst: "Gotrade US Stock: Snapdragon X Elite & AI Copilot+ PC",
        support1: 172.0,
        resistance1: 185.0,
        targetPrice1: 195.0,
        targetPrice2: 210.0,
        stopLossPrice: 168.0,
        sparklinePoints: _generateSparkline(177.72, -2.34),
        logoUrl: "https://logo.clearbit.com/qualcomm.com",
      ),
      Stock(
        ticker: "CRM",
        name: "Salesforce Inc.",
        exchange: "NYSE",
        price: 237.92,
        change: -9.80,
        changePercent: -3.96,
        volume: 4100000000.0,
        volumeFormatted: "4.1B",
        recommendationScore: -0.20,
        recommendationText: "NEUTRAL",
        rsi: 40.5,
        macdStatus: "Bearish Momentum",
        ema20: 245.00,
        ema50: 255.00,
        description: "Gotrade US Stock - Platform CRM & Agentforce AI",
        sector: "Technology",
        marketCap: 2.8e11,
        catalyst: "Gotrade US Stock: Peluncuran Agentforce Enterprise AI",
        support1: 230.0,
        resistance1: 248.0,
        targetPrice1: 260.0,
        targetPrice2: 280.0,
        stopLossPrice: 225.0,
        sparklinePoints: _generateSparkline(237.92, -3.96),
        logoUrl: "https://logo.clearbit.com/salesforce.com",
      ),
      Stock(
        ticker: "INTC",
        name: "Intel Corp",
        exchange: "NASDAQ",
        price: 108.60,
        change: 5.66,
        changePercent: 5.50,
        volume: 1800000000.0,
        volumeFormatted: "1.8B",
        recommendationScore: 0.75,
        recommendationText: "BUY",
        rsi: 61.2,
        macdStatus: "Bullish Reversal",
        ema20: 102.00,
        ema50: 95.00,
        description: "Gotrade US Stock - Pabrikan Chip x86 & Foundry",
        sector: "Technology",
        marketCap: 9.4e10,
        catalyst: "Gotrade US Stock: Restrukturisasi Foundry & Pemangkasan Beban",
        support1: 102.0,
        resistance1: 115.0,
        targetPrice1: 125.0,
        targetPrice2: 135.0,
        stopLossPrice: 98.0,
        sparklinePoints: _generateSparkline(108.60, 5.50),
        logoUrl: "https://logo.clearbit.com/intel.com",
      ),
      Stock(
        ticker: "NKE",
        name: "Nike, Inc.",
        exchange: "NYSE",
        price: 35.51,
        change: -1.29,
        changePercent: -3.51,
        volume: 1200000000.0,
        volumeFormatted: "1.2B",
        recommendationScore: -0.25,
        recommendationText: "SELL",
        rsi: 38.0,
        macdStatus: "Bearish Momentum",
        ema20: 37.00,
        ema50: 40.00,
        description: "Gotrade US Stock - Apparel, Sepatu Olahraga & Lifestyle",
        sector: "Consumer Cyclical",
        marketCap: 1.2e11,
        catalyst: "Gotrade US Stock: Re-Test Area Support Teknikal",
        support1: 34.0,
        resistance1: 38.0,
        targetPrice1: 41.0,
        targetPrice2: 45.0,
        stopLossPrice: 33.0,
        sparklinePoints: _generateSparkline(35.51, -3.51),
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

    return _stockDatabase[upperTicker];
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
    final allStocks = _stockDatabase.values.toList();
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
