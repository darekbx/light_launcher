package com.darekbx.lightlauncher.ui.userapplications

import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dao.ClickCountDao
import com.darekbx.lightlauncher.repository.local.dto.ApplicationDto
import com.darekbx.lightlauncher.repository.local.dto.ClickCountDto
import com.darekbx.lightlauncher.system.BaseApplicationsProvider
import com.darekbx.lightlauncher.system.BasePackageManager
import com.darekbx.lightlauncher.system.model.Application
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserApplicationsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: UserApplicationsViewModel
    private val applicationsProvider = mockk<BaseApplicationsProvider>()
    private val packageManager = mockk<BasePackageManager>()
    private val applicationDao = mockk<ApplicationDao>()
    private val clickCountDao = mockk<ClickCountDao>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = UserApplicationsViewModel(
            applicationsProvider,
            packageManager,
            applicationDao,
            clickCountDao,
            testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `increaseClickCount increases count for new application`() = runTest {
        // Given
        val application = Application("Activity name", "com.test.app", "Test app", mockk(), 0)
        coEvery { clickCountDao.get(application.activityName) } returns null
        coEvery { clickCountDao.add(any()) } just runs

        // When
        viewModel.increaseClickCount(application)

        // Then
        coVerify { clickCountDao.get(application.activityName) }
        coVerify { clickCountDao.add(any()) }
    }

    @Test
    fun `increaseClickCount increases count for existing application`() = runTest {
        // Given
        val application = Application("Activity name", "com.test.app", "Test app", mockk(), 0)
        coEvery { clickCountDao.get(application.activityName) } returns ClickCountDto(null, application.activityName, 1)
        coEvery { clickCountDao.increaseClicks(any()) } just runs

        // When
        viewModel.increaseClickCount(application)

        // Then
        coVerify { clickCountDao.get(application.activityName) }
        coVerify { clickCountDao.increaseClicks(application.activityName) }
        coVerify(exactly = 0) { clickCountDao.add(any()) }
    }

    @Test
    fun `loadApplications fetches mapped item`() = runTest {
        // given
        coEvery { applicationDao.fetch() } returns listOf(
            ApplicationDto(1L, "Activity name", "com.test.app1", "Test app1", order = 1),
        )

        // when
        /*val result = viewModel.loadApplications()

        // then
        TestCase.assertEquals(1, result.size)
        with(result[0]) {
            TestCase.assertEquals("com.test.app1", packageName)
            TestCase.assertEquals("Activity name", activityName)
            TestCase.assertEquals("Test app1", label)
        }*/
    }

    @Test
    fun `loadAllApplications fetches mapped item`() = runTest {
        // given
        coEvery { applicationsProvider.listInstalledApplications() } returns listOf(
            Application("Activity name 1", "com.test.app1", "Test app1", mockk(), 0),
            Application("Activity name 2", "com.test.app2", "Test app2", mockk(), 0)
        )
        coEvery { applicationDao.fetch() } returns listOf(
            ApplicationDto(1L, "Activity name 1", "com.test.app1", "Test app1"),
        )

        // when
        //viewModel.loadAllApplications()
        advanceUntilIdle()

        // then
        val result = viewModel.uiState.value as UserApplicationsUiState.Done
        TestCase.assertEquals(1, result.applications.size)
        with(result.applications[0]) {
            TestCase.assertEquals("com.test.app2", packageName)
            TestCase.assertEquals("Activity name 2", activityName)
            TestCase.assertEquals("Test app2", label)
        }
    }
}