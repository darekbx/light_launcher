package com.darekbx.lightlauncher.ui.settings.order

import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dto.ApplicationDto
import io.mockk.coEvery
import io.mockk.coVerify
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
class OrderViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: OrderViewModel
    private val applicationDao = mockk<ApplicationDao>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OrderViewModel(
            applicationDao,
            testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should set application order`() = runTest {
        // given
        coEvery { applicationDao.setOrder("com.test.app1", 10) } answers { }

        // when
        viewModel.setOrder("com.test.app1", 10)

        // then
        coVerify { applicationDao.setOrder("com.test.app1", 10) }
    }

    @Test
    fun `loadApplicationsOrder fetches mapped item`() = runTest {
        // given
        coEvery { applicationDao.fetch() } returns listOf(
            ApplicationDto(1L, "com.test.app1", "Test app1", order = 1),
        )

        // when
        viewModel.loadApplicationsOrder()

        // then
        TestCase.assertTrue(viewModel.uiState.value is OrderedUiState.Done)
        val result = (viewModel.uiState.value as OrderedUiState.Done).applications
        TestCase.assertEquals(1, result.size)
        with(result[0]) {
            TestCase.assertEquals("com.test.app1", packageName)
            TestCase.assertEquals("Test app1", label)
            TestCase.assertEquals(1, order)
        }
    }
}
