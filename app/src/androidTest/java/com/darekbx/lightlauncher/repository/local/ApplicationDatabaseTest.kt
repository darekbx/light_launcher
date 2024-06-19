package com.darekbx.lightlauncher.repository.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dto.ApplicationDto
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ApplicationDatabaseTest {

    private lateinit var applicationDao: ApplicationDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        applicationDao = db.applicationDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun `should add application and increase click_count twice`() = runBlocking {
        // given
        applicationDao.add(ApplicationDto(1L, "package.name"))

        // when
        applicationDao.increaseClicks("package.name")
        applicationDao.increaseClicks("package.name")

        // then
        val applications = applicationDao.fetch()
        assertEquals(1, applications.size)
        with(applications[0]) {
            assertEquals("package.name", packageName)
            assertEquals(2, clickCount)
        }
    }

    @Test
    @Throws(Exception::class)
    fun `should add application and change order`() = runBlocking {
        // given
        applicationDao.add(ApplicationDto(1L, "package.name"))

        // when
        applicationDao.setOrder("package.name", 12)

        // then
        val applications = applicationDao.fetch()
        assertEquals(1, applications.size)
        with(applications[0]) {
            assertEquals("package.name", packageName)
            assertEquals(12, order)
        }
    }

    @Test
    @Throws(Exception::class)
    fun `should add application and delete`() = runBlocking {
        // given
        applicationDao.add(ApplicationDto(1L, "package.name1"))
        applicationDao.add(ApplicationDto(2L, "package.name2"))
        assertEquals(2, applicationDao.fetch().size)

        // when
        applicationDao.delete("package.name2")

        // then
        val applications = applicationDao.fetch()
        assertEquals(1, applications.size)
        assertEquals("package.name1", applications[0].packageName)
    }
}
