package com.darekbx.lightlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.darekbx.lightlauncher.ui.theme.LightLauncherTheme
import com.darekbx.shoppinglist.navigation.AppNavHost

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
        enableEdgeToEdge()
        setContent {
            LightLauncherTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = { },
                    content = { innerPadding ->
                        AppNavHost(
                            modifier = Modifier.padding(innerPadding),
                            navController = navController
                        )
                    },
                    bottomBar = { /*BottomNavigation(navController)*/ }
                )
            }
        }
    }
}
