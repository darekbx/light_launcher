package com.darekbx.lightlauncher.ui.stocks

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.darekbx.lightlauncher.repository.remote.stocks.StocksProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun StockWidget(
    type: StocksProvider.StockType,
    stocksProvider: StocksProvider = koinInject()
) {
    var isLoading by remember { mutableStateOf(false) }
    var previousValue by remember { mutableDoubleStateOf(-1.0) }
    var currentValue by remember { mutableDoubleStateOf(-1.0) }

    LifecycleResumeEffect(type) {
        isLoading = true
        val job = CoroutineScope(Dispatchers.IO).launch {
            stocksProvider.fetch(type)?.let {
                previousValue = currentValue
                currentValue = it
            }
            isLoading = false
        }
        onPauseOrDispose { job.cancel() }
    }

    when {
        isLoading -> Loading()
        currentValue > 0.0 -> StockWidgetText(currentValue, previousValue)
        else -> { /* Error */ }
    }
}

@Composable
private fun Loading() {
    LinearProgressIndicator(
        modifier = Modifier
            .height(10.dp)
            .width(60.dp)
            .padding(4.dp)
    )
}

@Composable
private fun StockWidgetText(value: Double, previous: Double) {

    val color = when {
        previous != -1.0 && value > previous -> Color(56, 142, 60)
        previous != -1.0 && value < previous -> Color(229, 115, 115)
        else -> Color.LightGray
    }

    val unit = "zł"
    Text(
        text = "%.2f$unit".format(value),
        style = MaterialTheme.typography.labelSmall,
        letterSpacing = 0.sp,
        fontSize = 9.sp,
        lineHeight = 9.sp,
        textAlign = TextAlign.End,
        color = color,
        modifier = Modifier
            .height(10.dp)
            .width(60.dp)
            .padding(start = 4.dp, end = 4.dp)
    )
}

@Preview
@Composable
fun StockWidgetTextEqualPreview() {
    StockWidgetText(12452.21421, 12452.21421)
}

@Preview
@Composable
fun StockWidgetTextGreatedPreview() {
    StockWidgetText(12452.21421, 12512.21)
}

@Preview
@Composable
fun StockWidgetTextLowerPreview() {
    StockWidgetText(12452.21421, 11213.21)
}

@Preview
@Composable
fun LoadingPreview() {
    Loading()
}
