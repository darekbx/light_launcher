package com.darekbx.lightlauncher.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dao.ClickCountDao
import com.darekbx.lightlauncher.repository.local.dto.ApplicationDto
import com.darekbx.lightlauncher.repository.local.dto.ClickCountDto

@Database(entities = [ApplicationDto::class, ClickCountDto::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun applicationDao(): ApplicationDao

    abstract fun clickCountDao(): ClickCountDao

    companion object {
        const val DB_NAME = "light_launcher_database"
    }
}
