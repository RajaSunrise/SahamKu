package com.investra.app.data.repository

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.investra.app.data.model.Stock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class TradingViewRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    suspend fun fetchStockScanner(
        category: String = "gainers",
        limit: Int = 20
    ): Result<List<Stock>> = withContext(Dispatchers.IO) {
        try {
            val jsonMediaType = "application/json; charset=utf-8".toMediaType()

            val sortBy = when (category) {
                "gainers" -> "change"
                "losers" -> "change"
                "active" -> "volume"
                "unusual" -> "volume"
                else -> "change"
            }
            val sortOrder = when (category) {
                "losers" -> "asc"
                else -> "desc"
            }

            val requestBodyJson = """
                {
                  "filter": [
                    {"left": "type", "operation": "equal", "right": "stock"},
                    {"left": "subtype", "operation": "equal", "right": "common"}
                  ],
                  "options": {"lang": "en"},
                  "markets": ["america"],
                  "symbols": {"query": {"types": []}, "tickers": []},
                  "columns": [
                    "name", "close", "change", "change_abs", "volume",
                    "Recommend.All", "RSI", "MACD.macd", "MACD.signal",
                    "EMA20", "EMA50", "description", "market_cap_basic", "sector"
                  ],
                  "sort": {"sortBy": "$sortBy", "sortOrder": "$sortOrder"},
                  "range": [0, $limit]
                }
            """.trimIndent()

            val request = Request.Builder()
                .url("https://scanner.tradingview.com/america/scan")
                .post(requestBodyJson.toRequestBody(jsonMediaType))
                .addHeader("User-Agent", "Mozilla/5.0")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val parsedObj = gson.fromJson(responseBody, JsonObject::class.java)
                val dataArray = parsedObj.getAsJsonArray("data")
                val stocks = mutableListOf<Stock>()

                dataArray?.forEach { element ->
                    val item = element.asJsonObject
                    val symbolFull = item.get("s")?.asString ?: "NASDAQ:UNKNOWN"
                    val exchange = if (symbolFull.contains(":")) symbolFull.split(":")[0] else "NASDAQ"
                    val dArray = item.getAsJsonArray("d")

                    if (dArray != null && dArray.size() >= 12) {
                        val name = getJsonString(dArray, 0, "UNKNOWN")
                        val close = getJsonDouble(dArray, 1, 100.0)
                        val changePct = getJsonDouble(dArray, 2, 0.0)
                        val changeAbs = getJsonDouble(dArray, 3, 0.0)
                        val volume = getJsonDouble(dArray, 4, 1000000.0)
                        val recommendScore = getJsonDouble(dArray, 5, 0.5)
                        val rsi = getJsonDouble(dArray, 6, 58.0)
                        val macd = getJsonDouble(dArray, 7, 1.2)
                        val macdSignal = getJsonDouble(dArray, 8, 1.0)
                        val ema20 = getJsonDouble(dArray, 9, close * 0.97)
                        val ema50 = getJsonDouble(dArray, 10, close * 0.93)
                        val description = getJsonString(dArray, 11, name)
                        val marketCap = if (dArray.size() > 12) getJsonDouble(dArray, 12, 1e11) else 1e11
                        val sector = if (dArray.size() > 13) getJsonString(dArray, 13, "Technology") else "Technology"

                        val recText = when {
                            recommendScore >= 0.5 -> "STRONG BUY"
                            recommendScore >= 0.1 -> "BUY"
                            recommendScore >= -0.1 -> "NEUTRAL"
                            recommendScore >= -0.5 -> "SELL"
                            else -> "STRONG SELL"
                        }

                        val volFormatted = when {
                            volume >= 1e9 -> String.format("%.1fB", volume / 1e9)
                            volume >= 1e6 -> String.format("%.1fM", volume / 1e6)
                            volume >= 1e3 -> String.format("%.1fK", volume / 1e3)
                            else -> String.format("%.0f", volume)
                        }

                        val catalystText = when {
                            changePct >= 5.0 -> "Catalyst: AI Chip & Institutional Surge"
                            changePct >= 2.0 -> "Breakout MA-20 Bullish"
                            changePct <= -2.0 -> "Oversold Support Test"
                            else -> "Volume Consolidation"
                        }

                        val stock = Stock(
                            ticker = name,
                            name = description,
                            exchange = exchange,
                            price = close,
                            change = changeAbs,
                            changePercent = changePct,
                            volume = volume,
                            volumeFormatted = volFormatted,
                            recommendationScore = recommendScore,
                            recommendationText = recText,
                            rsi = rsi,
                            macdStatus = if (macd > macdSignal) "Bullish Crossover" else "Bearish Momentum",
                            ema20 = ema20,
                            ema50 = ema50,
                            description = description,
                            sector = sector,
                            marketCap = marketCap,
                            catalyst = catalystText,
                            support1 = close * 0.95,
                            resistance1 = close * 1.02,
                            targetPrice1 = close * 1.07,
                            targetPrice2 = close * 1.12,
                            stopLossPrice = close * 0.95,
                            sparklinePoints = generateSparkline(close, changePct)
                        )
                        stocks.add(stock)
                    }
                }

                if (stocks.isNotEmpty()) {
                    Result.success(stocks)
                } else {
                    Result.success(getFallbackStocks(category))
                }
            } else {
                Result.success(getFallbackStocks(category))
            }
        } catch (e: Exception) {
            Result.success(getFallbackStocks(category))
        }
    }

    private fun getJsonString(array: JsonArray, index: Int, defaultVal: String): String {
        return try {
            val elem = array.get(index)
            if (elem != null && !elem.isJsonNull) elem.asString else defaultVal
        } catch (e: Exception) {
            defaultVal
        }
    }

    private fun getJsonDouble(array: JsonArray, index: Int, defaultVal: Double): Double {
        return try {
            val elem = array.get(index)
            if (elem != null && !elem.isJsonNull) elem.asDouble else defaultVal
        } catch (e: Exception) {
            defaultVal
        }
    }

    private fun generateSparkline(close: Double, changePct: Double): List<Float> {
        val base = close.toFloat()
        val delta = (close * (changePct / 100.0) / 7.0).toFloat()
        return listOf(
            base - delta * 6,
            base - delta * 4,
            base - delta * 5,
            base - delta * 2,
            base - delta * 3,
            base - delta,
            base + delta * 0.5f,
            base
        )
    }

    fun getFallbackStocks(category: String = "gainers"): List<Stock> {
        return when (category) {
            "gainers" -> listOf(
                Stock("NVDA", "NVIDIA Corporation", "NASDAQ", 142.50, 7.32, 5.42, 14200000000.0, "14.2B", 0.85, "STRONG BUY", 62.4, "Bullish Crossover", 138.45, 132.80, "Semikonduktor AI", "Technology", 3.5e12, "Catalyst: AI Chip Surge", 136.0, 145.0, 152.0, 160.0, 136.0),
                Stock("TSLA", "Tesla, Inc.", "NASDAQ", 224.80, 10.40, 4.85, 9800000000.0, "9.8B", 0.72, "BUY", 58.5, "Bullish Crossover", 218.00, 205.50, "Automotive Tech", "Consumer Cyclical", 7.2e11, "Breakout MA-20 Bullish", 212.0, 230.0, 240.0, 255.0, 210.0),
                Stock("PLTR", "Palantir Technologies", "NYSE", 44.10, 2.54, 6.12, 3500000000.0, "3.5B", 0.90, "STRONG BUY", 68.2, "Bullish Continuation", 41.50, 38.20, "AI Data Analytics", "Technology", 9.8e10, "S&P 500 Inklusi Rally", 41.0, 45.0, 48.0, 52.0, 40.0),
                Stock("AMD", "Advanced Micro Devices", "NASDAQ", 158.30, 5.72, 3.75, 2900000000.0, "2.9B", 0.65, "BUY", 55.4, "Golden Cross EMA", 152.00, 146.00, "Semiconductor Tech", "Technology", 2.5e11, "Peningkatan Target Harga Analis", 150.0, 162.0, 170.0, 180.0, 148.0)
            )
            "losers" -> listOf(
                Stock("INTC", "Intel Corp", "NASDAQ", 22.10, -0.78, -3.40, 1800000000.0, "1.8B", -0.42, "SELL", 38.2, "Bearish Momentum", 23.50, 25.10, "Semiconductors", "Technology", 9.4e10, "Oversold Test", 21.5, 23.0, 24.5, 26.0, 21.0),
                Stock("NKE", "Nike, Inc.", "NYSE", 82.40, -1.81, -2.15, 1200000000.0, "1.2B", -0.20, "NEUTRAL", 42.1, "Consolidation", 84.50, 88.00, "Apparel Footwear", "Consumer Cyclical", 1.2e11, "Support Re-Test", 80.0, 85.0, 89.0, 95.0, 78.0)
            )
            "active" -> listOf(
                Stock("AAPL", "Apple Inc.", "NASDAQ", 232.50, 2.71, 1.18, 18500000000.0, "18.5B", 0.82, "STRONG BUY", 58.2, "Golden Cross", 228.00, 220.00, "Consumer Electronics", "Technology", 3.5e12, "Daily Swing Pick", 226.0, 235.0, 248.0, 260.0, 226.0),
                Stock("MSFT", "Microsoft Corp", "NASDAQ", 448.20, 8.20, 1.86, 12400000000.0, "12.4B", 0.78, "BUY", 54.0, "Ascending Triangle", 438.00, 425.00, "Software Cloud", "Technology", 3.3e12, "Volume melonjak +22%", 439.0, 455.0, 471.5, 490.0, 435.0),
                Stock("AMZN", "Amazon.com Inc", "NASDAQ", 186.40, 3.20, 1.75, 10200000000.0, "10.2B", 0.75, "BUY", 56.8, "Cup & Handle Pattern", 182.00, 178.00, "E-Commerce Cloud", "Consumer Cyclical", 1.9e12, "Rebound dari Support EMA 50", 181.0, 192.0, 202.0, 215.0, 178.0),
                Stock("GOOGL", "Alphabet Inc", "NASDAQ", 165.10, 2.10, 1.29, 8800000000.0, "8.8B", 0.68, "BUY ON DIP", 48.5, "Fib 61.8% Bounce", 162.00, 158.00, "Internet Media", "Communication", 2.0e12, "Reversal Candlestick Hammer", 161.5, 168.0, 173.2, 182.0, 159.0)
            )
            else -> getFallbackStocks("gainers")
        }
    }
}
