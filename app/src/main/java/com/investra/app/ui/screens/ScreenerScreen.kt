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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Radar
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.investra.app.data.model.Stock
import com.investra.app.ui.MainViewModel
import com.investra.app.ui.theme.PrimaryEmerald
import com.investra.app.ui.theme.SecondaryBlue
import com.investra.app.ui.theme.SurfaceContainer
import com.investra.app.ui.theme.SurfaceContainerHigh
import com.investra.app.ui.theme.SurfaceContainerLow
import com.investra.app.ui.theme.SurfaceContainerHighest
import com.investra.app.ui.theme.TextMain
import com.investra.app.ui.theme.TextMuted

@Composable
fun ScreenerScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onStockClick: (Stock) -> Unit,
    onQuickBuyClick: (Stock) -> Unit
) {
    val filterState by viewModel.screenerFilter.collectAsState()
    val presets = listOf("Breakout 52W High", "RSI Oversold (<30)", "Golden Cross EMA", "Volume Spike >2x", "MACD Reversal")

    val screenResults = listOf(
        Stock("AVGO", "Broadcom Inc.", "NASDAQ", 182.40, 7.18, 4.10, 5200000000.0, "5.2B", 0.92, "STRONG BREAKOUT", 64.0, "Breakout All-Time High", 175.0, 168.0, "Semiconductors", "Technology", 8.5e11, "Catalyst: AI Custom Chips", 178.0, 185.0, 198.0, 210.0, 175.0),
        Stock("QCOM", "Qualcomm Inc.", "NASDAQ", 169.20, 5.24, 3.20, 3800000000.0, "3.8B", 0.82, "GOLDEN CROSS", 58.0, "EMA 20/50 Cross", 162.0, 155.0, "Wireless Tech", "Technology", 1.8e11, "Support dinamis MA200 mantul", 165.0, 175.0, 184.0, 195.0, 162.0),
        Stock("CRM", "Salesforce Inc.", "NYSE", 288.50, 7.72, 2.75, 4100000000.0, "4.1B", 0.80, "BULL FLAG", 61.0, "Consolidation Breakout", 280.0, 272.0, "Cloud Software", "Technology", 2.8e11, "Saluran konsolidasi 14 hari", 282.0, 295.0, 304.0, 320.0, 278.0),
        Stock("META", "Meta Platforms Inc.", "NASDAQ", 588.00, 10.96, 1.90, 8900000000.0, "8.9B", 0.88, "TREND STRONG", 65.0, "Higher High Momentum", 570.0, 550.0, "Interactive Media", "Communication", 1.5e12, "Histogram MACD melebar", 575.0, 600.0, 620.0, 650.0, 568.0)
    )

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
                    Text(text = "Radar & Screener Teknikal AS", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "Filter emiten berdasarkan pola & indikator kuantitatif", color = TextMuted, fontSize = 11.sp)
                }
            }
        }

        // Status Pindai Mesin Widget
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Radar, contentDescription = "Radar", tint = PrimaryEmerald, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(text = "Status Pindai Mesin", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "504 emiten S&P 500 dipantau", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "4 Sinyal", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Konfirmasi Kuat", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        }

        // Momentum Presets
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = "Tune", tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Preset Momentum Cepat", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Text(text = "Reset", color = PrimaryEmerald, fontSize = 11.sp, modifier = Modifier.clickable { viewModel.setScreenerPreset("Breakout 52W High") })
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(presets) { p ->
                        val isSelected = filterState.preset == p
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) PrimaryEmerald else SurfaceContainer)
                                .clickable { viewModel.setScreenerPreset(p) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = p,
                                color = if (isSelected) Color(0xFF003824) else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Parameter Kustom
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter", tint = SecondaryBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Parameter Kustom", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(SurfaceContainerHighest).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text(text = "2 Aktif", color = TextMuted, fontSize = 10.sp)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "KAPITALISASI PASAR", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(SurfaceContainerHigh).padding(8.dp)
                            ) {
                                Column {
                                    Text(text = "Mega Cap", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text(text = "> $200B", color = TextMuted, fontSize = 10.sp)
                                }
                            }
                            Box(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(SurfaceContainerHigh).padding(8.dp)
                            ) {
                                Column {
                                    Text(text = "Large Cap", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text(text = "$10B - $200B", color = TextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Screening Results List
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Hasil Skrining Potensial (${screenResults.size} Saham)", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = "Urutkan: RSI", color = TextMuted, fontSize = 11.sp)
            }
        }

        items(screenResults) { stock ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().clickable { onStockClick(stock) }
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = stock.ticker.take(1), color = TextMain, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = stock.ticker, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = stock.exchange, color = TextMuted, fontSize = 10.sp)
                                }
                                Text(text = stock.name, color = TextMuted, fontSize = 11.sp)
                            }
                        }

                        Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(PrimaryEmerald.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                            Text(text = stock.recommendationText, color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        Column {
                            Text(text = "$${String.format("%.2f", stock.price)}", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.NorthEast, contentDescription = "Up", tint = PrimaryEmerald, modifier = Modifier.size(14.dp))
                                Text(text = "+${String.format("%.2f", stock.changePercent)}%", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        // Sparkline
                        Canvas(modifier = Modifier.width(100.dp).height(28.dp)) {
                            val path = Path().apply {
                                moveTo(0f, size.height * 0.8f)
                                lineTo(size.width * 0.3f, size.height * 0.5f)
                                lineTo(size.width * 0.6f, size.height * 0.6f)
                                lineTo(size.width, size.height * 0.1f)
                            }
                            drawPath(path = path, color = PrimaryEmerald, style = Stroke(width = 2.dp.toPx()))
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.toggleWatchlist(stock.ticker) },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = "Watchlist", tint = TextMain, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Watchlist", color = TextMain, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onQuickBuyClick(stock) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Bolt, contentDescription = "Beli", tint = Color(0xFF003824), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Beli Demo", color = Color(0xFF003824), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
