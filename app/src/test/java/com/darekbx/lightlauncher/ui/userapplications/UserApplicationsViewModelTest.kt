package com.darekbx.lightlauncher.ui.userapplications

import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dao.ClickCountDao
import com.darekbx.lightlauncher.repository.local.dto.ApplicationDto
import com.darekbx.lightlauncher.system.BaseApplicationsProvider
import com.darekbx.lightlauncher.system.model.Application
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
    private val applicationDao = mockk<ApplicationDao>()
    private val clickCountDao = mockk<ClickCountDao>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = UserApplicationsViewModel(
            applicationsProvider,
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
    fun `loadApplications fetches mapped item`() = runTest {
        // given
        coEvery { applicationDao.fetch() } returns listOf(
            ApplicationDto(1L, "com.test.app1", "Test app1", order = 1),
        )

        // when
        val result = viewModel.loadApplications().last()

        // then
        TestCase.assertEquals(1, result.size)
        with(result[0]) {
            TestCase.assertEquals("com.test.app1", packageName)
            TestCase.assertEquals("Test app1", label)
        }
    }

    @Test
    fun `loadAllApplications fetches mapped item`() = runTest {
        // given
        coEvery { applicationsProvider.listInstalledApplications() } returns listOf(
            Application("com.test.app1", "Test app1"),
            Application("com.test.app2", "Test app2")
        )
        coEvery { applicationDao.fetch() } returns listOf(
            ApplicationDto(1L, "com.test.app1", "Test app1"),
        )

        // when
        val result = viewModel.loadAllApplications().last()

        // then
        TestCase.assertEquals(1, result.size)
        with(result[0]) {
            TestCase.assertEquals("com.test.app2", packageName)
            TestCase.assertEquals("Test app2", label)
        }
    }
}