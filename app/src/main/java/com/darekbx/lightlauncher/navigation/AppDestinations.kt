package com.darekbx.shoppinglist.navigation

interface AppDestinations {
    val route: String
}

object SettingsDestination : AppDestinations {
    override val route = "settings"
}

object StatisticsDestination : AppDestinations {
    override val route = "statistics"
}

object FavouriteApplicationsDestination : AppDestinations {
    override val route = "favourite_applications"
}

object ApplicationsOrderDestination : AppDestinations {
    override val route = "applications_order"
}

object UserApplicationsDestination : AppDestinations {
    override val route = "user_applications"
}