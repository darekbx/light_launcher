package com.darekbx.lightlauncher.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.darekbx.lightlauncher.system.model.ClickCount
import com.darekbx.lightlauncher.ui.theme.fontFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun StatisticsScreen(statisticsViewModel: StatisticsViewModel = koinViewModel()) {
    val data by statisticsViewModel.getClickCount().collectAsState(initial = emptyList())
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = "click statistics",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = fontFamily
        )
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            items(data) { item ->
                ClickCountView(Modifier.fillMaxWidth(), item)
            }
        }
    }
}

@Composable
fun ClickCountView(
    modifier: Modifier = Modifier,
    clickCount: ClickCount,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
            .semantics { testTag = "favourite_application_view" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = clickCount.label,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = fontFamily
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = "${clickCount.count}",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontFamily = fontFamily
        )
    }
}