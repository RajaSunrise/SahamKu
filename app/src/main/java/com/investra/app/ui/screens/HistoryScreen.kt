package com.investra.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.investra.app.data.model.TradeHistory
import com.investra.app.ui.MainViewModel
import com.investra.app.ui.components.StockLogoImage
import com.investra.app.ui.theme.PrimaryContainer
import com.investra.app.ui.theme.PrimaryEmerald
import com.investra.app.ui.theme.SecondaryBlue
import com.investra.app.ui.theme.SurfaceContainer
import com.investra.app.ui.theme.SurfaceContainerHigh
import com.investra.app.ui.theme.SurfaceContainerLow
import com.investra.app.ui.theme.SurfaceContainerLowest
import com.investra.app.ui.theme.TertiaryContainer
import com.investra.app.ui.theme.TextMain
import com.investra.app.ui.theme.TextMuted

@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val rawTradeHistory by viewModel.tradeHistory.collectAsState()
    val initialCapital by viewModel.initialCapital.collectAsState()
    val marketGainers by viewModel.gainers.collectAsState()
    var selectedRange by remember { mutableStateOf("semua") }

    val tradeHistory = remember(rawTradeHistory, selectedRange) {
        rawTradeHistory // Filters can be applied if date range metadata exists
    }

    val totalRealizedPnL = tradeHistory.sumOf { it.realizedPnL }
    val winCount = tradeHistory.count { it.isWin }
    val lossCount = tradeHistory.count { !it.isWin }
    val totalTrades = tradeHistory.size
    val winRate = if (totalTrades > 0) (winCount.toDouble() / totalTrades) * 100 else 0.0

    val winTrades = tradeHistory.filter { it.isWin }
    val lossTrades = tradeHistory.filter { !it.isWin }

    val totalWinAmount = winTrades.sumOf { it.realizedPnL }
    val totalLossAmount = lossTrades.sumOf { Math.abs(it.realizedPnL) }

    val avgWinAmount = if (winTrades.isNotEmpty()) totalWinAmount / winTrades.size else 0.0
    val avgWinPercent = if (winTrades.isNotEmpty()) winTrades.map { it.realizedPnLPercent }.average() else 0.0

    val avgLossAmount = if (lossTrades.isNotEmpty()) totalLossAmount / lossTrades.size else 0.0
    val avgLossPercent = if (lossTrades.isNotEmpty()) lossTrades.map { Math.abs(it.realizedPnLPercent) }.average() else 0.0

    val profitFactor = if (totalLossAmount > 0) totalWinAmount / totalLossAmount else if (totalWinAmount > 0) 99.0 else 0.0
    val returnOnCapital = if (initialCapital > 0) (totalRealizedPnL / initialCapital) * 100 else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = TextMain)
                }
                Column {
                    Text(text = "Riwayat & Kinerja Trading", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "Jurnal evaluasi real dari eksekusi transaksi Anda", color = TextMuted, fontSize = 11.sp)
                }
            }
        }

        // Timeframe Selector
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerLowest)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("bulan-ini" to "Bulan Ini", "30-hari" to "30 Hari", "semua" to "Semua").forEach { (id, label) ->
                    val isSelected = selectedRange == id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) SurfaceContainerHigh else Color.Transparent)
                            .clickable { selectedRange = id }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) PrimaryEmerald else TextMuted,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Primary Realized PnL Hero Card
        item {
            val isPositivePnL = totalRealizedPnL >= 0

            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(text = "TOTAL REALIZED PROFIT", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${if (isPositivePnL) "+" else ""}$${String.format("%,.2f", totalRealizedPnL)} USD",
                                color = if (isPositivePnL) PrimaryEmerald else TertiaryContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isPositivePnL) PrimaryEmerald.copy(alpha = 0.15f) else TertiaryContainer.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.TrendingUp, contentDescription = "Up", tint = if (isPositivePnL) PrimaryEmerald else TertiaryContainer, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${if (returnOnCapital >= 0) "+" else ""}${String.format("%.2f", returnOnCapital)}%",
                                    color = if (isPositivePnL) PrimaryEmerald else TertiaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Distribution Mini Bars dynamically rendered from real tradeHistory
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Distribusi Transaksi ($totalTrades Transaksi Real)", color = TextMuted, fontSize = 10.sp)
                            Text(text = "${String.format("%.1f", winRate)}% Win Rate", color = if (winRate >= 50) PrimaryEmerald else TertiaryContainer, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        if (tradeHistory.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().height(28.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                val recentTrades = tradeHistory.take(15).reversed()
                                val maxPnl = recentTrades.maxOfOrNull { Math.abs(it.realizedPnL) }?.coerceAtLeast(1.0) ?: 1.0

                                recentTrades.forEach { tr ->
                                    val pct = (Math.abs(tr.realizedPnL) / maxPnl).coerceIn(0.2, 1.0).toFloat()
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height((28 * pct).dp)
                                            .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                            .background(if (tr.isWin) PrimaryEmerald else TertiaryContainer)
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Belum ada transaksi tertutup", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // Key Analytics Metrics Grid
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AnalyticsCard(
                    title = "WIN RATE",
                    value = "${String.format("%.1f", winRate)}%",
                    sub = "$winCount Menang / $lossCount Kalah ($totalTrades Total)",
                    modifier = Modifier.weight(1f)
                )
                AnalyticsCard(
                    title = "PROFIT FACTOR",
                    value = if (profitFactor >= 99.0) "MAX" else String.format("%.2f", profitFactor),
                    sub = if (profitFactor >= 1.5) "Sangat Efisien" else if (profitFactor >= 1.0) "Netral" else "Perlu Evaluasi",
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AnalyticsCard(
                    title = "RATA-RATA UNTUNG",
                    value = "+$${String.format("%,.2f", avgWinAmount)}",
                    sub = "+${String.format("%.1f", avgWinPercent)}% per trade",
                    isPositive = true,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsCard(
                    title = "RATA-RATA RUGI",
                    value = "-$${String.format("%,.2f", avgLossAmount)}",
                    sub = "-${String.format("%.1f", avgLossPercent)}% per trade",
                    isPositive = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // AI Cognitive Learning Real Insight Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(PrimaryContainer.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.SmartToy, contentDescription = "AI", tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(text = "EVALUASI KINERJA NYATA", color = PrimaryEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (totalTrades == 0) {
                                "“Belum ada transaksi terealisasi. Lakukan simulasi beli & jual saham untuk membentuk jurnal & analytics otomatis.”"
                            } else if (winRate >= 60.0) {
                                "“Kinerja trading Anda solid dengan Win Rate ${String.format("%.1f", winRate)}%. Pertahankan eksekusi Risk-Reward yang disiplin.”"
                            } else {
                                "“Anda telah menyelesaikan $totalTrades transaksi ($winCount untung, $lossCount rugi). Perhatikan manajemen risiko untuk meningkatkan rasio profit.”"
                            },
                            color = TextMain,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Closed Trades Journal Listing
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Riwayat Eksekusi Real ($totalTrades)", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Log Transaksi", color = PrimaryEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(imageVector = Icons.Default.Download, contentDescription = "Export", tint = PrimaryEmerald, modifier = Modifier.size(14.dp))
                }
            }
        }

        if (tradeHistory.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "Belum Ada Riwayat Transaksi", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = "Silakan lakukan jual beli saham demo di menu Pasar untuk mencatat jurnal riwayat secara otomatis.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        } else {
            items(tradeHistory, key = { it.id }) { trade ->
                val matchingStock = marketGainers.find { it.ticker == trade.ticker }
                TradeHistoryCardItem(trade = trade, logoUrl = matchingStock?.logoUrl)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun AnalyticsCard(title: String, value: String, sub: String, isPositive: Boolean = true, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(text = value, color = if (isPositive) PrimaryEmerald else TertiaryContainer, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = sub, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
fun TradeHistoryCardItem(trade: TradeHistory, logoUrl: String? = null) {
    val effectiveLogoUrl = trade.logoUrl ?: logoUrl
    val sharesText = if (trade.shares % 1.0 == 0.0) "${trade.shares.toInt()}" else String.format("%.2f", trade.shares)

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StockLogoImage(ticker = trade.ticker, logoUrl = effectiveLogoUrl, size = 36.dp, fontSize = 14)
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = trade.ticker, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = trade.exchange, color = TextMuted, fontSize = 9.sp)
                        }
                        Text(text = "$sharesText lembar • ${trade.reasonText}", color = TextMuted, fontSize = 10.sp)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (trade.isWin) PrimaryEmerald.copy(alpha = 0.15f) else TertiaryContainer.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (trade.isWin) "WIN" else "LOSS",
                            color = if (trade.isWin) PrimaryEmerald else TertiaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                    Text(text = trade.dateText, color = TextMuted, fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerLow)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "P&L Terealisasi", color = TextMuted, fontSize = 9.sp)
                    Text(
                        text = "${if (trade.realizedPnL >= 0) "+" else ""}$${String.format("%.2f", trade.realizedPnL)}",
                        color = if (trade.isWin) PrimaryEmerald else TertiaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Divergensi Untung", color = TextMuted, fontSize = 9.sp)
                    Text(
                        text = "${if (trade.realizedPnLPercent >= 0) "+" else ""}${String.format("%.1f", trade.realizedPnLPercent)}%",
                        color = if (trade.isWin) PrimaryEmerald else TertiaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
