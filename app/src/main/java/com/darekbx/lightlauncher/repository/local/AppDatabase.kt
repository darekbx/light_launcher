package com.darekbx.lightlauncher.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.darekbx.lightlauncher.repository.local.dao.ApplicationCacheDao
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dao.ClickCountDao
import com.darekbx.lightlauncher.repository.local.dto.ApplicationCacheDto
import com.darekbx.lightlauncher.repository.local.dto.ApplicationDto
import com.darekbx.lightlauncher.repository.local.dto.ClickCountDto

@Database(
    entities = [
        ApplicationDto::class,
        ClickCountDto::class,
        ApplicationCacheDto::class
   ],
    exportSchema = true,
    version = 5
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun applicationDao(): ApplicationDao

    abstract fun clickCountDao(): ClickCountDao

    abstract fun applicationCacheDao(): ApplicationCacheDao

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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `application` ADD COLUMN `x` INTEGER NOT NULL DEFAULT -1")
                db.execSQL("ALTER TABLE `application` ADD COLUMN `y` INTEGER NOT NULL DEFAULT -1")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `application_cache` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT, 
                        `activity_name` TEXT NOT NULL, 
                        `package_name` TEXT NOT NULL, 
                        `label` TEXT NOT NULL, 
                        `order` INTEGER NOT NULL, 
                        `is_from_home` INTEGER NOT NULL, 
                        `is_my` INTEGER NOT NULL, 
                        `page` INTEGER NOT NULL
                       )""".trimMargin()
                )
            }
        }


        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS `notification`")
            }
        }
    }
}
