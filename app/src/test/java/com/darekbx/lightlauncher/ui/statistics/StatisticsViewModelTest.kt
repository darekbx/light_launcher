package com.darekbx.lightlauncher.ui.statistics

import com.darekbx.lightlauncher.system.BaseApplicationsProvider
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.repository.local.dao.ClickCountDao
import com.darekbx.lightlauncher.repository.local.dto.ClickCountDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StatisticsViewModelTest {

    private lateinit var viewModel: StatisticsViewModel
    private val applicationsProvider: BaseApplicationsProvider = mockk()
    private val clickCountDao: ClickCountDao = mockk()

    @Before
    fun setUp() {
        viewModel = StatisticsViewModel(applicationsProvider, clickCountDao)
    }

    @Test
    fun `getClickCount returns mapped ClickCount`() = runTest {
        // Given
        val applications = listOf(
            Application("Activity name 1", "com.test.app1", "Test app1", 0, false),
            Application("Activity name 2", "com.test.app2", "Test app2", 0, false)
        )
        val clickCounts = listOf(
            ClickCountDto(null, "Activity name 1", 5),
            ClickCountDto(null, "Activity name 2", 3)
        )
        coEvery { applicationsProvider.listInstalledApplications() } returns applications
        coEvery { clickCountDao.fetch() } returns clickCounts

        // When
        val result = viewModel.getClickCount().first()

        // Then
        assertEquals(2, result.size)
        with(result[0]) {
            assertEquals("Test app1", label)
            assertEquals(5, count)
        }
        with(result[1]) {
            assertEquals("Test app2", label)
            assertEquals(3, count)
        }
    }
}
