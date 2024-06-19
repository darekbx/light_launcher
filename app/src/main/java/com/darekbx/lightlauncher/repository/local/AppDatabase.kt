package com.darekbx.lightlauncher.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dto.ApplicationDto

@Database(entities = [ApplicationDto::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun applicationDao(): ApplicationDao

    companion object {
        const val DB_NAME = "light_launcher_database"
    }
}
