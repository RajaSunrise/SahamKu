package com.investra.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import com.investra.app.data.model.Position
import com.investra.app.data.model.Stock
import com.investra.app.ui.MainViewModel
import com.investra.app.ui.components.StockLogoImage
import com.investra.app.ui.theme.PrimaryContainer
import com.investra.app.ui.theme.PrimaryEmerald
import com.investra.app.ui.theme.SurfaceContainer
import com.investra.app.ui.theme.SurfaceContainerHigh
import com.investra.app.ui.theme.SurfaceContainerLow
import com.investra.app.ui.theme.SurfaceContainerLowest
import com.investra.app.ui.theme.TertiaryContainer
import com.investra.app.ui.theme.TextMain
import com.investra.app.ui.theme.TextMuted

@Composable
fun DemoBuyDialog(
    stock: Stock,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val virtualCash by viewModel.virtualCash.collectAsState()
    var sharesText by remember { mutableStateOf("50") }
    var orderType by remember { mutableStateOf("Market Order") }
    var tpChecked by remember { mutableStateOf(true) }
    var slChecked by remember { mutableStateOf(true) }

    val shares = sharesText.toDoubleOrNull() ?: 0.0
    val totalCost = stock.price * shares
    val tpProfit = shares * (stock.price * 0.08)
    val slLoss = shares * (stock.price * 0.03)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceContainerHigh,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StockLogoImage(ticker = stock.ticker, logoUrl = stock.logoUrl, size = 36.dp, fontSize = 16)
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = stock.ticker, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = stock.exchange, color = TextMuted, fontSize = 10.sp)
                            }
                            Text(text = stock.name, color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                // Price & Cash Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainer)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Harga Eksekusi Real-time", color = TextMuted, fontSize = 10.sp)
                        Text(text = "$${String.format("%.2f", stock.price)} USD", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Saldo Kas Virtual", color = TextMuted, fontSize = 10.sp)
                        Text(text = "$${String.format("%,.2f", virtualCash)} USD", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                // Order Type Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerLowest)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("Market Order", "Limit Order", "Stop Limit").forEach { type ->
                        val isSelected = orderType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryEmerald else Color.Transparent)
                                .clickable { orderType = type }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type,
                                color = if (isSelected) Color(0xFF003824) else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                // Shares Input Control (Typed Decimal Input & Stepper)
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Jumlah Lembar Saham", color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text(text = "Dapat ketik desimal/koma", color = PrimaryEmerald, fontSize = 10.sp)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceContainer)
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    val current = sharesText.toDoubleOrNull() ?: 0.0
                                    val newShares = (current - 1.0).coerceAtLeast(0.1)
                                    sharesText = if (newShares % 1.0 == 0.0) "${newShares.toInt()}" else String.format("%.2f", newShares)
                                },
                                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceContainerHigh)
                            ) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "Minus", tint = TextMain)
                            }

                            OutlinedTextField(
                                value = sharesText,
                                onValueChange = { input ->
                                    // Replace comma with dot for Indonesian keyboard compatibility
                                    sharesText = input.replace(',', '.')
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryEmerald,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = TextMain,
                                    unfocusedTextColor = TextMain
                                ),
                                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                            )

                            IconButton(
                                onClick = {
                                    val current = sharesText.toDoubleOrNull() ?: 0.0
                                    val newShares = current + 1.0
                                    sharesText = if (newShares % 1.0 == 0.0) "${newShares.toInt()}" else String.format("%.2f", newShares)
                                },
                                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceContainerHigh)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Plus", tint = TextMain)
                            }
                        }

                        // Allocation Quick Select Chips
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(10, 25, 50, 100).forEach { pct ->
                                val targetShares = (virtualCash * (pct / 100.0)) / stock.price
                                val chipText = if (targetShares % 1.0 == 0.0) "${targetShares.toInt()}" else String.format("%.2f", targetShares)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SurfaceContainer)
                                        .clickable { sharesText = chipText }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = if (pct == 100) "Max" else "$pct%", color = TextMain, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }

                // Protection Toggles (TP & SL)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = "Protection", tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Proteksi Otomatis Demo", color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainer)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = tpChecked,
                                onCheckedChange = { tpChecked = it },
                                colors = CheckboxDefaults.colors(checkedColor = PrimaryEmerald)
                            )
                            Column {
                                Text(text = "Take Profit (+8.0%)", color = TextMain, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Target: $${String.format("%.2f", stock.price * 1.08)}", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                        Text(text = "+$${String.format("%.2f", tpProfit)}", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainer)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = slChecked,
                                onCheckedChange = { slChecked = it },
                                colors = CheckboxDefaults.colors(checkedColor = TertiaryContainer)
                            )
                            Column {
                                Text(text = "Stop Loss (-3.0%)", color = TextMain, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Target: $${String.format("%.2f", stock.price * 0.97)}", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                        Text(text = "-$${String.format("%.2f", slLoss)}", color = TertiaryContainer, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                // Total Cost Breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Nilai Investasi Demo", color = TextMuted, fontSize = 11.sp)
                    Text(text = "$${String.format("%,.2f", totalCost)} USD", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        val sl = if (slChecked) stock.price * 0.97 else null
                        val tp = if (tpChecked) stock.price * 1.08 else null
                        val success = viewModel.executeBuy(stock, shares, orderType, sl, tp)
                        if (success) {
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Buy", tint = Color(0xFF003824), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Konfirmasi Beli Demo ($${String.format("%,.2f", totalCost)})", color = Color(0xFF003824), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun DemoSellDialog(
    position: Position,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var sellSharesPct by remember { mutableStateOf(100) }
    val sharesToSell = (position.shares * (sellSharesPct / 100.0)).coerceAtLeast(0.01)
    val grossValue = sharesToSell * position.currentPrice
    val costBasis = sharesToSell * position.avgBuyPrice
    val realizedPnL = grossValue - costBasis
    val pnlPct = if (costBasis > 0) (realizedPnL / costBasis) * 100 else 0.0

    val positionSharesText = if (position.shares % 1.0 == 0.0) "${position.shares.toInt()}" else String.format("%.2f", position.shares)
    val sellSharesText = if (sharesToSell % 1.0 == 0.0) "${sharesToSell.toInt()}" else String.format("%.2f", sharesToSell)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceContainerHigh,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StockLogoImage(ticker = position.ticker, logoUrl = position.logoUrl, size = 36.dp, fontSize = 16)
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = position.ticker, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = position.exchange, color = TextMuted, fontSize = 10.sp)
                            }
                            Text(text = position.name, color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                // Stats Summary
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainer)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Posisi Aktif", color = TextMuted, fontSize = 10.sp)
                        Text(text = "$positionSharesText Lembar", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Harga Pasar Real-time", color = TextMuted, fontSize = 10.sp)
                        Text(text = "$${String.format("%.2f", position.currentPrice)}", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                // Volume Selector Buttons
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Volume yang Ingin Dijual: $sellSharesText Lembar ($sellSharesPct%)", color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(25, 50, 75, 100).forEach { pct ->
                            val isSelected = sellSharesPct == pct
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) PrimaryEmerald else SurfaceContainer)
                                    .clickable { sellSharesPct = pct }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (pct == 100) "Semua" else "$pct%",
                                    color = if (isSelected) Color(0xFF003824) else TextMain,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Realized PnL Projection
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "ESTIMASI CUAN BERSIH TEREALISASI", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${if (realizedPnL >= 0) "+" else ""}$${String.format("%,.2f", realizedPnL)} USD",
                            color = if (realizedPnL >= 0) PrimaryEmerald else TertiaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "${if (pnlPct >= 0) "+" else ""}${String.format("%.2f", pnlPct)}% ROI",
                            color = if (realizedPnL >= 0) PrimaryEmerald else TertiaryContainer,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Button(
                    onClick = {
                        val success = viewModel.executeSell(position.ticker, sharesToSell)
                        if (success) {
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text(
                        text = "Konfirmasi Jual (${if (realizedPnL >= 0) "+" else ""}$${String.format("%,.2f", realizedPnL)})",
                        color = Color(0xFF003824),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
