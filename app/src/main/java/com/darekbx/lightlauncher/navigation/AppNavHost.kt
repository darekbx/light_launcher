package com.darekbx.shoppinglist.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darekbx.lightlauncher.ui.main.MainScreen
import com.darekbx.lightlauncher.ui.settings.SettingsScreen
import com.darekbx.lightlauncher.ui.settings.favourites.FavouriteApplicationsScreen
import com.darekbx.lightlauncher.ui.settings.order.ApplicationsOrderScreen
import com.darekbx.lightlauncher.ui.statistics.StatisticsScreen

@Composable
fun AppNavHost(
    controller: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = controller,
        startDestination = UserApplicationsDestination.route,
        modifier = modifier
    ) {
        composable(route = UserApplicationsDestination.route) {
            MainScreen(onOpenSettings = { controller.navigate(SettingsDestination.route) })
        }

        composable(route = SettingsDestination.route) {
            SettingsScreen(
                openFavouriteApplications = { controller.navigate(FavouriteApplicationsDestination.route) },
                openApplicationsOrder = { controller.navigate(ApplicationsOrderDestination.route) },
                openStatistics = { controller.navigate(StatisticsDestination.route) },
            )
        }

        composable(route = FavouriteApplicationsDestination.route) {
            FavouriteApplicationsScreen()
        }

        composable(route = ApplicationsOrderDestination.route) {
            ApplicationsOrderScreen()
        }

        composable(route = StatisticsDestination.route) {
            StatisticsScreen()
        }
    }
}
