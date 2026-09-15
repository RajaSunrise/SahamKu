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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.investra.app.ui.theme.SurfaceContainerHigh
import com.investra.app.ui.theme.TextMain

@Composable
fun StockLogoImage(
    ticker: String,
    logoUrl: String? = null,
    size: Dp = 38.dp,
    fontSize: Int = 16
) {
    val context = LocalContext.current

    if (!logoUrl.isNullOrBlank()) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(logoUrl)
                .decoderFactory(SvgDecoder.Factory())
                .crossfade(true)
                .build(),
            contentDescription = "$ticker logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceContainerHigh),
            loading = {
                FallbackBadge(ticker = ticker, size = size, fontSize = fontSize)
            },
            error = {
                FallbackBadge(ticker = ticker, size = size, fontSize = fontSize)
            }
        )
    } else {
        FallbackBadge(ticker = ticker, size = size, fontSize = fontSize)
    }
}

@Composable
private fun FallbackBadge(
    ticker: String,
    size: Dp,
    fontSize: Int
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
