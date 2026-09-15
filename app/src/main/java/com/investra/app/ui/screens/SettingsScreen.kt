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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.investra.app.ui.MainViewModel
import com.investra.app.ui.theme.AppThemeMode
import com.investra.app.ui.theme.PrimaryContainer
import com.investra.app.ui.theme.PrimaryEmerald
import com.investra.app.ui.theme.SecondaryBlue
import com.investra.app.ui.theme.SurfaceContainer
import com.investra.app.ui.theme.SurfaceContainerHigh
import com.investra.app.ui.theme.SurfaceContainerLow
import com.investra.app.ui.theme.TertiaryContainer
import com.investra.app.ui.theme.TextMain
import com.investra.app.ui.theme.TextMuted

@Composable
fun SettingsScreen(
    viewModel: MainViewModel
) {
    val virtualCash by viewModel.virtualCash.collectAsState()
    val initialCapital by viewModel.initialCapital.collectAsState()

    var selectedCapitalAmount by remember { mutableStateOf(100000.0) }
    var minRR by remember { mutableStateOf(2.0f) }
    var breakoutAlert by remember { mutableStateOf(true) }
    var stopLossAlert by remember { mutableStateOf(true) }
    var selectedTf by remember { mutableStateOf("1 Hari (Swing)") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header Title
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = "Settings", tint = PrimaryEmerald, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Pengaturan Simulator", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Text(text = "Konfigurasi parameter risiko, modal latihan, dan sinyal Wall Street", color = TextMuted, fontSize = 11.sp)
            }
        }

        // Virtual Capital Reset Card
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceContainerHigh), contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = "Wallet", tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text(text = "MODAL VIRTUAL AKTIF", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(text = "$${String.format("%,.2f", initialCapital)} USD", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                        Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(SurfaceContainerHigh).padding(horizontal = 8.dp, vertical = 2.dp)) {
                            Text(text = "Tanpa Risiko Nyata", color = SecondaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(text = "Pilih Nilai Saldo Reset:", color = TextMain, fontSize = 12.sp, fontWeight = FontWeight.Medium)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(10000.0 to "$10K", 50000.0 to "$50K", 100000.0 to "$100K", 500000.0 to "$500K").forEach { (cap, label) ->
                            val isSelected = selectedCapitalAmount == cap
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PrimaryEmerald else SurfaceContainerLow)
                                    .clickable { selectedCapitalAmount = cap }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color(0xFF003824) else TextMain,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.resetPortfolio(selectedCapitalAmount) },
                        colors = ButtonDefaults.buttonColors(containerColor = TertiaryContainer.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = "Reset", tint = TertiaryContainer, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Mulai Ulang Portofolio Simulasi", color = TertiaryContainer, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Technical Recommendations & Algorithms Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceContainerHigh), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Analytics, contentDescription = "Algo", tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text(text = "Rekomendasi & Algoritma", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Parameter komputasi teknikal & sinyal", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    // Default Timeframe
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Sinyal Default Timeframe", color = TextMain, fontSize = 12.sp)
                            Text(text = selectedTf, color = PrimaryEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("15 Menit", "4 Jam", "1 Hari (Swing)").forEach { tf ->
                                val isSelected = selectedTf == tf
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) PrimaryEmerald.copy(alpha = 0.2f) else SurfaceContainerLow)
                                        .clickable { selectedTf = tf }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = tf, color = if (isSelected) PrimaryEmerald else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Risk-Reward Slider
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Filter Min. Risk-to-Reward (R:R)", color = TextMain, fontSize = 12.sp)
                            Text(text = "1 : ${String.format("%.1f", minRR)}", color = PrimaryEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = minRR,
                            onValueChange = { minRR = it },
                            valueRange = 1.0f..4.0f,
                            steps = 5,
                            colors = SliderDefaults.colors(thumbColor = PrimaryEmerald, activeTrackColor = PrimaryEmerald, inactiveTrackColor = SurfaceContainerHigh)
                        )
                    }

                    // Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Alert Breakout & Target Price", color = TextMain, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Push notifikasi saat resistance ditembus", color = TextMuted, fontSize = 10.sp)
                        }
                        Switch(
                            checked = breakoutAlert,
                            onCheckedChange = { breakoutAlert = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF003824), checkedTrackColor = PrimaryEmerald)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Peringatan Stop Loss Mendekat", color = TextMain, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Indikator aktif bila sisa selisih <1.5%", color = TextMuted, fontSize = 10.sp)
                        }
                        Switch(
                            checked = stopLossAlert,
                            onCheckedChange = { stopLossAlert = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF003824), checkedTrackColor = PrimaryEmerald)
                        )
                    }
                }
            }
        }

        // Theme Selection Card
        item {
            val currentTheme by viewModel.themeMode.collectAsState()

            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceContainerHigh), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (currentTheme == AppThemeMode.LIGHT) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Theme",
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(text = "Mode Tampilan Aplikasi", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Pilih tema terang atau gelap untuk kenyamanan visual", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppThemeMode.values().forEach { mode ->
                            val isSelected = currentTheme == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PrimaryEmerald else SurfaceContainerLow)
                                    .clickable { viewModel.setThemeMode(mode) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mode.label,
                                    color = if (isSelected) Color(0xFF003824) else TextMain,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Preferences Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Preferensi Jam & Pasar", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    PreferenceRow(icon = Icons.Default.Schedule, title = "Zona Jam Pasar", value = "New York (EDT) & WIB")
                    PreferenceRow(icon = Icons.Default.Sell, title = "Komisi Per Eksekusi", value = "$0.00 USD (Bebas Biaya)")
                    PreferenceRow(icon = Icons.Default.CandlestickChart, title = "Tipe Grafik Utama", value = "Candlestick MA20/50")
                }
            }
        }

        // Sandbox Helper Banner
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
                    Icon(imageVector = Icons.Default.Shield, contentDescription = "Shield", tint = PrimaryEmerald, modifier = Modifier.size(20.dp))
                    Column {
                        Text(text = "Lingkungan Bebas Risiko", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(
                            text = "Seluruh transaksi, eksekusi margin, dan kalkulasi P/L dihitung secara virtual menggunakan feed harga real-time US Stock Market. Tanpa registrasi KTP atau verifikasi dana.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Version Footer
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "SahamKu US Simulator v2.4.0", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "Lingkungan Simulasi Terproteksi • Sandbox Paper Trading", color = TextMuted, fontSize = 10.sp)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun PreferenceRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
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
            Icon(imageVector = icon, contentDescription = title, tint = SecondaryBlue, modifier = Modifier.size(18.dp))
            Text(text = title, color = TextMain, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Text(text = value, color = PrimaryEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
