package com.darekbx.lightlauncher.ui.userapplications

import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dto.ApplicationDto
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val applicationDao = mockk<ApplicationDao>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = UserApplicationsViewModel(
            applicationDao,
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
        viewModel.loadApplications()

        // then
        TestCase.assertTrue(viewModel.uiState.value is UserApplicationsUiState.Done)
        val result = (viewModel.uiState.value as UserApplicationsUiState.Done).applications
        TestCase.assertEquals(1, result.size)
        with(result[0]) {
            TestCase.assertEquals("com.test.app1", packageName)
            TestCase.assertEquals("Test app1", label)
        }
    }
}