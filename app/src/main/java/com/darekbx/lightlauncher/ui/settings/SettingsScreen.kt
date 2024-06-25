package com.darekbx.lightlauncher.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.lightlauncher.BuildConfig
import com.darekbx.lightlauncher.ui.theme.LightLauncherTheme
import com.darekbx.lightlauncher.ui.theme.fontFamily

@Composable
fun SettingsScreen(
    openFavouriteApplications: () -> Unit = { },
    openApplicationsOrder: () -> Unit = { }
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = "settings",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = fontFamily
        )

        SettingsOption(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { openFavouriteApplications() },
            title = "favourite applications"
        )
        SettingsOption(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { openApplicationsOrder() },
            title = "applications order"
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = BuildConfig.VERSION_NAME,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = fontFamily,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SettingsOption(modifier: Modifier = Modifier, title: String) {
    Text(
        modifier = modifier.padding(top = 8.dp, bottom = 8.dp),
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        fontFamily = fontFamily
    )
}

@Preview(device = Devices.PIXEL_6A, showSystemUi = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun SettingsScreenPreview() {
    LightLauncherTheme {
        SettingsScreen()
    }
}
