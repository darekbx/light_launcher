package com.darekbx.lightlauncher.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.darekbx.lightlauncher.repository.local.dto.ApplicationDto

/**
 * Data Access Object for the `application` table.
 * This is used to access the data in the `application` table.
 */
@Dao
interface ApplicationDao {

    /**
     * Fetches all applications from the `application` table.
     *
     * @return List of [ApplicationDto] objects.
     */
    @Query("SELECT * FROM application ORDER BY `order` ASC")
    suspend fun fetch(): List<ApplicationDto>

    /**
     * Adds a new application to the `application` table.
     *
     * @param applicationDto The [ApplicationDto] object to be added.
     */
    @Insert
    suspend fun add(applicationDto: ApplicationDto)

    /**
     * Deletes an application from the `application` table based on the package name.
     *
     * @param packageName The package name of the application to be deleted.
     */
    @Query("DELETE FROM application WHERE package_name = :packageName")
    suspend fun delete(packageName: String)

    /**
     * Updates the order of an application in the `application` table.
     *
     * @param packageName The package name of the application to be updated.
     * @param order The new order of the application.
     */
    @Query("UPDATE application SET `order` = :order WHERE activity_name = :activityName")
    suspend fun setOrder(activityName: String, order: Int)


    @Query("UPDATE application SET `x` = :x, `y` = :y WHERE activity_name = :activityName")
    suspend fun setLocation(activityName: String, x: Int, y: Int)
}
