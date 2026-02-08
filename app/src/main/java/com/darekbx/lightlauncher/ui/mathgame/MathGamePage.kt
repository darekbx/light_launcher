package com.darekbx.lightlauncher.ui.mathgame

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MathGamePage(onBack: () -> Unit = { }) {
    BackHandler { onBack() }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.BottomEnd),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(24.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "forward"
            )
            // Just to keep correct spacing
            repeat(2) {
                Icon(
                    modifier = Modifier.padding(24.dp),
                    tint = Color.Transparent,
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "up"
                )
            }
        }

    }
}