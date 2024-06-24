package com.darekbx.lightlauncher.system

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable

interface BasePackageManager {

    fun queryPackageActivities(packageName: String, flags: Int): List<ActivityInfo>

    fun queryIntentActivities(intent: Intent, flags: Int): List<ResolveInfo>

    fun getApplicationLabel(resolveInfo: ResolveInfo): String

    fun getApplicationIcon(resolveInfo: ResolveInfo): Drawable
}