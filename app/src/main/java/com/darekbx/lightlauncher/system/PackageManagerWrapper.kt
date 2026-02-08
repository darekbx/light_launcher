package com.darekbx.lightlauncher.system

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PackageInfoFlags
import android.content.pm.ResolveInfo

class PackageManagerWrapper(private val packageManager: PackageManager) : BasePackageManager {

    override fun queryPackageActivities(packageName: String, flags: Int) =
        packageManager.getPackageInfo(packageName, PackageInfoFlags.of(0)).activities?.toList() ?: emptyList()

    override fun queryIntentActivities(intent: Intent, flags: Int) =
        packageManager.queryIntentActivities(intent, flags)

    override fun getApplicationLabel(resolveInfo: ResolveInfo) =
        resolveInfo.loadLabel(packageManager).toString()

    override fun getApplicationIcon(resolveInfo: ResolveInfo) =
        resolveInfo.loadIcon(packageManager)

    override fun getApplicationIcon(packageName: String) =
        packageManager.getApplicationIcon(packageName)
}
