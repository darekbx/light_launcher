package com.darekbx.lightlauncher.system.model

import android.graphics.drawable.Drawable

data class Application(
    val activityName: String,
    val packageName: String,
    val label: String,
    val icon: Drawable?,
    val order: Int = -1,
    var x: Int = -1,
    var y: Int = -1
) {
    var fontWeight: Int = 400 // DEFAULT FONT WEIGHT
    var scale: Float = 1.0F
    var fontSize: Int = 14
    var isFromHome: Boolean = false
}