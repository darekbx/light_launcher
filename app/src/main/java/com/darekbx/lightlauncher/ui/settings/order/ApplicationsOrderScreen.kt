package com.darekbx.lightlauncher.ui.settings.order

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.lightlauncher.system.model.ApplicationOrder
import com.darekbx.lightlauncher.ui.Loading
import com.darekbx.lightlauncher.ui.theme.LightLauncherTheme
import com.darekbx.lightlauncher.ui.theme.fontFamily
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.koin.androidx.compose.koinViewModel

@Composable
fun ApplicationsOrderScreen(
    orderViewModel: OrderViewModel = koinViewModel()
) {
    val state by orderViewModel.uiState
    LaunchedEffect(Unit) {
        orderViewModel.loadApplicationsOrder()
    }
    state.let {
        when (it) {
            is OrderedUiState.Done -> {
                ApplicationsOrderList(it.applications, setOrder = orderViewModel::setOrder)
            }

            else -> Loading()
        }
    }
}

@Composable
fun ApplicationsOrderList(
    applications: List<ApplicationOrder>,
    setOrder: (List<ApplicationOrder>) -> Unit = { _ -> }
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = "applications order",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = fontFamily
        )
        val data = remember { mutableStateOf(applications) }
        val state = rememberReorderableLazyListState(
            onDragEnd = { fromIndex, toIndex ->
                val order = data.value.mapIndexed { index, application ->
                    application.copy(order = index)
                }
                setOrder(order)
            },
            onMove = { from, to ->
                data.value = data.value.toMutableList().apply {
                    val fromItem = removeAt(from.index)
                    add(to.index, fromItem)
                }
            }
        )
        LazyColumn(
            state = state.listState,
            modifier = Modifier
                .reorderable(state)
                .detectReorderAfterLongPress(state)
        ) {
            items(data.value, { it }) { item ->
                ReorderableItem(state, key = item) { isDragging ->
                    val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp, label = "")
                    ApplicationOrderView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation.value), item
                    )
                }
            }
        }
    }
}

@Composable
fun ApplicationOrderView(
    modifier: Modifier = Modifier,
    application: ApplicationOrder
) {
    Row(
        modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .semantics { testTag = "favourite_application_view" },
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = application.label,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = fontFamily
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = "(${application.order})",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5F),
            fontFamily = fontFamily
        )
    }
}

@Preview
@Composable
fun ApplicationsListPreview() {
    LightLauncherTheme {
        ApplicationsOrderList(
            listOf(
                ApplicationOrder("", "", "Google Maps", null),
                ApplicationOrder("", "", "Phone", null),
                ApplicationOrder("", "", "Messages", null)
            )
        )
    }
}