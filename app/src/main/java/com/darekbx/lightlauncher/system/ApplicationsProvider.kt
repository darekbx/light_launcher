package com.darekbx.lightlauncher.system

import android.content.Intent
import android.content.pm.ResolveInfo
import com.darekbx.lightlauncher.system.model.PackageManagerApplication

class ApplicationsProvider(
    private val packageManager: BasePackageManager
) : BaseApplicationsProvider {

    override fun listPackageManagerApps(): List<PackageManagerApplication> {
        val intent = launcherIntent()
        return packageManager
            .queryIntentActivities(intent, flags = 0)
            .map {
                PackageManagerApplication(
                    getActivityName(it),
                    getPackageName(it),
                    loadAppLabel(it)
                )
            }
    }

    fun getPackageName(resolveInfo: ResolveInfo): String =
        resolveInfo.activityInfo.applicationInfo.packageName

    fun getActivityName(resolveInfo: ResolveInfo): String =
        resolveInfo.activityInfo.name

    fun loadAppLabel(resolveInfo: ResolveInfo) =
        packageManager.getApplicationLabel(resolveInfo)

    fun launcherIntent() =
        Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
}
