package com.darekbx.shoppinglist.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darekbx.lightlauncher.ui.settings.SettingsScreen
import com.darekbx.lightlauncher.ui.settings.favourites.FavouriteApplicationsScreen
import com.darekbx.lightlauncher.ui.settings.order.ApplicationsOrderScreen
import com.darekbx.lightlauncher.ui.settings.selforganized.SelfOrganizedCloud
import com.darekbx.lightlauncher.ui.statistics.StatisticsScreen
import com.darekbx.lightlauncher.ui.userapplications.UserApplicationsScreen

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
            UserApplicationsScreen(
                onSettingsClick = { controller.navigate(SettingsDestination.route) },
                onStatisticsClick = { controller.navigate(StatisticsDestination.route) }
            )
        }

        composable(route = SettingsDestination.route) {
            SettingsScreen(
                openFavouriteApplications = { controller.navigate(FavouriteApplicationsDestination.route) },
                openApplicationsOrder = { controller.navigate(ApplicationsOrderDestination.route) },
                openSelfOrganizedCloudOrder = { controller.navigate(ApplicationsSelfOrganizedDestination.route) },
            )
        }

        composable(route = FavouriteApplicationsDestination.route) {
            FavouriteApplicationsScreen()
        }

        composable(route = ApplicationsOrderDestination.route) {
            ApplicationsOrderScreen()
        }

        composable(route = ApplicationsSelfOrganizedDestination.route) {
            SelfOrganizedCloud()
        }

        composable(route = StatisticsDestination.route) {
            StatisticsScreen()
        }
    }
}
