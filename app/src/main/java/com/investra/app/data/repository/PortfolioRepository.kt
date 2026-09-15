package com.investra.app.data.repository

import com.investra.app.data.db.DatabaseHelper
import com.investra.app.data.model.Position
import com.investra.app.data.model.Stock
import com.investra.app.data.model.TradeHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PortfolioRepository(private val dbHelper: DatabaseHelper? = null) {

    private val _virtualCash: MutableStateFlow<Double>
    val virtualCash: StateFlow<Double>

    private val _initialCapital: MutableStateFlow<Double>
    val initialCapital: StateFlow<Double>

    private val _positions: MutableStateFlow<List<Position>>
    val positions: StateFlow<List<Position>>

    private val _watchlist: MutableStateFlow<Set<String>>
    val watchlist: StateFlow<Set<String>>

    private val _tradeHistory: MutableStateFlow<List<TradeHistory>>
    val tradeHistory: StateFlow<List<TradeHistory>>

    init {
        if (dbHelper != null) {
            val (cash, initial) = dbHelper.getAccountInfo()
            _virtualCash = MutableStateFlow(cash)
            virtualCash = _virtualCash.asStateFlow()

            _initialCapital = MutableStateFlow(initial)
            initialCapital = _initialCapital.asStateFlow()

            _positions = MutableStateFlow(dbHelper.getPositions())
            positions = _positions.asStateFlow()

            _watchlist = MutableStateFlow(dbHelper.getWatchlist())
            watchlist = _watchlist.asStateFlow()

            _tradeHistory = MutableStateFlow(dbHelper.getTradeHistory())
            tradeHistory = _tradeHistory.asStateFlow()
        } else {
            _virtualCash = MutableStateFlow(42150.00)
            virtualCash = _virtualCash.asStateFlow()

            _initialCapital = MutableStateFlow(100000.00)
            initialCapital = _initialCapital.asStateFlow()

            _positions = MutableStateFlow(
                listOf(
                    Position("NVDA", "NVIDIA Corporation", "NASDAQ", 150, 132.00, 142.50, stopLoss = 125.0, takeProfit = 152.0),
                    Position("AAPL", "Apple Inc.", "NASDAQ", 100, 220.00, 232.50, stopLoss = 210.0, takeProfit = 248.0),
                    Position("TSLA", "Tesla, Inc.", "NASDAQ", 80, 212.00, 224.80, stopLoss = 200.0, takeProfit = 240.0),
                    Position("INTC", "Intel Corp", "NASDAQ", 200, 23.20, 22.10, stopLoss = 21.80, takeProfit = 26.0)
                )
            )
            positions = _positions.asStateFlow()

            _watchlist = MutableStateFlow(
                setOf("MSFT", "AMZN", "META", "GOOGL", "AMD", "BRK.B")
            )
            watchlist = _watchlist.asStateFlow()

            _tradeHistory = MutableStateFlow(
                listOf(
                    TradeHistory("1", "PLTR", "Palantir Tech", "NYSE", "SELL", 100, 44.10, 3420.00, 15.4, "Hari Ini, 14:22", "Jual Realisasi Breakout", true),
                    TradeHistory("2", "MSFT", "Microsoft Corp", "NASDAQ", "SELL", 50, 448.20, 2150.00, 8.6, "Kemarin, 21:10", "Jual Realisasi TP2", true),
                    TradeHistory("3", "AMZN", "Amazon.com Inc", "NASDAQ", "SELL", 80, 186.40, 1680.00, 6.2, "3 Okt, 19:45", "Jual Realisasi TP1", true),
                    TradeHistory("4", "AMD", "Advanced Micro", "NASDAQ", "SELL", 60, 158.30, -340.00, -2.8, "1 Okt, 16:05", "Jual Cut Loss Terproteksi", false)
                )
            )
            tradeHistory = _tradeHistory.asStateFlow()
        }
    }

    fun buyStock(stock: Stock, shares: Int, orderType: String = "Market Order", stopLoss: Double? = null, takeProfit: Double? = null): Boolean {
        val cost = stock.price * shares
        if (_virtualCash.value >= cost && shares > 0) {
            _virtualCash.value -= cost
            dbHelper?.updateVirtualCash(_virtualCash.value)

            val currentList = _positions.value.toMutableList()
            val existingIndex = currentList.indexOfFirst { it.ticker == stock.ticker }

            val updatedPos: Position
            if (existingIndex >= 0) {
                val existing = currentList[existingIndex]
                val totalShares = existing.shares + shares
                val avgPrice = ((existing.shares * existing.avgBuyPrice) + (shares * stock.price)) / totalShares
                updatedPos = existing.copy(
                    shares = totalShares,
                    avgBuyPrice = avgPrice,
                    currentPrice = stock.price,
                    stopLoss = stopLoss ?: existing.stopLoss,
                    takeProfit = takeProfit ?: existing.takeProfit
                )
                currentList[existingIndex] = updatedPos
            } else {
                updatedPos = Position(
                    ticker = stock.ticker,
                    name = stock.name,
                    exchange = stock.exchange,
                    shares = shares,
                    avgBuyPrice = stock.price,
                    currentPrice = stock.price,
                    stopLoss = stopLoss,
                    takeProfit = takeProfit
                )
                currentList.add(updatedPos)
            }
            dbHelper?.savePosition(updatedPos)
            _positions.value = currentList
            return true
        }
        return false
    }

    fun sellStock(ticker: String, sharesToSell: Int): Boolean {
        val currentList = _positions.value.toMutableList()
        val index = currentList.indexOfFirst { it.ticker == ticker }
        if (index >= 0) {
            val position = currentList[index]
            val actualSellShares = Math.min(sharesToSell, position.shares)
            val grossProceeds = actualSellShares * position.currentPrice
            val costBasis = actualSellShares * position.avgBuyPrice
            val pnl = grossProceeds - costBasis
            val pnlPct = if (costBasis > 0) (pnl / costBasis) * 100 else 0.0

            _virtualCash.value += grossProceeds
            dbHelper?.updateVirtualCash(_virtualCash.value)

            if (actualSellShares >= position.shares) {
                currentList.removeAt(index)
                dbHelper?.deletePosition(ticker)
            } else {
                val updated = position.copy(shares = position.shares - actualSellShares)
                currentList[index] = updated
                dbHelper?.savePosition(updated)
            }
            _positions.value = currentList

            val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
            val dateStr = dateFormat.format(Date())
            val history = TradeHistory(
                id = System.currentTimeMillis().toString(),
                ticker = position.ticker,
                name = position.name,
                exchange = position.exchange,
                type = "SELL",
                shares = actualSellShares,
                price = position.currentPrice,
                realizedPnL = pnl,
                realizedPnLPercent = pnlPct,
                dateText = dateStr,
                reasonText = if (pnl >= 0) "Jual Realisasi Profit Demo" else "Jual Stop Loss Demo",
                isWin = pnl >= 0
            )
            _tradeHistory.value = listOf(history) + _tradeHistory.value
            dbHelper?.addTradeHistory(history)
            return true
        }
        return false
    }

    fun toggleWatchlist(ticker: String) {
        val current = _watchlist.value.toMutableSet()
        if (current.contains(ticker)) {
            current.remove(ticker)
            dbHelper?.removeWatchlist(ticker)
        } else {
            current.add(ticker)
            dbHelper?.addWatchlist(ticker)
        }
        _watchlist.value = current
    }

    fun updatePositionPrices(priceMap: Map<String, Double>) {
        if (priceMap.isEmpty()) return
        val currentList = _positions.value.toMutableList()
        var updated = false

        for (i in currentList.indices) {
            val pos = currentList[i]
            val newPrice = priceMap[pos.ticker]
            if (newPrice != null && newPrice != pos.currentPrice) {
                val updatedPos = pos.copy(currentPrice = newPrice)
                currentList[i] = updatedPos
                dbHelper?.savePosition(updatedPos)
                updated = true
            }
        }

        if (updated) {
            _positions.value = currentList
        }
    }

    fun resetPortfolio(newCapital: Double) {
        _initialCapital.value = newCapital
        _virtualCash.value = newCapital
        _positions.value = emptyList()
        _tradeHistory.value = emptyList()
        dbHelper?.resetAccount(newCapital)
    }
}
