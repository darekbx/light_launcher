package com.darekbx.lightlauncher.system.model

data class Application(
    val activityName: String,
    val packageName: String,
    val label: String,
    val order: Int,
    val isFromHome: Boolean
)