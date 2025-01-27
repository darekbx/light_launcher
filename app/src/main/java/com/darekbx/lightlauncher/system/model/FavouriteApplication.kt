package com.darekbx.lightlauncher.system.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FavouriteApplication(
    val activityName: String,
    val packageName: String,
    val label: String,
    val isFavourite: Boolean,
    val isFromHome: Boolean
) : Parcelable