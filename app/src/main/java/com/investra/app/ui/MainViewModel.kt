package com.investra.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.investra.app.data.db.DatabaseHelper
import com.investra.app.data.model.CalculatorState
import com.investra.app.data.model.Position
import com.investra.app.data.model.ScreenerFilter
import com.investra.app.data.model.Stock
import com.investra.app.data.model.TradeHistory
import com.investra.app.data.repository.PortfolioRepository
import com.investra.app.data.repository.TradingViewRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)
    private val tradingViewRepo = TradingViewRepository()
    private val portfolioRepo = PortfolioRepository(dbHelper)

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Stock>>(emptyList())
    val searchResults: StateFlow<List<Stock>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var searchJob: Job? = null

    // Market stocks
    private val _gainers = MutableStateFlow<List<Stock>>(emptyList())
    val gainers: StateFlow<List<Stock>> = _gainers.asStateFlow()

    private val _losers = MutableStateFlow<List<Stock>>(emptyList())
    val losers: StateFlow<List<Stock>> = _losers.asStateFlow()

    private val _activeStocks = MutableStateFlow<List<Stock>>(emptyList())
    val activeStocks: StateFlow<List<Stock>> = _activeStocks.asStateFlow()

    private val _selectedCategoryTab = MutableStateFlow("gainers")
    val selectedCategoryTab: StateFlow<String> = _selectedCategoryTab.asStateFlow()

    // Timeframe Selection
    private val _selectedTimeframe = MutableStateFlow("1 Hari")
    val selectedTimeframe: StateFlow<String> = _selectedTimeframe.asStateFlow()

    // Portfolio State
    val virtualCash: StateFlow<Double> = portfolioRepo.virtualCash
    val initialCapital: StateFlow<Double> = portfolioRepo.initialCapital
    val positions: StateFlow<List<Position>> = portfolioRepo.positions
    val watchlist: StateFlow<Set<String>> = portfolioRepo.watchlist
    val tradeHistory: StateFlow<List<TradeHistory>> = portfolioRepo.tradeHistory

    // Calculator State
    private val _calculatorState = MutableStateFlow(CalculatorState())
    val calculatorState: StateFlow<CalculatorState> = _calculatorState.asStateFlow()

    // Screener State
    private val _screenerFilter = MutableStateFlow(ScreenerFilter())
    val screenerFilter: StateFlow<ScreenerFilter> = _screenerFilter.asStateFlow()

    // Toast Message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private var liveUpdateJob: Job? = null

    init {
        startLivePriceUpdates()
    }

    private fun startLivePriceUpdates() {
        liveUpdateJob?.cancel()
        liveUpdateJob = viewModelScope.launch {
            while (true) {
                try {
                    // 1. Fetch top gainers (20) & losers (20) & active
                    val gainersRes = tradingViewRepo.fetchStockScanner("gainers", 20)
                    if (gainersRes.isSuccess && gainersRes.getOrNull()?.isNotEmpty() == true) {
                        _gainers.value = gainersRes.getOrNull()!!
                    } else if (_gainers.value.isEmpty()) {
                        _gainers.value = tradingViewRepo.getFallbackStocks("gainers")
                    }

                    val losersRes = tradingViewRepo.fetchStockScanner("losers", 20)
                    if (losersRes.isSuccess && losersRes.getOrNull()?.isNotEmpty() == true) {
                        _losers.value = losersRes.getOrNull()!!
                    } else if (_losers.value.isEmpty()) {
                        _losers.value = tradingViewRepo.getFallbackStocks("losers")
                    }

                    val activeRes = tradingViewRepo.fetchStockScanner("active", 20)
                    if (activeRes.isSuccess && activeRes.getOrNull()?.isNotEmpty() == true) {
                        _activeStocks.value = activeRes.getOrNull()!!
                    } else if (_activeStocks.value.isEmpty()) {
                        _activeStocks.value = tradingViewRepo.getFallbackStocks("active")
                    }

                    // 2. Fetch live quotes for portfolio positions and watchlist
                    val posTickers = positions.value.map { it.ticker }
                    val watchTickers = watchlist.value.toList()
                    val allTickers = (posTickers + watchTickers).distinct()

                    if (allTickers.isNotEmpty()) {
                        val quotesRes = tradingViewRepo.fetchStockQuotes(allTickers)
                        val quoteMap = quotesRes.getOrDefault(emptyMap())

                        if (quoteMap.isNotEmpty()) {
                            val priceMap = quoteMap.mapValues { it.value.price }
                            portfolioRepo.updatePositionPrices(priceMap)
                        }
                    }
                } catch (e: Exception) {
                    // Suppress and continue live loop
                }

                delay(1000) // 1-second interval
            }
        }
    }

    fun loadMarketData() {
        startLivePriceUpdates()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }

        searchJob = viewModelScope.launch {
            _isSearching.value = true
            delay(300) // Debounce
            val result = tradingViewRepo.searchStocks(query, 15)
            _searchResults.value = result.getOrDefault(emptyList())
            _isSearching.value = false
        }
    }

    fun setCategoryTab(tab: String) {
        _selectedCategoryTab.value = tab
    }

    fun setTimeframe(tf: String) {
        _selectedTimeframe.value = tf
        showToast("Timeframe diubah ke $tf")
    }

    fun executeBuy(stock: Stock, shares: Int, orderType: String = "Market Order", stopLoss: Double? = null, takeProfit: Double? = null): Boolean {
        val success = portfolioRepo.buyStock(stock, shares, orderType, stopLoss, takeProfit)
        if (success) {
            val cost = stock.price * shares
            showToast("Order simulasi $shares lot ${stock.ticker} ($${String.format("%.2f", cost)}) berhasil!")
        } else {
            showToast("Saldo kas virtual tidak mencukupi!")
        }
        return success
    }

    fun executeSell(ticker: String, sharesToSell: Int): Boolean {
        val success = portfolioRepo.sellStock(ticker, sharesToSell)
        if (success) {
            showToast("Posisi $ticker $sharesToSell lembar berhasil direalisasikan!")
        } else {
            showToast("Gagal merealisasikan posisi!")
        }
        return success
    }

    fun toggleWatchlist(ticker: String) {
        portfolioRepo.toggleWatchlist(ticker)
        val isSaved = portfolioRepo.watchlist.value.contains(ticker)
        val text = if (isSaved) "$ticker ditambahkan ke Watchlist" else "$ticker dihapus dari Watchlist"
        showToast(text)
    }

    fun resetPortfolio(newCapital: Double) {
        portfolioRepo.resetPortfolio(newCapital)
        showToast("Portofolio di-reset ke $${String.format("%,.2f", newCapital)} USD")
    }

    fun updateCalculator(entryPrice: Double, shares: Int, targetPrice: Double, stopLossPrice: Double, ticker: String = "NVDA") {
        _calculatorState.value = CalculatorState(
            ticker = ticker,
            entryPrice = entryPrice,
            shares = shares,
            targetPrice = targetPrice,
            stopLossPrice = stopLossPrice
        )
    }

    fun setScreenerPreset(preset: String) {
        _screenerFilter.value = _screenerFilter.value.copy(preset = preset)
        showToast("Preset skrining: $preset")
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
