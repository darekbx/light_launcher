package com.darekbx.lightlauncher.system

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo

class PackageManagerWrapper(private val packageManager: PackageManager) : BasePackageManager {

    override fun queryIntentActivities(intent: Intent, flags: Int) =
        packageManager.queryIntentActivities(intent, flags)

    override fun getApplicationLabel(resolveInfo: ResolveInfo) =
        resolveInfo.loadLabel(packageManager).toString()

    override fun getApplicationIcon(resolveInfo: ResolveInfo) =
        resolveInfo.loadIcon(packageManager)
}
