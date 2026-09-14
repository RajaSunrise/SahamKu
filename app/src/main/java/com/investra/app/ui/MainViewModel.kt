package com.investra.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investra.app.data.model.CalculatorState
import com.investra.app.data.model.Position
import com.investra.app.data.model.ScreenerFilter
import com.investra.app.data.model.Stock
import com.investra.app.data.model.TradeHistory
import com.investra.app.data.repository.PortfolioRepository
import com.investra.app.data.repository.TradingViewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val tradingViewRepo: TradingViewRepository = TradingViewRepository(),
    private val portfolioRepo: PortfolioRepository = PortfolioRepository()
) : ViewModel() {

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

    init {
        loadMarketData()
    }

    fun loadMarketData() {
        viewModelScope.launch {
            val gainersResult = tradingViewRepo.fetchStockScanner("gainers", 10)
            _gainers.value = gainersResult.getOrDefault(tradingViewRepo.getFallbackStocks("gainers"))

            val losersResult = tradingViewRepo.fetchStockScanner("losers", 10)
            _losers.value = losersResult.getOrDefault(tradingViewRepo.getFallbackStocks("losers"))

            val activeResult = tradingViewRepo.fetchStockScanner("active", 10)
            _activeStocks.value = activeResult.getOrDefault(tradingViewRepo.getFallbackStocks("active"))
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
