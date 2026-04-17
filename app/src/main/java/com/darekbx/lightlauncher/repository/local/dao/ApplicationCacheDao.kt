package com.darekbx.lightlauncher.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.lightlauncher.repository.local.dto.ApplicationCacheDto

@Dao
interface ApplicationCacheDao {

    @Query("SELECT * FROM application_cache")
    suspend fun fetch(): List<ApplicationCacheDto>

    @Insert
    suspend fun addAll(applications: List<ApplicationCacheDto>)

    @Query("DELETE FROM application_cache")
    suspend fun deleteAll()
}
