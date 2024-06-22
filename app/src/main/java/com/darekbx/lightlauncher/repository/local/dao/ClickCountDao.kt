package com.darekbx.lightlauncher.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.lightlauncher.repository.local.dto.ClickCountDto

@Dao
interface ClickCountDao {

    @Insert
    suspend fun add(clickCountDto: ClickCountDto)

    @Query("SELECT * FROM click_count WHERE package_name = :packageName")
    suspend fun get(packageName: String): ClickCountDto?

    @Query("""
UPDATE click_count
SET count = (SELECT count FROM click_count WHERE package_name = :packageName) + 1
WHERE package_name = :packageName
""")
    suspend fun increaseClicks(packageName: String)
}
