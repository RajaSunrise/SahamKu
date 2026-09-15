package com.investra.app

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.investra.app.ui.screens.SearchScreen
import com.investra.app.ui.screens.SettingsScreen
import com.investra.app.ui.screens.SplashScreen
import com.investra.app.ui.screens.StockDetailScreen
import com.investra.app.ui.theme.BgDark
import com.investra.app.ui.theme.InvestraTheme
import com.investra.app.ui.theme.PrimaryEmerald

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            InvestraTheme(themeMode = themeMode) {
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

    var showSplash by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(NavTab.PASAR) }
    var activeSubScreen by remember { mutableStateOf<String?>(null) } // "detail", "history", "screener", "calculator", "search"
    var selectedStockDetail by remember { mutableStateOf<Stock?>(null) }

    // Dialog states
    var buyStockTarget by remember { mutableStateOf<Stock?>(null) }
    var sellPositionTarget by remember { mutableStateOf<Position?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(toastMsg) {
        toastMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    if (showSplash) {
        SplashScreen(
            onSplashFinished = { showSplash = false }
        )
        return
    }

    // Handle System Back Button Navigation & Exit Confirmation
    BackHandler {
        when {
            buyStockTarget != null -> buyStockTarget = null
            sellPositionTarget != null -> sellPositionTarget = null
            activeSubScreen == "calculator" && selectedStockDetail != null -> activeSubScreen = "detail"
            activeSubScreen != null -> activeSubScreen = null
            else -> showExitDialog = true
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
                    "search" -> "Pencarian Saham"
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
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                activeSubScreen == "detail" && selectedStockDetail != null -> {
                    LaunchedEffect(selectedStockDetail) {
                        viewModel.setSelectedStock(selectedStockDetail)
                    }
                    StockDetailScreen(
                        stock = selectedStockDetail!!,
                        viewModel = viewModel,
                        onBackClick = {
                            viewModel.setSelectedStock(null)
                            activeSubScreen = null
                        },
                        onBuyClick = { buyStockTarget = viewModel.selectedStock.value ?: selectedStockDetail },
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
                        }
                    )
                }
                activeSubScreen == "calculator" -> {
                    CalculatorScreen(
                        viewModel = viewModel,
                        stock = selectedStockDetail,
                        onBackClick = { activeSubScreen = null },
                        onApplyToOrder = { entryPrice, shares ->
                            val defaultStock = selectedStockDetail ?: Stock("NVDA", "NVIDIA Corporation", "NASDAQ", entryPrice, 7.32, 5.42, 14200000000.0, "14.2B")
                            buyStockTarget = defaultStock.copy(price = entryPrice)
                            activeSubScreen = null
                        }
                    )
                }
                activeSubScreen == "search" -> {
                    SearchScreen(
                        viewModel = viewModel,
                        onBackClick = { activeSubScreen = null },
                        onStockClick = { stock ->
                            selectedStockDetail = stock
                            activeSubScreen = "detail"
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
                                onSearchClick = { activeSubScreen = "search" }
                            )
                        }
                        NavTab.REKOMENDASI -> {
                            RekomendasiScreen(
                                viewModel = viewModel,
                                onStockClick = { stock ->
                                    selectedStockDetail = stock
                                    activeSubScreen = "detail"
                                }
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

        // Exit Confirmation Dialog
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = {
                    Text(
                        text = "Keluar dari Aplikasi",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Text(
                        text = "Apakah Anda yakin ingin keluar dari SahamKu?",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showExitDialog = false
                            (context as? Activity)?.finish()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Iya", color = Color(0xFF003824), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showExitDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Tidak", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}
