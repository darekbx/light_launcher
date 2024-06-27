package com.darekbx.lightlauncher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.darekbx.lightlauncher.R

private val ColorScheme = darkColorScheme(
    background = Color(22, 27, 30, 255),
    onBackground = Color.White.copy(alpha = 0.7f),
)

val fontFamily = FontFamily(Font(R.font.alliance_font))

@Composable
fun LightLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}
