package com.darekbx.lightlauncher.repository.local.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "click_count")
class ClickCountDto(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "package_name") var packageName: String = "",
    @ColumnInfo(name = "count") var count: Int = 0
)