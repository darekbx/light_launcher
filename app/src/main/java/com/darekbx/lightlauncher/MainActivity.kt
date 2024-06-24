package com.darekbx.lightlauncher

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.rememberNavController
import com.darekbx.lightlauncher.ui.theme.LightLauncherTheme
import com.darekbx.lightlauncher.ui.userapplications.UserApplicationsViewModel
import com.darekbx.shoppinglist.navigation.AppNavHost
import org.koin.androidx.compose.koinViewModel

/**
 * Light phone launcher
 *
 * - min 60% of code coverage!
 *
 * - apps list (lowercase), is scrolled by pages, with page indiction dots
 * - on the list will be only selected apps (from settings)
 * - in settings: selecting apps, reordering
 * - dotpad: "dot pad (12)"
 * - "geo tracker (1253km)" or with some details 9sp below app nape
 * - ideas:
 *   - swipe left to show list of "tools": fuel, books, weight ...
 * - statistics:
 *   - app click count
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LauncherContent()
        }
    }

    @Composable
    private fun LauncherContent(
        userApplicationsViewModel: UserApplicationsViewModel = koinViewModel()
    ) {
        DisposableEffect(Unit) {
            registerReceiver(userApplicationsViewModel.applicationsReceiver, IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addDataScheme("package")
            })
            onDispose {
                unregisterReceiver(userApplicationsViewModel.applicationsReceiver)
            }
        }
        LightLauncherTheme {
            val navController = rememberNavController()
            Scaffold(
                topBar = { },
                content = { innerPadding ->
                    AppNavHost(
                        modifier = Modifier.padding(innerPadding),
                        controller = navController
                    )
                },
                bottomBar = { /*BottomNavigation(navController)*/ }
            )
            SetNavigationBarColor()
        }
    }

    @Composable
    fun SetNavigationBarColor() {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                MaterialTheme.colorScheme.background.toArgb(),
                MaterialTheme.colorScheme.background.toArgb(),
            ),
            navigationBarStyle = SystemBarStyle.auto(
                MaterialTheme.colorScheme.background.toArgb(),
                MaterialTheme.colorScheme.background.toArgb(),
                detectDarkMode = { true }
            )
        )
    }
}

