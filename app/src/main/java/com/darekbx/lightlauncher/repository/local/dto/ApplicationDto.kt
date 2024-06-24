package com.darekbx.lightlauncher.repository.local.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "application")
class ApplicationDto(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "activity_name") var activityName: String = "",
    @ColumnInfo(name = "package_name") var packageName: String = "",
    @ColumnInfo(name = "label") var label: String = "",
    @ColumnInfo(name = "order") var order: Int = 0
)