package com.darekbx.lightlauncher.ui.userapplications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.ui.Loading
import com.darekbx.lightlauncher.ui.theme.LightLauncherTheme
import com.darekbx.lightlauncher.ui.theme.fontFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun UserApplicationsScreen(
    userApplicationsViewModel: UserApplicationsViewModel = koinViewModel(),
    onSettingsClick: () -> Unit
) {
    val state by userApplicationsViewModel.uiState
    LaunchedEffect(Unit) {
        userApplicationsViewModel.loadApplications()
    }
    state.let {
        when (it) {
            is UserApplicationsUiState.Done -> {
                UserApplicationsList(it.applications, onSettingsClick)
            }

            else -> Loading()
        }
    }
}

@Composable
fun UserApplicationsList(
    applications: List<Application>,
    onSettingsClick: () -> Unit = { }
) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            items(applications) { item ->
                UserApplicationView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = context
                                .packageManager
                                .getLaunchIntentForPackage(item.packageName)
                            context.startActivity(intent)
                        },
                    item
                )
            }
        }

        TextButton(modifier = Modifier.align(Alignment.BottomEnd), onClick = { onSettingsClick() }) {
            Text(
                text = "Settings",
                fontFamily = fontFamily
            )
        }
    }
}

@Composable
fun UserApplicationView(
    modifier: Modifier = Modifier,
    application: Application
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
            text = application.label,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = fontFamily
        )
    }
}

@Preview
@Composable
fun UserApplicationPreview() {
    LightLauncherTheme {
        UserApplicationsList(
            listOf(
                Application("", "Google Maps"),
                Application("", "Phone"),
                Application("", "Messages")
            )
        )
    }
}
