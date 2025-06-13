package com.darekbx.lightlauncher.system

import android.content.Context
import android.content.Intent
import android.content.IntentFilter

object BatteryCycles {

    fun getBatteryCycles(context: Context): Int {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, filter)
        return batteryStatus?.getIntExtra("android.os.extra.CYCLE_COUNT", -1) ?: -1
    }
}