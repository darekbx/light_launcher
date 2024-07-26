package com.darekbx.lightlauncher

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.darekbx.lightlauncher.ui.theme.LightLauncherTheme
import com.darekbx.lightlauncher.ui.userapplications.UserApplicationsViewModel
import com.darekbx.shoppinglist.navigation.AppNavHost
import com.darekbx.shoppinglist.navigation.UserApplicationsDestination
import org.koin.androidx.compose.koinViewModel

/**
 * Light phone launcher
 *
 * - min 60% of code coverage!
 * - dotpad: "dot pad (12)"
 * - "geo tracker (1253km)" or with some details 9sp below app nape
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

            BackHandler {
                if (navController.currentDestination?.route != UserApplicationsDestination.route) {
                    navController.popBackStack()
                }
            }

            Scaffold(content = { innerPadding ->
                AppNavHost(
                    modifier = Modifier.padding(innerPadding),
                    controller = navController
                )
            })

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
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }
}
