package com.darekbx.lightlauncher.system

import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable

interface BasePackageManager {

    fun queryIntentActivities(intent: Intent, flags: Int): List<ResolveInfo>

    fun getApplicationLabel(resolveInfo: ResolveInfo): String

    fun getApplicationIcon(resolveInfo: ResolveInfo): Drawable
}