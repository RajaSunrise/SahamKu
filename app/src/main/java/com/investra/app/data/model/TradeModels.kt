package com.investra.app.data.model

data class TechnicalAnalysis(
    val ticker: String,
    val consensusText: String = "STRONG BUY",
    val winRateAccuracy: Double = 86.4,
    val bullishCount: Int = 16,
    val neutralCount: Int = 4,
    val bearishCount: Int = 1,
    val rsiValue: Double = 62.4,
    val rsiStatus: String = "Zona Akumulasi Sehat",
    val macdStatus: String = "Bullish Crossover",
    val emaStatus: String = "Golden Cross EMA 20/50",
    val riskRewardRatio: Double = 2.69,
    val targetPrice1: Double = 152.00,
    val targetPrice2: Double = 160.00,
    val stopLossPrice: Double = 136.00
)

data class Position(
    val ticker: String,
    val name: String,
    val exchange: String,
    val shares: Double,
    val avgBuyPrice: Double,
    val currentPrice: Double,
    val stopLoss: Double? = null,
    val takeProfit: Double? = null,
    val logoUrl: String? = null
) {
    val totalInvestment: Double get() = shares * avgBuyPrice
    val currentMarketValue: Double get() = shares * currentPrice
    val floatingPnL: Double get() = currentMarketValue - totalInvestment
    val floatingPnLPercent: Double get() = if (totalInvestment > 0) (floatingPnL / totalInvestment) * 100 else 0.0
}

data class TradeHistory(
    val id: String,
    val ticker: String,
    val name: String,
    val exchange: String,
    val type: String, // "BUY" or "SELL"
    val shares: Double,
    val price: Double,
    val realizedPnL: Double = 0.0,
    val realizedPnLPercent: Double = 0.0,
    val dateText: String,
    val reasonText: String = "Jual Realisasi Breakout",
    val isWin: Boolean = true,
    val logoUrl: String? = null
)

data class CalculatorState(
    val ticker: String = "NVDA",
    val entryPrice: Double = 142.50,
    val shares: Double = 100.0,
    val targetPrice: Double = 162.00,
    val stopLossPrice: Double = 135.00
) {
    val totalCapital: Double get() = entryPrice * shares
    val potentialProfit: Double get() = (targetPrice - entryPrice) * shares
    val potentialProfitPct: Double get() = if (entryPrice > 0) ((targetPrice - entryPrice) / entryPrice) * 100 else 0.0
    val potentialLoss: Double get() = (entryPrice - stopLossPrice) * shares
    val potentialLossPct: Double get() = if (entryPrice > 0) ((entryPrice - stopLossPrice) / entryPrice) * 100 else 0.0
    val riskRewardMultiplier: Double get() = if (potentialLoss > 0) potentialProfit / potentialLoss else 0.0
}

data class ScreenerFilter(
    val preset: String = "Breakout 52W High",
    val selectedMegaCap: Boolean = true,
    val selectedLargeCap: Boolean = true,
    val selectedSectors: List<String> = listOf("Technology", "Healthcare", "Energy")
)
