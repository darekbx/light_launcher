package com.darekbx.lightlauncher.system.model

import android.graphics.drawable.Drawable

data class Application(
    val activityName: String,
    val packageName: String,
    val label: String,
    val icon: Drawable?,
    val order: Int,
    val isFromHome: Boolean
) {
    var fontWeight: Int = 400 // DEFAULT FONT WEIGHT
    var scale: Float = 1.0F
    var fontSize: Int = 14
}