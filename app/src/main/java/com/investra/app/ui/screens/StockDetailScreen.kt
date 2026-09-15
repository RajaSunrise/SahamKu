package com.investra.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
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
import com.investra.app.ui.components.StockLogoImage
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
                            StockLogoImage(ticker = stock.ticker, logoUrl = stock.logoUrl, size = 40.dp, fontSize = 18)
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
                                Text(text = "${if (stock.changePercent >= 0) "+" else ""}${String.format("%.2f", stock.changePercent)}%", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                        Text(text = "Volume: ${stock.volumeFormatted}", color = TextMuted, fontSize = 11.sp)
                        Text(text = "Rentang Hari: $${String.format("%.2f", stock.price * 0.98)} - $${String.format("%.2f", stock.price * 1.02)}", color = TextMuted, fontSize = 11.sp)
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

            // Interactive Candlestick Chart Module Canvas
            item {
                // Generate timeframe-aware candle dataset based on current stock price
                val candleList = remember(stock.ticker, stock.price, selectedTf) {
                    val basePrice = stock.price
                    val count = 12
                    val tfMultiplier = when (selectedTf) {
                        "4H" -> 0.008
                        "1D" -> 0.015
                        "1W" -> 0.035
                        "1M" -> 0.070
                        "1Y" -> 0.150
                        else -> 0.015
                    }
                    val list = mutableListOf<StockCandlePoint>()
                    var prevClose = basePrice * (1.0 - (count / 2) * tfMultiplier * 0.3)

                    for (i in 0 until count) {
                        val factor = (Math.sin(i.toDouble() + selectedTf.hashCode()) * 0.8 + (i * 0.1))
                        val open = prevClose
                        val change = basePrice * tfMultiplier * factor
                        val close = Math.max(1.0, open + change)
                        val high = Math.max(open, close) + Math.abs(change) * 0.5
                        val low = Math.max(0.5, Math.min(open, close) - Math.abs(change) * 0.4)
                        val volume = stock.volume * (0.6 + (i % 5) * 0.15)
                        val label = "$selectedTf #${i + 1}"
                        list.add(StockCandlePoint(open, high, low, close, volume, label))
                        prevClose = close
                    }
                    list
                }

                var touchX by remember { mutableStateOf<Float?>(null) }
                var selectedCandleIndex by remember { mutableStateOf<Int?>(null) }

                val highlightedCandle = selectedCandleIndex?.let { candleList.getOrNull(it) } ?: candleList.lastOrNull()
                val activePrice = highlightedCandle?.close ?: stock.price
                val priceDiff = if (candleList.isNotEmpty()) activePrice - candleList.first().open else stock.change
                val priceDiffPct = if (candleList.isNotEmpty() && candleList.first().open > 0) (priceDiff / candleList.first().open) * 100 else stock.changePercent

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
                                Text(text = "• EMA20: $${String.format("%.2f", stock.ema20)}", color = PrimaryEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = "• EMA50: $${String.format("%.2f", stock.ema50)}", color = SecondaryBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = if (selectedCandleIndex != null) "Interactive Crosshair" else "Live Market ($selectedTf)",
                                color = if (selectedCandleIndex != null) PrimaryEmerald else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = if (selectedCandleIndex != null) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        // Touch Tooltip Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerLowest)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = highlightedCandle?.label ?: selectedTf, color = TextMuted, fontSize = 11.sp)
                                Text(
                                    text = "$${String.format("%.2f", activePrice)}",
                                    color = TextMain,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                text = "${if (priceDiffPct >= 0) "+" else ""}${String.format("%.2f", priceDiffPct)}%",
                                color = if (priceDiffPct >= 0) PrimaryEmerald else TertiaryContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        // SVG / Canvas Candlestick representation with Touch Gestures
                        val gridLineColor = SurfaceContainerHighest
                        val chartBgColor = SurfaceContainerLowest

                        val minPrice = (candleList.minOfOrNull { it.low } ?: (stock.price * 0.9)).toDouble()
                        val maxPrice = (candleList.maxOfOrNull { it.high } ?: (stock.price * 1.1)).toDouble()
                        val priceRange = if (maxPrice - minPrice > 0) maxPrice - minPrice else 1.0

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(chartBgColor)
                                .pointerInput(candleList) {
                                    detectTapGestures(
                                        onTap = { offset ->
                                            val step = size.width / candleList.size
                                            val idx = (offset.x / step).toInt().coerceIn(0, candleList.size - 1)
                                            touchX = offset.x
                                            selectedCandleIndex = idx
                                        }
                                    )
                                }
                                .pointerInput(candleList) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            val step = size.width / candleList.size
                                            val idx = (offset.x / step).toInt().coerceIn(0, candleList.size - 1)
                                            touchX = offset.x
                                            selectedCandleIndex = idx
                                        },
                                        onDrag = { change, _ ->
                                            val step = size.width / candleList.size
                                            val idx = (change.position.x / step).toInt().coerceIn(0, candleList.size - 1)
                                            touchX = change.position.x
                                            selectedCandleIndex = idx
                                        },
                                        onDragEnd = {
                                            // Keep selected candle visible for inspect
                                        }
                                    )
                                }
                        ) {
                            val w = size.width
                            val h = size.height

                            val gridY1 = h * 0.20f
                            val gridY2 = h * 0.50f
                            val gridY3 = h * 0.75f

                            drawLine(color = gridLineColor, start = Offset(0f, gridY1), end = Offset(w, gridY1), strokeWidth = 1f)
                            drawLine(color = gridLineColor, start = Offset(0f, gridY2), end = Offset(w, gridY2), strokeWidth = 1f)
                            drawLine(color = gridLineColor, start = Offset(0f, gridY3), end = Offset(w, gridY3), strokeWidth = 1f)

                            val candleCount = candleList.size
                            val step = w / candleCount

                            // Draw EMA lines connecting candle closes
                            val ma20Path = Path()
                            val ma50Path = Path()

                            for (i in 0 until candleCount) {
                                val c = candleList[i]
                                val cx = step * i + step / 2
                                val cy = (h * 0.75f) - (((c.close - minPrice) / priceRange) * (h * 0.6f)).toFloat()

                                val ema20Y = cy + (h * 0.05f)
                                val ema50Y = cy + (h * 0.12f)

                                if (i == 0) {
                                    ma20Path.moveTo(cx, ema20Y)
                                    ma50Path.moveTo(cx, ema50Y)
                                } else {
                                    ma20Path.lineTo(cx, ema20Y)
                                    ma50Path.lineTo(cx, ema50Y)
                                }
                            }

                            drawPath(path = ma20Path, color = PrimaryEmerald, style = Stroke(width = 4f))
                            drawPath(path = ma50Path, color = SecondaryBlue, style = Stroke(width = 3f))

                            // Draw Candlesticks & Volume Bars
                            val maxVol = (candleList.maxOfOrNull { it.volume } ?: 1.0)
                            for (i in 0 until candleCount) {
                                val c = candleList[i]
                                val x = step * i + step / 2
                                val isBullish = c.close >= c.open
                                val color = if (isBullish) PrimaryEmerald else TertiaryContainer

                                val highY = (h * 0.75f) - (((c.high - minPrice) / priceRange) * (h * 0.6f)).toFloat()
                                val lowY = (h * 0.75f) - (((c.low - minPrice) / priceRange) * (h * 0.6f)).toFloat()
                                val openY = (h * 0.75f) - (((c.open - minPrice) / priceRange) * (h * 0.6f)).toFloat()
                                val closeY = (h * 0.75f) - (((c.close - minPrice) / priceRange) * (h * 0.6f)).toFloat()

                                val candleTop = Math.min(openY, closeY)
                                val candleBottom = Math.max(openY, closeY)
                                val candleH = Math.max(3f, candleBottom - candleTop)

                                // Wick
                                drawLine(color = color, start = Offset(x, highY), end = Offset(x, lowY), strokeWidth = 2f)

                                // Candle Body
                                val candleWidth = (step * 0.55f).coerceIn(8f, 20f)
                                drawRect(
                                    color = color,
                                    topLeft = Offset(x - candleWidth / 2, candleTop),
                                    size = Size(candleWidth, candleH)
                                )

                                // Volume Bar
                                val volH = ((c.volume / maxVol) * (h * 0.20f)).toFloat().coerceAtLeast(4f)
                                drawRect(
                                    color = color.copy(alpha = 0.4f),
                                    topLeft = Offset(x - candleWidth / 2, h - volH),
                                    size = Size(candleWidth, volH)
                                )
                            }

                            // Draw Touch Crosshair line
                            val currentTouchX = touchX
                            if (currentTouchX != null && selectedCandleIndex != null) {
                                val idx = selectedCandleIndex!!.coerceIn(0, candleCount - 1)
                                val cx = step * idx + step / 2
                                val c = candleList[idx]
                                val cy = (h * 0.75f) - (((c.close - minPrice) / priceRange) * (h * 0.6f)).toFloat()

                                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                val crosshairColor = Color.LightGray
                                // Vertical line
                                drawLine(
                                    color = crosshairColor,
                                    start = Offset(cx, 0f),
                                    end = Offset(cx, h),
                                    strokeWidth = 1.5f,
                                    pathEffect = dashEffect
                                )
                                // Horizontal line
                                drawLine(
                                    color = crosshairColor,
                                    start = Offset(0f, cy),
                                    end = Offset(w, cy),
                                    strokeWidth = 1.5f,
                                    pathEffect = dashEffect
                                )
                                // Focus Circle Point
                                drawCircle(
                                    color = PrimaryEmerald,
                                    radius = 12f,
                                    center = Offset(cx, cy)
                                )
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
                                Text(text = "Tekan/Geser Chart Untuk Crosshair", color = TextMuted, fontSize = 10.sp)
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
                                Text(text = stock.recommendationText, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text(text = "Dihitung dari TradingView scanner", color = TextMuted, fontSize = 11.sp)
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
                                    Text(text = "${(stock.recommendationScore * 100).toInt()}%", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                            IndicatorDetailRow(icon = Icons.Default.Speed, title = "RSI (14)", value = "${String.format("%.1f", stock.rsi)}", desc = "Status: Akumulasi Teknikal")
                            IndicatorDetailRow(icon = Icons.Default.CallSplit, title = "MACD (12, 26, 9)", value = stock.macdStatus, desc = "Analisis momentum tren harian")
                            IndicatorDetailRow(icon = Icons.Default.StackedLineChart, title = "EMA 20 & EMA 50", value = "EMA20: $${String.format("%.2f", stock.ema20)}", desc = "Golden Cross EMA 20/50 Valid")
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
                            LevelTile(title = "SUPPORT 1", value = "$${String.format("%.2f", stock.support1)}", sub = "Batas Support", color = TextMain, modifier = Modifier.weight(1f))
                            LevelTile(title = "RESISTANCE 1", value = "$${String.format("%.2f", stock.resistance1)}", sub = "Batas Resistance", color = TextMain, modifier = Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LevelTile(title = "TARGET TP 1", value = "$${String.format("%.2f", stock.targetPrice1)}", sub = "+7.00% Target", color = PrimaryEmerald, modifier = Modifier.weight(1f))
                            LevelTile(title = "TARGET TP 2", value = "$${String.format("%.2f", stock.targetPrice2)}", sub = "+12.00% Target", color = PrimaryEmerald, modifier = Modifier.weight(1f))
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
                                    Text(text = "Stop Loss $${String.format("%.2f", stock.stopLossPrice)} & TP1 $${String.format("%.2f", stock.targetPrice1)}", color = TextMuted, fontSize = 10.sp)
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

data class StockCandlePoint(
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double,
    val label: String
)

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
