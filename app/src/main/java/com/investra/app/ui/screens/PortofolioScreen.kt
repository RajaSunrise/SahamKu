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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
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
import com.investra.app.data.model.Position
import com.investra.app.ui.MainViewModel
import com.investra.app.ui.components.StockLogoImage
import com.investra.app.ui.theme.PrimaryEmerald
import com.investra.app.ui.theme.SecondaryBlue
import com.investra.app.ui.theme.SurfaceContainer
import com.investra.app.ui.theme.SurfaceContainerHigh
import com.investra.app.ui.theme.SurfaceContainerLow
import com.investra.app.ui.theme.TertiaryContainer
import com.investra.app.ui.theme.TextMain
import com.investra.app.ui.theme.TextMuted

@Composable
fun PortofolioScreen(
    viewModel: MainViewModel,
    onStockClick: (String) -> Unit,
    onSellClick: (Position) -> Unit,
    onHistoryClick: () -> Unit
) {
    val positions by viewModel.positions.collectAsState()
    val virtualCash by viewModel.virtualCash.collectAsState()
    val watchlist by viewModel.watchlist.collectAsState()
    var selectedTab by remember { mutableStateOf("open") } // "open" or "watch"

    val holdingsValue = positions.sumOf { it.currentMarketValue }
    val totalPortfolioValue = virtualCash + holdingsValue
    val totalInvestment = positions.sumOf { it.totalInvestment }
    val floatingPnL = holdingsValue - totalInvestment
    val floatingPnLPercent = if (totalInvestment > 0) (floatingPnL / totalInvestment) * 100 else 0.0

    val gainersList by viewModel.gainers.collectAsState()
    val losersList by viewModel.losers.collectAsState()
    val activeList by viewModel.activeStocks.collectAsState()
    val allMarketStocks = remember(gainersList, losersList, activeList) {
        (gainersList + losersList + activeList).associateBy { it.ticker }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Virtual Portfolio Hero Metric Card
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryEmerald))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Virtual Portfolio Value (SQLite Saved)", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(SurfaceContainerHigh).padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(text = "Paper Trading", color = SecondaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Column {
                        Text(
                            text = "$${String.format("%,.2f", totalPortfolioValue)} USD",
                            color = TextMain,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                            Icon(imageVector = Icons.Default.TrendingUp, contentDescription = "PnL", tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${if (floatingPnL >= 0) "+" else ""}$${String.format("%,.2f", floatingPnL)} (${if (floatingPnLPercent >= 0) "+" else ""}${String.format("%.2f", floatingPnLPercent)}%)",
                                color = if (floatingPnL >= 0) PrimaryEmerald else TertiaryContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Floating PnL", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    // Allocation Breakdown Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceContainerLow)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PrimaryEmerald))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Saham Dipegang", color = TextMuted, fontSize = 10.sp)
                            }
                            Text(text = "$${String.format("%,.2f", holdingsValue)}", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SecondaryBlue))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Kas Tersedia", color = TextMuted, fontSize = 10.sp)
                            }
                            Text(text = "$${String.format("%,.2f", virtualCash)}", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Tabs & History Button Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedTab == "open") PrimaryEmerald else SurfaceContainer)
                        .clickable { selectedTab = "open" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CandlestickChart, contentDescription = "Open", tint = if (selectedTab == "open") Color(0xFF003824) else TextMuted, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Posisi Terbuka (${positions.size})", color = if (selectedTab == "open") Color(0xFF003824) else TextMuted, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedTab == "watch") PrimaryEmerald else SurfaceContainer)
                        .clickable { selectedTab = "watch" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Watch", tint = if (selectedTab == "watch") Color(0xFF003824) else TextMuted, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Watchlist (${watchlist.size})", color = if (selectedTab == "watch") Color(0xFF003824) else TextMuted, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                IconButton(
                    onClick = onHistoryClick,
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceContainerHigh)
                ) {
                    Icon(imageVector = Icons.Default.History, contentDescription = "History", tint = PrimaryEmerald)
                }
            }
        }

        // Active Positions / Watchlist Listing
        if (selectedTab == "open") {
            items(positions) { pos ->
                PositionCardItem(
                    position = pos,
                    onStockClick = { onStockClick(pos.ticker) },
                    onSellClick = { onSellClick(pos) }
                )
            }
        } else {
            items(watchlist.toList()) { ticker ->
                val matchedStock = allMarketStocks[ticker]
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().clickable { onStockClick(ticker) }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StockLogoImage(ticker = ticker, logoUrl = matchedStock?.logoUrl, size = 36.dp, fontSize = 14)
                            Column {
                                Text(text = ticker, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = matchedStock?.name ?: "Saham Terpilih Watchlist", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                        Button(
                            onClick = { onStockClick(ticker) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Analisis", color = Color(0xFF003824), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun PositionCardItem(
    position: Position,
    onStockClick: () -> Unit,
    onSellClick: () -> Unit
) {
    val isPositive = position.floatingPnL >= 0
    val sharesText = if (position.shares % 1.0 == 0.0) "${position.shares.toInt()}" else String.format("%.2f", position.shares)

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().clickable { onStockClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StockLogoImage(ticker = position.ticker, logoUrl = position.logoUrl, size = 38.dp, fontSize = 16)
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = position.ticker, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = position.exchange, color = TextMuted, fontSize = 10.sp)
                        }
                        Text(text = "${position.name} • $sharesText Lembar", color = TextMuted, fontSize = 11.sp)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${if (isPositive) "+" else ""}$${String.format("%.2f", position.floatingPnL)}",
                        color = if (isPositive) PrimaryEmerald else TertiaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isPositive) PrimaryEmerald.copy(alpha = 0.15f) else TertiaryContainer.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${if (isPositive) "+" else ""}${String.format("%.2f", position.floatingPnLPercent)}%",
                            color = if (isPositive) PrimaryEmerald else TertiaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerLow)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Avg Beli", color = TextMuted, fontSize = 10.sp)
                    Text(text = "$${String.format("%.2f", position.avgBuyPrice)}", color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Harga Saat Ini", color = TextMuted, fontSize = 10.sp)
                    Text(text = "$${String.format("%.2f", position.currentPrice)}", color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onStockClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.ShowChart, contentDescription = "Analisis", tint = TextMain, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Analisis", color = TextMain, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onSellClick,
                    colors = ButtonDefaults.buttonColors(containerColor = if (isPositive) PrimaryEmerald else TertiaryContainer),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Icon(imageVector = if (isPositive) Icons.Default.MonetizationOn else Icons.Default.Close, contentDescription = "Sell", tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isPositive) "Ambil Untung" else "Cut Loss", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
