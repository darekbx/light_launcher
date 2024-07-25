package com.darekbx.lightlauncher.system

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.darekbx.lightlauncher.system.model.Application

object ActivityStarter {

    fun startApplication(context: Context, application: Application) {
        val intent = Intent().apply {
            setComponent(
                ComponentName(
                    application.packageName,
                    application.activityName
                )
            )
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}