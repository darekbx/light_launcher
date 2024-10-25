package com.darekbx.lightlauncher.ui.settings.favourites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.lightlauncher.R
import com.darekbx.lightlauncher.system.model.FavouriteApplication
import com.darekbx.lightlauncher.ui.Loading
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
                FavouriteApplicationsList(
                    it.applications,
                    setFavourite = { application, isFavourite ->
                        favouritesViewModel.setFavourite(application, isFavourite)
                    })
            }

            else -> Loading()
        }
    }
}

@Composable
fun FavouriteApplicationsList(
    applications: List<FavouriteApplication>,
    setFavourite: (FavouriteApplication, Boolean) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            //.background(MaterialTheme.colorScheme.background)
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
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(applications, { it }) { item ->
                FavouriteApplicationView(modifier = Modifier.fillMaxWidth(), item, setFavourite)
            }
        }
    }
}

@Composable
fun FavouriteApplicationView(
    modifier: Modifier = Modifier,
    application: FavouriteApplication,
    setFavourite: (FavouriteApplication, Boolean) -> Unit = { _, _ -> }
) {
    var isFavourite by rememberSaveable(application) { mutableStateOf(application.isFavourite) }
    Row(
        modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .semantics { testTag = "favourite_application_view" }
            .clickable {
                isFavourite = !isFavourite
                setFavourite(application, isFavourite)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isFavourite) {
            Icon(
                modifier = Modifier.semantics { testTag = "favourite_application_checked" },
                imageVector = Icons.Filled.Star,
                contentDescription = "favourite",
                tint = MaterialTheme.colorScheme.onBackground
            )
        } else {
            Icon(
                modifier = Modifier.semantics { testTag = "favourite_application_unchecked" },
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
        FavouriteApplicationsList(
            listOf(
                FavouriteApplication("", "", "Google Maps", true),
                FavouriteApplication("", "", "Phone", false),
                FavouriteApplication("", "", "Messages", false),
                FavouriteApplication("", "", "Settings", false),
                FavouriteApplication("", "", "Photos", false),
            )
        )
    }
}

@Preview
@Composable
fun EmptyApplicationsListPreview() {
    LightLauncherTheme {
        FavouriteApplicationsList(listOf())
    }
}
