class Stock {
  final String ticker;
  final String name;
  final String exchange;
  final double price;
  final double change;
  final double changePercent;
  final double volume;
  final String volumeFormatted;
  final double recommendationScore;
  final String recommendationText;
  final double rsi;
  final String macdStatus;
  final double ema20;
  final double ema50;
  final String description;
  final String sector;
  final double marketCap;
  final String catalyst;
  final double support1;
  final double resistance1;
  final double targetPrice1;
  final double targetPrice2;
  final double stopLossPrice;
  final List<double> sparklinePoints;
  final String logoUrl;

  Stock({
    required this.ticker,
    required this.name,
    this.exchange = 'NASDAQ',
    required this.price,
    required this.change,
    required this.changePercent,
    required this.volume,
    this.volumeFormatted = '1.0B',
    this.recommendationScore = 0.75,
    this.recommendationText = 'BUY',
    this.rsi = 55.0,
    this.macdStatus = 'Bullish',
    this.ema20 = 100.0,
    this.ema50 = 95.0,
    this.description = '',
    this.sector = 'Technology',
    this.marketCap = 1e11,
    this.catalyst = 'High activity in US stock market',
    this.support1 = 90.0,
    this.resistance1 = 110.0,
    this.targetPrice1 = 115.0,
    this.targetPrice2 = 125.0,
    this.stopLossPrice = 85.0,
    this.sparklinePoints = const [],
    this.logoUrl = '',
  });

  Stock copyWith({
    String? ticker,
    String? name,
    String? exchange,
    double? price,
    double? change,
    double? changePercent,
    double? volume,
    String? volumeFormatted,
    double? recommendationScore,
    String? recommendationText,
    double? rsi,
    String? macdStatus,
    double? ema20,
    double? ema50,
    String? description,
    String? sector,
    double? marketCap,
    String? catalyst,
    double? support1,
    double? resistance1,
    double? targetPrice1,
    double? targetPrice2,
    double? stopLossPrice,
    List<double>? sparklinePoints,
    String? logoUrl,
  }) {
    return Stock(
      ticker: ticker ?? this.ticker,
      name: name ?? this.name,
      exchange: exchange ?? this.exchange,
      price: price ?? this.price,
      change: change ?? this.change,
      changePercent: changePercent ?? this.changePercent,
      volume: volume ?? this.volume,
      volumeFormatted: volumeFormatted ?? this.volumeFormatted,
      recommendationScore: recommendationScore ?? this.recommendationScore,
      recommendationText: recommendationText ?? this.recommendationText,
      rsi: rsi ?? this.rsi,
      macdStatus: macdStatus ?? this.macdStatus,
      ema20: ema20 ?? this.ema20,
      ema50: ema50 ?? this.ema50,
      description: description ?? this.description,
      sector: sector ?? this.sector,
      marketCap: marketCap ?? this.marketCap,
      catalyst: catalyst ?? this.catalyst,
      support1: support1 ?? this.support1,
      resistance1: resistance1 ?? this.resistance1,
      targetPrice1: targetPrice1 ?? this.targetPrice1,
      targetPrice2: targetPrice2 ?? this.targetPrice2,
      stopLossPrice: stopLossPrice ?? this.stopLossPrice,
      sparklinePoints: sparklinePoints ?? this.sparklinePoints,
      logoUrl: logoUrl ?? this.logoUrl,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'ticker': ticker,
      'name': name,
      'exchange': exchange,
      'price': price,
      'change': change,
      'changePercent': changePercent,
      'volume': volume,
      'logoUrl': logoUrl,
    };
  }
}
