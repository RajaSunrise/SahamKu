class TradeOrder {
  final int? id;
  final String ticker;
  final String type; // BUY or SELL
  final double shares;
  final double price;
  final double totalAmount;
  final String timestamp;

  TradeOrder({
    this.id,
    required this.ticker,
    required this.type,
    required this.shares,
    required this.price,
    required this.totalAmount,
    required this.timestamp,
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'ticker': ticker,
      'type': type,
      'shares': shares,
      'price': price,
      'totalAmount': totalAmount,
      'timestamp': timestamp,
    };
  }

  factory TradeOrder.fromMap(Map<String, dynamic> map) {
    return TradeOrder(
      id: map['id'] as int?,
      ticker: map['ticker'] as String,
      type: map['type'] as String,
      shares: (map['shares'] as num).toDouble(),
      price: (map['price'] as num).toDouble(),
      totalAmount: (map['totalAmount'] as num).toDouble(),
      timestamp: map['timestamp'] as String,
    );
  }
}

class PortfolioPosition {
  final String ticker;
  final double shares;
  final double avgBuyPrice;

  PortfolioPosition({
    required this.ticker,
    required this.shares,
    required this.avgBuyPrice,
  });

  Map<String, dynamic> toMap() {
    return {
      'ticker': ticker,
      'shares': shares,
      'avgBuyPrice': avgBuyPrice,
    };
  }

  factory PortfolioPosition.fromMap(Map<String, dynamic> map) {
    return PortfolioPosition(
      ticker: map['ticker'] as String,
      shares: (map['shares'] as num).toDouble(),
      avgBuyPrice: (map['avgBuyPrice'] as num).toDouble(),
    );
  }
}

class WatchlistItem {
  final String ticker;
  final String addedAt;

  WatchlistItem({
    required this.ticker,
    required this.addedAt,
  });

  Map<String, dynamic> toMap() {
    return {
      'ticker': ticker,
      'addedAt': addedAt,
    };
  }

  factory WatchlistItem.fromMap(Map<String, dynamic> map) {
    return WatchlistItem(
      ticker: map['ticker'] as String,
      addedAt: map['addedAt'] as String,
    );
  }
}
