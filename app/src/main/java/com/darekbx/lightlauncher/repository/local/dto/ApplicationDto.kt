package com.darekbx.lightlauncher.repository.local.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "application")
class ApplicationDto(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "package_name") var packageName: String = "",
    @ColumnInfo(name = "label") var label: String = "",
    @ColumnInfo(name = "order") var order: Int = -1,
    @ColumnInfo(name = "click_count") var clickCount: Int = 0
)