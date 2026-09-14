package com.investra.app.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.investra.app.data.model.Stock
import com.investra.app.ui.MainViewModel
import com.investra.app.ui.theme.PrimaryContainer
import com.investra.app.ui.theme.PrimaryEmerald
import com.investra.app.ui.theme.SecondaryBlue
import com.investra.app.ui.theme.SurfaceContainer
import com.investra.app.ui.theme.SurfaceContainerHigh
import com.investra.app.ui.theme.SurfaceContainerHighest
import com.investra.app.ui.theme.SurfaceContainerLow
import com.investra.app.ui.theme.SurfaceContainerLowest
import com.investra.app.ui.theme.TertiaryContainer
import com.investra.app.ui.theme.TextMain
import com.investra.app.ui.theme.TextMuted

@Composable
fun StockDetailScreen(
    stock: Stock,
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onBuyClick: () -> Unit,
    onCalculatorClick: () -> Unit
) {
    var selectedTf by remember { mutableStateOf("1D") }
    val timeframes = listOf("4H", "1D", "1W", "1M", "1Y")
    val watchlistSet by viewModel.watchlist.collectAsState()
    val isStarred = watchlistSet.contains(stock.ticker)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Navigation Bar Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = TextMain)
                        }
                        Column {
                            Text(text = "Stock Detail", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "DEMO TRADING / VIRTUAL $100K", color = PrimaryEmerald, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { viewModel.showToast("Notifikasi alert diset untuk ${stock.ticker}") },
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(SurfaceContainerHigh)
                        ) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Alert", tint = TextMuted, modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = { viewModel.toggleWatchlist(stock.ticker) },
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(SurfaceContainerHigh)
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = if (isStarred) PrimaryEmerald else TextMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Price & Stock Overview Header
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = stock.ticker.take(1), color = TextMain, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = stock.ticker, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(SurfaceContainerHigh).padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = stock.exchange, color = TextMuted, fontSize = 10.sp)
                                    }
                                }
                                Text(text = stock.name, color = TextMuted, fontSize = 12.sp)
                            }
                        }

                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(PrimaryEmerald.copy(alpha = 0.15f)).padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.TrendingUp, contentDescription = "Up", tint = PrimaryEmerald, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "+${String.format("%.2f", stock.changePercent)}%", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(text = "$${String.format("%.2f", stock.price)}", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "USD", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Volume: 48.2M", color = TextMuted, fontSize = 11.sp)
                        Text(text = "Rentang Hari: $137.10 - $143.85", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }

            // Timeframe Selector Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLowest)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    timeframes.forEach { tf ->
                        val isSelected = selectedTf == tf
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SurfaceContainerHigh else Color.Transparent)
                                .clickable {
                                    selectedTf = tf
                                    viewModel.showToast("Memuat timeframe $tf...")
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tf,
                                color = if (isSelected) PrimaryEmerald else TextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Candlestick Chart Module Canvas
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(text = "• MA20: 138.45", color = PrimaryEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = "• MA50: 132.80", color = SecondaryBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(text = "Live Market", color = TextMuted, fontSize = 10.sp)
                        }

                        // SVG / Canvas Candlestick representation
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerLowest)
                        ) {
                            val w = size.width
                            val h = size.height

                            // Draw horizontal grid lines
                            val gridY1 = h * 0.25f
                            val gridY2 = h * 0.55f
                            val gridY3 = h * 0.8f

                            drawLine(color = SurfaceContainerHighest, start = Offset(0f, gridY1), end = Offset(w, gridY1), strokeWidth = 1f)
                            drawLine(color = SurfaceContainerHighest, start = Offset(0f, gridY2), end = Offset(w, gridY2), strokeWidth = 1f)
                            drawLine(color = SurfaceContainerHighest, start = Offset(0f, gridY3), end = Offset(w, gridY3), strokeWidth = 1f)

                            // Draw MA lines
                            val ma20Path = Path().apply {
                                moveTo(0f, h * 0.7f)
                                cubicTo(w * 0.3f, h * 0.6f, w * 0.6f, h * 0.4f, w, h * 0.2f)
                            }
                            drawPath(path = ma20Path, color = PrimaryEmerald, style = Stroke(width = 2.dp.toPx()))

                            val ma50Path = Path().apply {
                                moveTo(0f, h * 0.8f)
                                cubicTo(w * 0.4f, h * 0.75f, w * 0.7f, h * 0.55f, w, h * 0.45f)
                            }
                            drawPath(path = ma50Path, color = SecondaryBlue, style = Stroke(width = 1.5.dp.toPx()))

                            // Draw Candlesticks
                            val candleCount = 10
                            val step = w / candleCount
                            for (i in 0 until candleCount) {
                                val x = step * i + step / 2
                                val isBullish = i % 3 != 1
                                val color = if (isBullish) PrimaryEmerald else TertiaryContainer
                                val topY = (h * 0.6f) - (i * (h * 0.04f))
                                val candleHeight = (h * 0.12f)

                                drawLine(color = color, start = Offset(x, topY - 10f), end = Offset(x, topY + candleHeight + 10f), strokeWidth = 2f)
                                drawRect(color = color, topLeft = Offset(x - 8f, topY), size = Size(16f, candleHeight))

                                // Volume bar
                                val volH = (i + 1) * 6f + 10f
                                drawRect(color = color.copy(alpha = 0.5f), topLeft = Offset(x - 8f, h - volH), size = Size(16f, volH))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Tune, contentDescription = "Tune", tint = TextMuted, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Vol: 48.25M (Akumulasi Institusi)", color = TextMuted, fontSize = 10.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Fullscreen", color = PrimaryEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(imageVector = Icons.Default.OpenInFull, contentDescription = "Fullscreen", tint = PrimaryEmerald, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }

            // AI Technical Consensus Score Card
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
                                Text(text = "KONSENSUS TEKNIKAL AI", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(text = "STRONG BUY", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text(text = "Dihitung dari 21 agregator model harian", color = TextMuted, fontSize = 11.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(PrimaryContainer.copy(alpha = 0.2f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Bolt, contentDescription = "Score", tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "91%", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }

                        // Ratio Bar
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "16 Bullish", color = PrimaryEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(text = "4 Netral", color = SecondaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(text = "1 Bearish", color = TertiaryContainer, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(SurfaceContainerHighest)) {
                                Box(modifier = Modifier.weight(0.76f).fillMaxWidth().background(PrimaryEmerald))
                                Box(modifier = Modifier.weight(0.19f).fillMaxWidth().background(SecondaryBlue))
                                Box(modifier = Modifier.weight(0.05f).fillMaxWidth().background(TertiaryContainer))
                            }
                        }

                        // Deep Dive Indicators
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            IndicatorDetailRow(icon = Icons.Default.Speed, title = "RSI (14)", value = "62.4", desc = "Status: Zona Akumulasi Sehat")
                            IndicatorDetailRow(icon = Icons.Default.CallSplit, title = "MACD (12, 26, 9)", value = "Bullish Cross", desc = "Garis sinyal melintasi histogram ke atas")
                            IndicatorDetailRow(icon = Icons.Default.StackedLineChart, title = "EMA 20 & EMA 50", value = "Golden Cross", desc = "EMA 20 ($138.45) bergerak mantap di atas EMA 50")
                        }
                    }
                }
            }

            // Key Levels & Projections Grid
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Level Kunci & Proyeksi", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(text = "Skenario breakout & manajemen risiko", color = TextMuted, fontSize = 11.sp)
                            }
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(SurfaceContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Flag, contentDescription = "Flag", tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LevelTile(title = "SUPPORT 1", value = "$136.00", sub = "-4.56% dari saat ini", color = TextMain, modifier = Modifier.weight(1f))
                            LevelTile(title = "RESISTANCE 1", value = "$145.00", sub = "+1.75% (Titik Uji)", color = TextMain, modifier = Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LevelTile(title = "TARGET TP 1", value = "$152.00", sub = "+6.67% Target", color = PrimaryEmerald, modifier = Modifier.weight(1f))
                            LevelTile(title = "TARGET TP 2", value = "$160.00", sub = "+12.28% Target", color = PrimaryEmerald, modifier = Modifier.weight(1f))
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceContainerLowest)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.Default.Balance, contentDescription = "Balance", tint = PrimaryEmerald, modifier = Modifier.size(20.dp))
                                Column {
                                    Text(text = "Rasio Risk / Reward (R:R)", color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    Text(text = "Berdasarkan Stop Loss $136 & TP1 $152", color = TextMuted, fontSize = 10.sp)
                                }
                            }
                            Text(text = "1 : 2.69", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // Bottom Floating Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(SurfaceContainerLowest.copy(alpha = 0.95f))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onCalculatorClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Calculate, contentDescription = "Calc", tint = TextMain, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Kalkulator", color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }

                Button(
                    onClick = onBuyClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.ShoppingCartCheckout, contentDescription = "Checkout", tint = Color(0xFF003824), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Beli Demo (Virtual Order)", color = Color(0xFF003824), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun IndicatorDetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceContainerLow)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = icon, contentDescription = title, tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
            Column {
                Text(text = title, color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text(text = desc, color = TextMuted, fontSize = 10.sp)
            }
        }
        Text(text = value, color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun LevelTile(title: String, value: String, sub: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceContainerLow)
            .padding(12.dp)
    ) {
        Column {
            Text(text = title, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(text = value, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(vertical = 2.dp))
            Text(text = sub, color = TextMuted, fontSize = 10.sp)
        }
    }
}
