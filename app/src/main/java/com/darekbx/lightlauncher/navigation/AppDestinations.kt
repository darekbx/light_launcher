package com.darekbx.shoppinglist.navigation

interface AppDestinations {
    val route: String
}

object SettingsDestination : AppDestinations {
    override val route = "settings"
}

object FavouriteApplicationsDestination : AppDestinations {
    override val route = "favourite_applications"
}

object UserApplicationsDestination : AppDestinations {
    override val route = "user_applications"
}