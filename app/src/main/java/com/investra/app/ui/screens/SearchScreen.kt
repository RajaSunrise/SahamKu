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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.investra.app.data.model.Stock
import com.investra.app.ui.MainViewModel
import com.investra.app.ui.components.StockLogoImage
import com.investra.app.ui.theme.PrimaryEmerald
import com.investra.app.ui.theme.SurfaceContainer
import com.investra.app.ui.theme.SurfaceContainerHigh
import com.investra.app.ui.theme.SurfaceContainerHighest
import com.investra.app.ui.theme.SurfaceContainerLow
import com.investra.app.ui.theme.TertiaryContainer
import com.investra.app.ui.theme.TextMain
import com.investra.app.ui.theme.TextMuted

@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onStockClick: (Stock) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val popularGainers by viewModel.gainers.collectAsState()

    val focusRequester = remember { FocusRequester() }

    val popularSearchTags = listOf("NVDA", "AAPL", "TSLA", "MSFT", "AMZN", "PLTR", "AMD", "META", "GOOGL")

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Header Bar
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
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text(text = "Cari ticker/nama saham (cth: NVDA, Apple)...", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = PrimaryEmerald)
                    },
                    trailingIcon = {
                        if (isSearching) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = PrimaryEmerald)
                        } else if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainer),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextMain,
                        unfocusedTextColor = TextMain
                    ),
                    singleLine = true
                )
            }
        }

        // Quick Popular Stock Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Pencarian Populer & Trending", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(popularSearchTags) { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(SurfaceContainerHigh)
                                .clickable { viewModel.onSearchQueryChanged(tag) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.TrendingUp, contentDescription = "Trending", tint = PrimaryEmerald, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = tag, color = TextMain, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Search Results Listing or Popular Stocks Preview
        if (searchQuery.isNotBlank()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hasil Pencarian ('$searchQuery')",
                        color = TextMain,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${searchResults.size} ditemukan",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            if (searchResults.isEmpty() && !isSearching) {
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
                            Text(text = "Tidak Ada Saham Ditemukan", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Coba gunakan kode ticker unik seperti NVDA, TSLA, AAPL, atau MSFT.", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            } else {
                items(searchResults) { stock ->
                    SearchResultCardItem(stock = stock, onClick = { onStockClick(stock) })
                }
            }
        } else {
            item {
                Text(text = "Rekomendasi Saham Valuasi > $5B", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            items(popularGainers) { stock ->
                SearchResultCardItem(stock = stock, onClick = { onStockClick(stock) })
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun SearchResultCardItem(
    stock: Stock,
    onClick: () -> Unit
) {
    val isPositive = stock.changePercent >= 0

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StockLogoImage(ticker = stock.ticker, logoUrl = stock.logoUrl, size = 38.dp, fontSize = 15)
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stock.ticker, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SurfaceContainerHighest)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(text = stock.exchange, color = TextMuted, fontSize = 9.sp)
                        }
                    }
                    Text(text = stock.name, color = TextMuted, fontSize = 11.sp, maxLines = 1)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%.2f", stock.price)}",
                    color = TextMain,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isPositive) PrimaryEmerald.copy(alpha = 0.15f) else TertiaryContainer.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${if (isPositive) "+" else ""}${String.format("%.2f", stock.changePercent)}%",
                        color = if (isPositive) PrimaryEmerald else TertiaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
