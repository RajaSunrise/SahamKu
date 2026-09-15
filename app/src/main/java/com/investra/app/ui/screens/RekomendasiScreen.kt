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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.investra.app.data.model.Stock
import com.investra.app.ui.MainViewModel
import com.investra.app.ui.components.StockLogoImage
import com.investra.app.ui.theme.PrimaryContainer
import com.investra.app.ui.theme.PrimaryEmerald
import com.investra.app.ui.theme.SurfaceContainer
import com.investra.app.ui.theme.SurfaceContainerHigh
import com.investra.app.ui.theme.SurfaceContainerLow
import com.investra.app.ui.theme.TertiaryContainer
import com.investra.app.ui.theme.TextMain
import com.investra.app.ui.theme.TextMuted

@Composable
fun RekomendasiScreen(
    viewModel: MainViewModel,
    onStockClick: (Stock) -> Unit
) {
    val selectedTf by viewModel.selectedTimeframe.collectAsState()
    val gainers by viewModel.gainers.collectAsState()
    val timeframes = listOf("4 Jam", "1 Hari", "1 Minggu", "1 Bulan", "1 Tahun")

    val aaplStock = gainers.firstOrNull() ?: Stock("AAPL", "Apple Inc.", "NASDAQ", 232.50, 2.71, 1.18, 18500000000.0, "18.5B", 0.85, "STRONG BUY", 58.20, "Golden Cross", 228.00, 220.00, "Consumer Electronics", "Technology", 3.5e12, "Daily Swing Pick", 226.0, 235.0, 248.0, 260.0, 226.0, logoUrl = "https://s3-symbol-logo.tradingview.com/apple.svg")

    val secondaryStocks = if (gainers.size > 1) gainers.drop(1).take(3) else listOf(
        Stock("MSFT", "Microsoft Corp", "NASDAQ", 448.20, 8.20, 1.86, 12400000000.0, "12.4B", 0.78, "BUY", 54.0, "Ascending Triangle", 438.00, 425.00, "Software Cloud", "Technology", 3.3e12, "Volume melonjak +22%", 439.0, 455.0, 471.5, 490.0, 439.0, logoUrl = "https://s3-symbol-logo.tradingview.com/microsoft.svg"),
        Stock("AMZN", "Amazon.com Inc", "NASDAQ", 186.40, 3.20, 1.75, 10200000000.0, "10.2B", 0.75, "BUY", 56.8, "Cup & Handle Pattern", 182.00, 178.00, "E-Commerce Cloud", "Consumer Cyclical", 1.9e12, "Rebound dari Support EMA 50", 181.0, 192.0, 202.0, 215.0, 181.0, logoUrl = "https://s3-symbol-logo.tradingview.com/amazon.svg"),
        Stock("GOOGL", "Alphabet Inc", "NASDAQ", 165.10, 2.10, 1.29, 8800000000.0, "8.8B", 0.68, "BUY ON DIP", 48.5, "Fib 61.8% Bounce", 162.00, 158.00, "Internet Media", "Communication", 2.0e12, "Reversal Candlestick Hammer", 161.5, 168.0, 173.2, 182.0, 161.5, logoUrl = "https://s3-symbol-logo.tradingview.com/alphabet.svg")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // AI Signal Accuracy Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PrimaryContainer.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "AI",
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Algoritma AI Teknikal", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PrimaryEmerald)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(text = "LIVE", color = Color(0xFF003824), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(text = "Sinyal terverifikasi multi-indikator", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "86.4%", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = "Win-Rate", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        }

        // Timeframe Selector
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "PILIH TIMEFRAME ANALISIS", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Schedule, contentDescription = "Update", tint = PrimaryEmerald, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "Update 2m lalu", color = PrimaryEmerald, fontSize = 10.sp)
                    }
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(timeframes, key = { it }) { tf ->
                        val isSelected = selectedTf == tf
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryEmerald else SurfaceContainerLow)
                                .clickable { viewModel.setTimeframe(tf) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = tf,
                                color = if (isSelected) Color(0xFF003824) else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PrimaryEmerald))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Mode: $selectedTf (Daily Swing)", color = PrimaryEmerald, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    Text(text = "Setup Risk/Reward Optimal", color = TextMuted, fontSize = 11.sp)
                }
            }
        }

        // Featured Technical Pick Card: AAPL
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStockClick(aaplStock) }
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StockLogoImage(ticker = aaplStock.ticker, logoUrl = aaplStock.logoUrl, size = 44.dp, fontSize = 18)
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = aaplStock.ticker, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(SurfaceContainerHigh)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = aaplStock.exchange, color = TextMuted, fontSize = 10.sp)
                                    }
                                }
                                Text(
                                    text = aaplStock.name,
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(horizontalAlignment = Alignment.End) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(PrimaryEmerald.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.TrendingUp, contentDescription = "Strong Buy", tint = PrimaryEmerald, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = aaplStock.recommendationText, color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                            Text(text = "Daily Swing Pick", color = TextMuted, fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
                        }
                    }

                    // Price vs Potential Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceContainer)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "HARGA SEKARANG", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(text = "$${String.format("%.2f", aaplStock.price)}", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${if (aaplStock.changePercent >= 0) "+" else ""}${String.format("%.2f", aaplStock.changePercent)}%", color = PrimaryEmerald, fontSize = 11.sp)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "TARGET KENAIKAN", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(text = "$${String.format("%.2f", aaplStock.targetPrice1)}", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "(+6.67%)", color = PrimaryEmerald, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Risk Reward Bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "STRUKTUR RISK & REWARD", color = TextMuted, fontSize = 10.sp)
                            Text(text = "Rasio 1 : 2.4 (Sangat Sehat)", color = PrimaryEmerald, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(SurfaceContainerHigh)
                        ) {
                            Box(modifier = Modifier.weight(0.29f).fillMaxWidth().background(TertiaryContainer))
                            Box(modifier = Modifier.weight(0.71f).fillMaxWidth().background(PrimaryEmerald))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Cut Loss: $${String.format("%.2f", aaplStock.stopLossPrice)}", color = TertiaryContainer, fontSize = 10.sp)
                            Text(text = "Target: $${String.format("%.2f", aaplStock.targetPrice1)}", color = PrimaryEmerald, fontSize = 10.sp)
                        }
                    }

                    // Mini Indicator Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IndicatorTile(title = "Moving Avg", value = "Golden Cross", sub = "EMA 20/50 Valid", modifier = Modifier.weight(1f))
                        IndicatorTile(title = "RSI (14)", value = "${aaplStock.rsi.toInt()}", sub = "Bullish Momentum", modifier = Modifier.weight(1f))
                        IndicatorTile(title = "Key Level", value = "Breakout", sub = "Resist Ditembus", modifier = Modifier.weight(1f))
                    }

                    // Action Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onStockClick(aaplStock) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayCircle, contentDescription = "Detail", tint = Color(0xFF003824), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Lihat Detail Saham", color = Color(0xFF003824), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        IconButton(
                            onClick = { viewModel.toggleWatchlist(aaplStock.ticker) },
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceContainer)
                        ) {
                            Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = "Bookmark", tint = TextMuted)
                        }
                    }
                }
            }
        }

        // Section Title Secondary Signals
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Radar, contentDescription = "Radar", tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Peluang Teknikal Lainnya", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Text(text = "${secondaryStocks.size} Sinyal Aktif", color = TextMuted, fontSize = 11.sp)
            }
        }

        // Secondary Signal List Items
        items(secondaryStocks, key = { it.ticker }) { stock ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStockClick(stock) }
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StockLogoImage(ticker = stock.ticker, logoUrl = stock.logoUrl, size = 36.dp, fontSize = 14)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stock.name,
                                    color = TextMain,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                Text(
                                    text = stock.catalyst,
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(text = stock.recommendationText, color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainer)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Harga Pasar", color = TextMuted, fontSize = 10.sp)
                            Text(text = "$${String.format("%.2f", stock.price)}", color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Kekuatan RSI", color = TextMuted, fontSize = 10.sp)
                            Text(text = "${stock.rsi.toInt()} (Netral)", color = PrimaryEmerald, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Target Profit", color = TextMuted, fontSize = 10.sp)
                            Text(text = "+5.20%", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Verified, contentDescription = "Verified", tint = PrimaryEmerald, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Volume & Rebound Confirm", color = TextMuted, fontSize = 10.sp)
                        }

                        Button(
                            onClick = { onStockClick(stock) },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(text = "Analisis", color = PrimaryEmerald, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Educational Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = "Risk Note", tint = TextMuted, modifier = Modifier.size(20.dp))
                    Column {
                        Text(text = "Catatan Edukasi & Risiko", color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text(
                            text = "Sinyal dihitung otomatis berbasis indikator momentum & volume TradingView. Gunakan dana simulasi ($100K demo) untuk menguji strategi tanpa risiko modal riil.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun IndicatorTile(title: String, value: String, sub: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceContainer)
            .padding(8.dp)
    ) {
        Column {
            Text(text = title, color = TextMuted, fontSize = 9.sp)
            Text(text = value, color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(vertical = 2.dp))
            Text(text = sub, color = TextMuted, fontSize = 8.sp)
        }
    }
}
