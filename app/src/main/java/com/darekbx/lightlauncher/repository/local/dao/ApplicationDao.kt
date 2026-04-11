package com.darekbx.lightlauncher.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.lightlauncher.repository.local.dto.ApplicationDto

@Dao
interface ApplicationDao {

    @Query("SELECT * FROM application ORDER BY `order` ASC")
    suspend fun fetch(): List<ApplicationDto>

    @Insert
    suspend fun add(applicationDto: ApplicationDto)

    @Query("DELETE FROM application WHERE package_name = :packageName")
    suspend fun delete(packageName: String)

    @Query("UPDATE application SET `order` = :order WHERE activity_name = :activityName")
    suspend fun setOrder(activityName: String, order: Int)

    @Query("UPDATE application SET `x` = :x, `y` = :y WHERE activity_name = :activityName")
    suspend fun setLocation(activityName: String, x: Int, y: Int)
}
