package com.darekbx.lightlauncher.ui.settings

import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.system.model.OrderedApplication
import com.darekbx.lightlauncher.ui.theme.LightLauncherTheme
import com.darekbx.lightlauncher.ui.theme.fontFamily
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun ApplicationSettingsScreen(
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    var applications by remember { mutableStateOf(listOf<OrderedApplication>()) }
    LaunchedEffect(Unit) {
        applications = settingsViewModel.loadApplications()
    }
    ApplicationsList(applications)
}

@Composable
private fun ApplicationsList(applications: List<OrderedApplication>) {
    if (applications.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(64.dp))
        }
    } else {

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = "applications settings",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = fontFamily
            )

            val data = remember { mutableStateOf(applications) }
            val state = rememberReorderableLazyListState(onMove = { from, to ->
                data.value = data.value.toMutableList().apply {
                    add(to.index, removeAt(from.index))
                }
            })
            LazyColumn(
                state = state.listState,
                modifier = Modifier
                    .reorderable(state)
                    .detectReorderAfterLongPress(state)
            ) {
                items(data.value, { it }) { item ->
                    ReorderableItem(state, key = item) { isDragging ->
                        val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
                        ApplicationView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(elevation.value), item
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationView(modifier: Modifier = Modifier, application: OrderedApplication) {
    Row(
        modifier.padding(top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (application.order != null) {
            Text(
                modifier = Modifier
                    .width(48.dp)
                    .padding(end = 8.dp),
                text = "#${application.order}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = fontFamily
            )
        } else {
            Spacer(modifier = Modifier.width(38.dp))
        }
        Text(
            modifier = Modifier,
            text = application.label,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = fontFamily
        )
    }
}

@Preview
@Composable
fun ApplicationsListPreview() {
    LightLauncherTheme {
        ApplicationsList(
            listOf(
                OrderedApplication("", "Google Maps", 12, 1L),
                OrderedApplication("", "Phone", 2, 2L),
                OrderedApplication("", "Messages", null, null),
                OrderedApplication("", "Settings", null, null),
                OrderedApplication("", "Photos", null, null),
            )
        )
    }
}

@Preview
@Composable
fun EmptyApplicationsListPreview() {
    LightLauncherTheme {
        ApplicationsList(listOf())
    }
}
