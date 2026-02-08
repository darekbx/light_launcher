package com.darekbx.lightlauncher.ui.settings

import com.darekbx.lightlauncher.repository.local.SettingsStore
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: SettingsViewModel
    private val settingsStore: SettingsStore = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SettingsViewModel(settingsStore)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load returns correct values`() = runTest {
        // Given
        coEvery { settingsStore.shouldUsePages } returns flowOf(true)
        coEvery { settingsStore.pageSize } returns flowOf(5)

        // When
        var resultUsePages = false
        var resultPageSize = 0
        /*viewModel.load { _, usePages, _, pageSize ->
            resultUsePages = usePages
            resultPageSize = pageSize
        }*/

        // Then
        assertEquals(true, resultUsePages)
        assertEquals(5, resultPageSize)
    }

    @Test
    fun `setUsePages sets correct value`() = runTest {
        // Given
        coEvery { settingsStore.setUsePages(any()) } just Runs

        // When
        viewModel.setUsePages(true)

        // Then
        coVerify { settingsStore.setUsePages(true) }
    }

    @Test
    fun `setPageSize sets correct value`() = runTest {
        // Given
        coEvery { settingsStore.setPageSize(any()) } just Runs

        // When
        viewModel.setPageSize(5)

        // Then
        coVerify { settingsStore.setPageSize(5) }
    }
}