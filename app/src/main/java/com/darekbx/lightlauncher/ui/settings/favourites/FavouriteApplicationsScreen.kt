package com.darekbx.lightlauncher.ui.settings.favourites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.lightlauncher.R
import com.darekbx.lightlauncher.system.model.FavouriteApplication
import com.darekbx.lightlauncher.ui.theme.LightLauncherTheme
import com.darekbx.lightlauncher.ui.theme.fontFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavouriteApplicationsScreen(
    favouritesViewModel: FavouritesViewModel = koinViewModel()
) {
    val state by favouritesViewModel.uiState
    LaunchedEffect(Unit) {
        favouritesViewModel.loadFavouriteApplications()
    }
    state.let {
        when (it) {
            is FavouritesUiState.Done -> {
                ApplicationsList(it.applications, setFavourite = { packageName, isFavourite ->
                    favouritesViewModel.setFavourite(packageName, isFavourite)
                })
            }
            else -> {
                Loading()
            }
        }
    }
}

@Composable
private fun ApplicationsList(
    applications: List<FavouriteApplication>,
    setFavourite: (String, Boolean) -> Unit = { _, _ -> }
) {
    if (applications.isEmpty()) {
        Loading()
    } else {

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = "favourite applications",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = fontFamily
            )
            /*
            TODO: reorder will on favourites apps screen
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
            }*/
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(applications, { it }) { item ->
                    ApplicationView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { setFavourite(item.packageName, !item.isFavourite) },
                        item
                    )
                }
            }
        }
    }
}

@Composable
private fun Loading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.size(64.dp))
    }
}

@Composable
fun ApplicationView(modifier: Modifier = Modifier, application: FavouriteApplication) {
    Row(
        modifier.padding(top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (application.isFavourite) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "favourite",
                tint = MaterialTheme.colorScheme.onBackground
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.is_star_outline),
                contentDescription = "make_favourite",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            modifier = Modifier.padding(start = 16.dp),
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
                FavouriteApplication("", "Google Maps", true),
                FavouriteApplication("", "Phone", false),
                FavouriteApplication("", "Messages", false),
                FavouriteApplication("", "Settings", false),
                FavouriteApplication("", "Photos", false),
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
