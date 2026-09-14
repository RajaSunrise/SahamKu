package com.investra.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.investra.app.data.model.Position
import com.investra.app.data.model.Stock
import com.investra.app.ui.MainViewModel
import com.investra.app.ui.components.InvestraBottomNav
import com.investra.app.ui.components.InvestraHeader
import com.investra.app.ui.components.NavTab
import com.investra.app.ui.screens.CalculatorScreen
import com.investra.app.ui.screens.DemoBuyDialog
import com.investra.app.ui.screens.DemoSellDialog
import com.investra.app.ui.screens.HistoryScreen
import com.investra.app.ui.screens.PasarScreen
import com.investra.app.ui.screens.PortofolioScreen
import com.investra.app.ui.screens.RekomendasiScreen
import com.investra.app.ui.screens.ScreenerScreen
import com.investra.app.ui.screens.SettingsScreen
import com.investra.app.ui.screens.StockDetailScreen
import com.investra.app.ui.theme.BgDark
import com.investra.app.ui.theme.InvestraTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InvestraTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: MainViewModel) {
    val context = LocalContext.current
    val toastMsg by viewModel.toastMessage.collectAsState()
    val virtualCash by viewModel.virtualCash.collectAsState()

    var currentTab by remember { mutableStateOf(NavTab.PASAR) }
    var activeSubScreen by remember { mutableStateOf<String?>(null) } // "detail", "history", "screener", "calculator"
    var selectedStockDetail by remember { mutableStateOf<Stock?>(null) }

    // Dialog states
    var buyStockTarget by remember { mutableStateOf<Stock?>(null) }
    var sellPositionTarget by remember { mutableStateOf<Position?>(null) }

    LaunchedEffect(toastMsg) {
        toastMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            InvestraHeader(
                currentTabTitle = when (activeSubScreen) {
                    "detail" -> "Detail ${selectedStockDetail?.ticker ?: "Stock"}"
                    "history" -> "Riwayat Kinerja"
                    "screener" -> "Screener"
                    "calculator" -> "Kalkulator"
                    else -> currentTab.label
                },
                virtualBalanceFormatted = "$${String.format("%,.2f", virtualCash)}"
            )
        },
        bottomBar = {
            if (activeSubScreen == null) {
                InvestraBottomNav(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            }
        },
        containerColor = BgDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BgDark)
        ) {
            when {
                activeSubScreen == "detail" && selectedStockDetail != null -> {
                    StockDetailScreen(
                        stock = selectedStockDetail!!,
                        viewModel = viewModel,
                        onBackClick = { activeSubScreen = null },
                        onBuyClick = { buyStockTarget = selectedStockDetail },
                        onCalculatorClick = { activeSubScreen = "calculator" }
                    )
                }
                activeSubScreen == "history" -> {
                    HistoryScreen(
                        viewModel = viewModel,
                        onBackClick = { activeSubScreen = null }
                    )
                }
                activeSubScreen == "screener" -> {
                    ScreenerScreen(
                        viewModel = viewModel,
                        onBackClick = { activeSubScreen = null },
                        onStockClick = { stock ->
                            selectedStockDetail = stock
                            activeSubScreen = "detail"
                        },
                        onQuickBuyClick = { buyStockTarget = it }
                    )
                }
                activeSubScreen == "calculator" -> {
                    CalculatorScreen(
                        viewModel = viewModel,
                        onBackClick = { activeSubScreen = null },
                        onApplyToOrder = { entryPrice, shares ->
                            val defaultStock = selectedStockDetail ?: Stock("NVDA", "NVIDIA Corporation", "NASDAQ", entryPrice, 7.32, 5.42, 14200000000.0, "14.2B")
                            buyStockTarget = defaultStock.copy(price = entryPrice)
                            activeSubScreen = null
                        }
                    )
                }
                else -> {
                    when (currentTab) {
                        NavTab.PASAR -> {
                            PasarScreen(
                                viewModel = viewModel,
                                onStockClick = { stock ->
                                    selectedStockDetail = stock
                                    activeSubScreen = "detail"
                                },
                                onQuickBuyClick = { buyStockTarget = it }
                            )
                        }
                        NavTab.REKOMENDASI -> {
                            RekomendasiScreen(
                                viewModel = viewModel,
                                onStockClick = { stock ->
                                    selectedStockDetail = stock
                                    activeSubScreen = "detail"
                                },
                                onQuickBuyClick = { buyStockTarget = it }
                            )
                        }
                        NavTab.PORTOFOLIO -> {
                            PortofolioScreen(
                                viewModel = viewModel,
                                onStockClick = { ticker ->
                                    val fallback = viewModel.gainers.value.find { it.ticker == ticker }
                                        ?: Stock(ticker, ticker, "NASDAQ", 142.50, 7.32, 5.42, 14200000000.0, "14.2B")
                                    selectedStockDetail = fallback
                                    activeSubScreen = "detail"
                                },
                                onSellClick = { sellPositionTarget = it },
                                onHistoryClick = { activeSubScreen = "history" }
                            )
                        }
                        NavTab.PENGATURAN -> {
                            SettingsScreen(
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }

        // Demo Buy Dialog Modal
        buyStockTarget?.let { stock ->
            DemoBuyDialog(
                stock = stock,
                viewModel = viewModel,
                onDismiss = { buyStockTarget = null }
            )
        }

        // Demo Sell Dialog Modal
        sellPositionTarget?.let { pos ->
            DemoSellDialog(
                position = pos,
                viewModel = viewModel,
                onDismiss = { sellPositionTarget = null }
            )
        }
    }
}
