package com.darekbx.shoppinglist.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darekbx.lightlauncher.ui.settings.SettingsScreen
import com.darekbx.lightlauncher.ui.userapplications.UserApplicationsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = UserApplicationsDestination.route,
        modifier = modifier
    ) {
        composable(route = UserApplicationsDestination.route) {
            UserApplicationsScreen {
                navController.navigateSingleTopTo(SettingsDestination.route)
            }
        }

        composable(route = SettingsDestination.route) {
            SettingsScreen(
                openApplications = {
                    navController.navigateSingleTopTo(SettingsApplicationsDestination.route)
                },
                openStatistics = {}
            )
        }
    }
}

fun NavController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
