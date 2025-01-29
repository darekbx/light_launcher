package com.darekbx.lightlauncher.system

import android.content.Intent
import android.content.pm.ResolveInfo
import com.darekbx.lightlauncher.system.model.Application

class ApplicationsProvider(
    private val packageManager: BasePackageManager
) : BaseApplicationsProvider {

    override fun listInstalledApplications(): List<Application> {
        val intent = launcherIntent()
        return packageManager
            .queryIntentActivities(intent, flags = 0)
            .map {
                Application(
                    getActivityName(it),
                    getPackageName(it),
                    loadAppLabel(it),
                    loadAppIcon(it),
                )
            }
    }

    fun getPackageName(resolveInfo: ResolveInfo): String =
        resolveInfo.activityInfo.applicationInfo.packageName

    fun getActivityName(resolveInfo: ResolveInfo): String =
        resolveInfo.activityInfo.name

    fun loadAppIcon(resolveInfo: ResolveInfo) =
        packageManager.getApplicationIcon(resolveInfo)

    fun loadAppLabel(resolveInfo: ResolveInfo) =
        packageManager.getApplicationLabel(resolveInfo)

    fun launcherIntent() =
        Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
}
