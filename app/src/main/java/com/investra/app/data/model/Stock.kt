package com.investra.app.data.model

data class Stock(
    val ticker: String,
    val name: String,
    val exchange: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val volume: Double,
    val volumeFormatted: String,
    val recommendationScore: Double = 0.5, // 0.5 to 1.0 Strong Buy, -0.5 to 0.5 Neutral, etc.
    val recommendationText: String = "STRONG BUY",
    val rsi: Double = 58.2,
    val macdStatus: String = "Bullish Crossover",
    val ema20: Double = price * 0.97,
    val ema50: Double = price * 0.93,
    val description: String = "",
    val sector: String = "Technology",
    val marketCap: Double = 0.0,
    val catalyst: String = "Breakout MA-20 Bullish",
    val support1: Double = price * 0.95,
    val resistance1: Double = price * 1.02,
    val targetPrice1: Double = price * 1.07,
    val targetPrice2: Double = price * 1.12,
    val stopLossPrice: Double = price * 0.95,
    val sparklinePoints: List<Float> = listOf(10f, 12f, 11f, 15f, 18f, 22f, 25f, 28f),
    val logoUrl: String? = null
)
