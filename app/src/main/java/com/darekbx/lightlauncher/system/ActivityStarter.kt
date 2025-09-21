package com.darekbx.lightlauncher.system

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.darekbx.lightlauncher.system.model.Application

class ActivityStarter(private val context: Context) {

    fun startApplication(application: Application) {

        val launchIntent: Intent = if (application.isFromHome) {
            componentIntent(application)
        } else {
            context.packageManager.getLaunchIntentForPackage(application.packageName)
                ?: componentIntent(application)
        }

        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)
    }

    private fun componentIntent(application: Application): Intent = Intent().apply {
        setComponent(
            ComponentName(
                application.packageName,
                application.activityName
            )
        )
    }

    fun openSettings(application: Application) {
        val intent = Intent().apply {
            action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = android.net.Uri.parse("package:${application.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
