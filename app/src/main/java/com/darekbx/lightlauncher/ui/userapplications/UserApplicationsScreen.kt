package com.darekbx.lightlauncher.ui.userapplications

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun UserApplicationsScreen(
    onSettingsClick: () -> Unit
) {
    Button(onClick = onSettingsClick) {
        Text(text = "settings")
    }
}