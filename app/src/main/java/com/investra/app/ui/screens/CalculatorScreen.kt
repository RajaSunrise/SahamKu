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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.investra.app.ui.MainViewModel
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
fun CalculatorScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onApplyToOrder: (Double, Int) -> Unit
) {
    val calcState by viewModel.calculatorState.collectAsState()

    var entryText by remember { mutableStateOf(calcState.entryPrice.toString()) }
    var sharesText by remember { mutableStateOf(calcState.shares.toString()) }
    var targetText by remember { mutableStateOf(calcState.targetPrice.toString()) }
    var stopText by remember { mutableStateOf(calcState.stopLossPrice.toString()) }
    var selectedTicker by remember { mutableStateOf(calcState.ticker) }

    val entry = entryText.toDoubleOrNull() ?: 142.50
    val shares = sharesText.toIntOrNull() ?: 100
    val target = targetText.toDoubleOrNull() ?: 162.00
    val stop = stopText.toDoubleOrNull() ?: 135.00

    val capital = entry * shares
    val profitPerShare = target - entry
    val profitTotal = profitPerShare * shares
    val profitPct = if (entry > 0) (profitPerShare / entry) * 100 else 0.0

    val lossPerShare = entry - stop
    val lossTotal = lossPerShare * shares
    val lossPct = if (entry > 0) (lossPerShare / entry) * 100 else 0.0

    val rrRatio = if (lossPerShare > 0) profitPerShare / lossPerShare else 0.0

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.QueryStats, contentDescription = "Calc", tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Kalkulator Cuan & Risiko", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(text = "Simulasikan target harga, profit %, dan proteksi rugi", color = TextMuted, fontSize = 11.sp)
                }
            }
        }

        // Ticker Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val stockList = listOf("NVDA" to 142.50, "AAPL" to 228.20, "TSLA" to 254.30)
                items(stockList) { (t, p) ->
                    val isSelected = selectedTicker == t
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) PrimaryEmerald else SurfaceContainerHigh)
                            .clickable {
                                selectedTicker = t
                                entryText = p.toString()
                                targetText = (p * 1.12).toString()
                                stopText = (p * 0.95).toString()
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "$t ($${String.format("%.2f", p)})",
                            color = if (isSelected) Color(0xFF003824) else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Calculation Inputs Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Harga Beli (Entry)", color = TextMuted, fontSize = 11.sp)
                            OutlinedTextField(
                                value = entryText,
                                onValueChange = { entryText = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = SurfaceContainerHigh),
                                modifier = Modifier.fillMaxWidth().height(52.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Jumlah Saham (Shares)", color = TextMuted, fontSize = 11.sp)
                            OutlinedTextField(
                                value = sharesText,
                                onValueChange = { sharesText = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = SurfaceContainerHigh),
                                modifier = Modifier.fillMaxWidth().height(52.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(SurfaceContainerLow).padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Estimasi Total Modal", color = TextMuted, fontSize = 11.sp)
                        Text(text = "$${String.format("%,.2f", capital)} USD", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Target Jual (Take Profit)", color = PrimaryEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "+${String.format("%.2f", profitPct)}%", color = PrimaryEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedTextField(
                            value = targetText,
                            onValueChange = { targetText = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryEmerald, unfocusedBorderColor = SurfaceContainerHigh),
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Batas Rugi (Stop Loss)", color = TertiaryContainer, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "-${String.format("%.2f", lossPct)}%", color = TertiaryContainer, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedTextField(
                            value = stopText,
                            onValueChange = { stopText = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TertiaryContainer, unfocusedBorderColor = SurfaceContainerHigh),
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        )
                    }
                }
            }
        }

        // Potential Profit & Loss Grid
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "POTENSI CUAN", color = PrimaryEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Icon(imageVector = Icons.Default.ArrowOutward, contentDescription = "Profit", tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                        }
                        Text(text = "+$${String.format("%,.2f", profitTotal)}", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "+${String.format("%.2f", profitPct)}% Profit", color = PrimaryEmerald, fontSize = 10.sp)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "POTENSI RISIKO", color = TertiaryContainer, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Icon(imageVector = Icons.Default.CallReceived, contentDescription = "Risk", tint = TertiaryContainer, modifier = Modifier.size(16.dp))
                        }
                        Text(text = "-$${String.format("%,.2f", lossTotal)}", color = TertiaryContainer, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "-${String.format("%.2f", lossPct)}% Proteksi", color = TertiaryContainer, fontSize = 10.sp)
                    }
                }
            }
        }

        // Risk Reward Ratio Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Balance, contentDescription = "R:R", tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Rasio Risk : Reward", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(PrimaryEmerald.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(text = if (rrRatio >= 2.0) "Sangat Ideal" else "Risiko Sedang", color = PrimaryEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(text = "1 : ${String.format("%.2f", rrRatio)}", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Text(text = "Setiap $1 risiko berpeluang raih $${String.format("%.2f", rrRatio)}", color = TextMuted, fontSize = 11.sp)
                }
            }
        }

        // Apply & Reset Actions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        viewModel.updateCalculator(entry, shares, target, stop, selectedTicker)
                        onApplyToOrder(entry, shares)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Apply", tint = Color(0xFF003824), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Terapkan ke Order Simulasi", color = Color(0xFF003824), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        entryText = "142.50"
                        sharesText = "100"
                        targetText = "162.00"
                        stopText = "135.00"
                        viewModel.showToast("Kalkulator direset!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = "Reset", tint = TextMain, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Reset Kalkulasi", color = TextMain, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
