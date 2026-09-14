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
    val tradeHistory by viewModel.tradeHistory.collectAsState()
    var selectedRange by remember { mutableStateOf("bulan-ini") }

    val totalRealizedPnL = tradeHistory.sumOf { it.realizedPnL }
    val winCount = tradeHistory.count { it.isWin }
    val totalTrades = tradeHistory.size
    val winRate = if (totalTrades > 0) (winCount.toDouble() / totalTrades) * 100 else 78.5

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
                    Text(text = "Jurnal evaluasi eksekusi & efisiensi modal demo", color = TextMuted, fontSize = 11.sp)
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

        // Primary PnL Hero Card
        item {
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
                                text = "+$${String.format("%,.2f", 14280.50 + totalRealizedPnL)} USD",
                                color = PrimaryEmerald,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(PrimaryEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.TrendingUp, contentDescription = "Up", tint = PrimaryEmerald, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "+14.28%", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    // Distribution Mini Bars
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Distribusi Kemenangan (14 Transaksi)", color = TextMuted, fontSize = 10.sp)
                            Text(text = "85.7% Skala Profit", color = PrimaryEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().height(24.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val barHeights = listOf(0.6f, 0.75f, 0.3f, 0.9f, 0.4f, 0.65f, 0.8f, 0.2f, 0.5f, 0.95f, 0.7f, 0.25f, 0.85f, 1.0f)
                            barHeights.forEachIndexed { idx, pct ->
                                val isWinBar = idx % 3 != 2
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height((24 * pct).dp)
                                        .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                        .background(if (isWinBar) PrimaryEmerald else TertiaryContainer)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Key Analytics Metrics Grid
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AnalyticsCard(title = "WIN RATE", value = "${String.format("%.1f", winRate)}%", sub = "11 Menang / 3 Kalah", modifier = Modifier.weight(1f))
                AnalyticsCard(title = "PROFIT FACTOR", value = "3.40", sub = "Rasio Laba / Rugi Optimal", modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AnalyticsCard(title = "RATA-RATA UNTUNG", value = "+$1,420.00", sub = "+9.2% per trade", isPositive = true, modifier = Modifier.weight(1f))
                AnalyticsCard(title = "RATA-RATA RUGI", value = "-$420.00", sub = "-3.1% per trade", isPositive = false, modifier = Modifier.weight(1f))
            }
        }

        // AI Cognitive Learning Evaluation Banner
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
                        Text(text = "EVALUASI PEMBELAJARAN AI", color = PrimaryEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "“Disiplin Stop Loss berhasil menghemat modal virtual sebesar $1,200 dari potensi penurunan lanjutan di ticker AMD.”",
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
                Text(text = "Riwayat Eksekusi Tertutup", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Ekspor Log", color = PrimaryEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(imageVector = Icons.Default.Download, contentDescription = "Export", tint = PrimaryEmerald, modifier = Modifier.size(14.dp))
                }
            }
        }

        items(tradeHistory) { trade ->
            TradeHistoryCardItem(trade = trade)
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
fun TradeHistoryCardItem(trade: TradeHistory) {
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
                    Box(
                        modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = trade.ticker.take(1), color = TextMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = trade.ticker, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = trade.exchange, color = TextMuted, fontSize = 9.sp)
                        }
                        Text(text = trade.reasonText, color = TextMuted, fontSize = 10.sp)
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
