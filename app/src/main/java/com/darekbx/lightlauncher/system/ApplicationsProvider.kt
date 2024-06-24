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
                    it.activityInfo.name,
                    getPackageName(it),
                    packageManager.getApplicationLabel(it),
                    order = -1,
                    isFromHome = false
                )
            }
    }

    fun getPackageName(it: ResolveInfo): String =
        it.activityInfo.applicationInfo.packageName

    fun launcherIntent() =
        Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
}
