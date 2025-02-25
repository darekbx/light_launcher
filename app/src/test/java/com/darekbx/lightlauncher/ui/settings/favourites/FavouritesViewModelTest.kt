package com.darekbx.lightlauncher.ui.settings.favourites

import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dto.ApplicationDto
import com.darekbx.lightlauncher.system.BaseApplicationsProvider
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.system.model.FavouriteApplication
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
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
class FavouritesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: FavouritesViewModel
    private val applicationsProvider = mockk<BaseApplicationsProvider>()
    private val applicationDao = mockk<ApplicationDao>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FavouritesViewModel(
            applicationsProvider,
            applicationDao,
            testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setFavourite adds favourite when isFavourite is true`() = runTest {
        // given
        val packageName = "com.test.app"
        every { applicationsProvider.listInstalledApplications() } returns emptyList()
        coEvery { applicationDao.fetch() } returns emptyList()

        val slot = slot<ApplicationDto>()
        coEvery { applicationDao.add(capture(slot)) } answers { }

        // when
        viewModel.setFavourite(FavouriteApplication("", packageName, "label", true, false), true)

        // then
        assertEquals(packageName, slot.captured.packageName)
    }

    @Test
    fun `setFavourite removes favourite when isFavourite is false`() = runTest {
        // given
        val packageName = "com.test.app"
        every { applicationsProvider.listInstalledApplications() } returns emptyList()
        coEvery { applicationDao.fetch() } returns emptyList()
        coEvery { applicationDao.delete(any()) } returns Unit

        // when
        viewModel.setFavourite(FavouriteApplication("", packageName, "label", true, false), false)

        // then
        coVerify { applicationDao.delete(packageName) }
    }

    @Test
    fun `loadFavouriteApplications fetches mapped item`() = runTest {
        // given
        coEvery { applicationsProvider.listInstalledApplications() } returns listOf(
            Application("activity1", "com.test.app1", "Test app1", mockk(), 0),
            Application("activity2", "com.test.app2", "Test app2", mockk(), 0)
        )
        coEvery { applicationDao.fetch() } returns listOf(
            ApplicationDto(1L, "activity1", "com.test.app1"),
        )

        // when
        viewModel.loadFavouriteApplications()

        // then
        assertTrue(viewModel.uiState.value is FavouritesUiState.Done)
        val result = (viewModel.uiState.value as FavouritesUiState.Done).applications
        assertEquals(2, result.size)
        with(result[0]) {
            assertEquals("com.test.app1", packageName)
            assertEquals("Test app1", label)
            assertTrue(isFavourite)
        }
        with(result[1]) {
            assertEquals("com.test.app2", packageName)
            assertEquals("Test app2", label)
            assertTrue(!isFavourite)
        }
    }
}