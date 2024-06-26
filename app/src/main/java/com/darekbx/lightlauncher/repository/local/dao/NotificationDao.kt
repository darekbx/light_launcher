package com.darekbx.lightlauncher.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.lightlauncher.repository.local.dto.NotificationDto
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert
    suspend fun add(notificationDto: NotificationDto)

    @Query("DELETE FROM notification WHERE package_name = :packageName")
    suspend fun delete(packageName: String)

    @Query("SELECT * FROM notification")
    fun fetch(): Flow<List<NotificationDto>>
}
