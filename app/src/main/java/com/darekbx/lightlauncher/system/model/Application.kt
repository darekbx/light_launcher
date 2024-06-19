package com.darekbx.lightlauncher.system.model

import android.graphics.drawable.Drawable

data class Application(
    val packageName: String,
    val label: String,
    val icon: Drawable
)