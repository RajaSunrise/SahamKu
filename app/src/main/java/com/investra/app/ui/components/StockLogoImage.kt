package com.investra.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.investra.app.ui.theme.SurfaceContainerHigh
import com.investra.app.ui.theme.TextMain

@Composable
fun StockLogoImage(
    ticker: String,
    logoUrl: String? = null,
    size: Dp = 38.dp,
    fontSize: Int = 16
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceContainerHigh),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = ticker.take(2).uppercase(),
            color = TextMain,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize.sp
        )
    }
}
