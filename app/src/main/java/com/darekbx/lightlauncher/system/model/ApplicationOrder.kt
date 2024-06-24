package com.darekbx.lightlauncher.system.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ApplicationOrder(
    val activityName: String,
    val packageName: String,
    val label: String,
    val order: Int?
) : Parcelable