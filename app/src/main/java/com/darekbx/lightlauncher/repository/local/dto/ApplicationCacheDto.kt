package com.darekbx.lightlauncher.repository.local.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "application_cache")
class ApplicationCacheDto(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "activity_name") var activityName: String,
    @ColumnInfo(name = "package_name") var packageName: String,
    @ColumnInfo(name = "label") var label: String,
    @ColumnInfo(name = "order") var order: Int,
    @ColumnInfo(name = "is_from_home") var isFromHome: Boolean,
    @ColumnInfo(name = "is_my") var isMy: Boolean,
    @ColumnInfo(name = "page") var page: Int, // Can be 0, 1, 2
)
