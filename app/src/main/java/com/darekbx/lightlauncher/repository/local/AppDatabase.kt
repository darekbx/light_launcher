package com.darekbx.lightlauncher.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dao.ClickCountDao
import com.darekbx.lightlauncher.repository.local.dao.NotificationDao
import com.darekbx.lightlauncher.repository.local.dto.ApplicationDto
import com.darekbx.lightlauncher.repository.local.dto.ClickCountDto
import com.darekbx.lightlauncher.repository.local.dto.NotificationDto

@Database(
    entities = [ApplicationDto::class, ClickCountDto::class, NotificationDto::class],
    exportSchema = true,
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun applicationDao(): ApplicationDao

    abstract fun clickCountDao(): ClickCountDao

    abstract fun notificationDao(): NotificationDao

    companion object {
        const val DB_NAME = "light_launcher_database"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE `notification`(
                        `id` INTEGER NULL, 
                        `package_name` TEXT NOT NULL, 
                         PRIMARY KEY(`id`)
                       )"""
                )
            }
        }
    }
}
