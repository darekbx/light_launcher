package com.darekbx.lightlauncher.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.lightlauncher.repository.local.dto.ClickCountDto

@Dao
interface ClickCountDao {

    @Insert
    suspend fun add(clickCountDto: ClickCountDto)

    @Query("SELECT * FROM click_count ORDER BY count DESC")
    suspend fun fetch(): List<ClickCountDto>

    @Query("SELECT * FROM click_count WHERE activity_name = :activityName")
    suspend fun get(activityName: String): ClickCountDto?

    @Query("SELECT * FROM click_count ORDER BY count DESC LIMIT 1")
    suspend fun getMaxCount(): ClickCountDto?

    @Query("""
UPDATE click_count
SET count = (SELECT count FROM click_count WHERE activity_name = :activityName) + 1
WHERE activity_name = :activityName
""")
    suspend fun increaseClicks(activityName: String)
}
