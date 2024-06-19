package com.darekbx.lightlauncher.system.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderedApplication(
    val packageName: String,
    val label: String,
    val order: Int?,
    val localId: Long?
) : Parcelable