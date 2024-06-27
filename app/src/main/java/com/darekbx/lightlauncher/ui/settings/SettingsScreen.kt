package com.darekbx.lightlauncher.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.lightlauncher.BuildConfig
import com.darekbx.lightlauncher.ui.theme.LightLauncherTheme
import com.darekbx.lightlauncher.ui.theme.fontFamily
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

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

        UiSettings()

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
private fun UiSettings(settingsViewModel: SettingsViewModel = koinViewModel()) {
    var usePages by remember { mutableStateOf(true) }
    var pageSize by remember { mutableStateOf(PageSize.MEDIUM) }

    LaunchedEffect(Unit) {
        settingsViewModel.load { usePagesValue, pageSizeValue ->
            usePages = usePagesValue
            pageSize = PageSize.entries
                .find { pageSize -> pageSize.value == pageSizeValue }
                ?: PageSize.MEDIUM
        }
    }

    Text(
        modifier = Modifier.padding(top = 32.dp, bottom = 16.dp),
        text = "use pages for lists",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onBackground,
        fontFamily = fontFamily
    )
    Switch(
        checked = usePages,
        onCheckedChange = {
            usePages = it
            settingsViewModel.setUsePages(it)
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.background,
            checkedTrackColor = MaterialTheme.colorScheme.onBackground,
        )
    )

    Text(
        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
        text = "page size",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onBackground,
        fontFamily = fontFamily
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PageSize.entries.forEach {
            OutlinedButton(
                enabled = usePages,
                onClick = {
                    pageSize = it
                    settingsViewModel.setPageSize(it.value)
                }
            ) {
                val name = it.name
                    .lowercase()
                    .replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.ROOT)
                        else it.toString()
                    }
                Text(
                    name,
                    color = if (usePages) MaterialTheme.colorScheme.onBackground else Color.Unspecified,
                    textDecoration = if (it == pageSize) TextDecoration.Underline else null
                )
            }
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
