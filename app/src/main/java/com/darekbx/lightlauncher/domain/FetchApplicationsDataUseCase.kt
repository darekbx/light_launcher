package com.darekbx.lightlauncher.domain

import com.darekbx.lightlauncher.repository.local.dao.ApplicationCacheDao
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dto.ApplicationCacheDto
import com.darekbx.lightlauncher.system.BaseApplicationsProvider
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.system.model.Application.Companion.toApplicationCache
import com.darekbx.lightlauncher.system.model.Application.Companion.toApplicationCacheDto

class FetchApplicationsDataUseCase(
    private val applicationsProvider: BaseApplicationsProvider,
    private val applicationCacheDao: ApplicationCacheDao,
    private val applicationDao: ApplicationDao
) {

    suspend operator fun invoke(forceReload: Boolean): List<List<Application>> {
        // 1. Check cache state
        val cachedData = applicationCacheDao.fetch()

        // 2. Fetch from cache if apps cached
        if (!forceReload && cachedData.isNotEmpty()) {
            return cachedData.transformCache()
        }

        // 3a. Load favourite applications
        val favouriteApplications = loadFavouriteApplications()

        // 3b. Load rest of home applications
        val otherHomeApplications = loadOtherHomeApplications(allExceptHome = false)

        // 3c. Load other apps
        val otherApplications = loadOtherHomeApplications(allExceptHome = true)

        // 4a. Clean cache
        applicationCacheDao.deleteAll()

        // 4b. Collect pages to cache
        val pageOne = favouriteApplications.map { it.toApplicationCacheDto(1) }
        val pageTwo = otherHomeApplications.map { it.toApplicationCacheDto(2) }
        val pageThree = otherApplications.map { it.toApplicationCacheDto(3) }


        val bariApps = listOf(
            "com.wizzair.WizzAirApp",
            "com.lynxspa.prontotreno",
            "com.whatsapp",
            "net.pluservice.muvt",
            "com.tranzmate",
        )
        val pageFour = otherApplications.map { it.toApplicationCacheDto(4) }


        val allPages = pageOne + pageTwo + pageThree + pageFour

        // 4c. Save cache
        applicationCacheDao.addAll(allPages)

        return listOf(
            favouriteApplications,
            otherHomeApplications,
            otherApplications,

            otherApplications
                .filter { bariApps.contains(it.packageName)  }
                .map { it.also { it.isBari = true } }
        )
    }

    private fun List<ApplicationCacheDto>.transformCache(): List<List<Application>> =
        groupBy { it.page }
        .mapValues { (_, list) -> list.map { dto -> dto.toApplicationCache() } }
        .map { (_, list) -> list }

    private suspend fun loadFavouriteApplications() =
        applicationDao.fetch().map { dto ->
            Application(
                activityName = dto.activityName,
                packageName = dto.packageName,
                label = dto.label,
                order = dto.order,
                isFromHome = dto.packageName.contains(IS_HOME),
                isMy = dto.packageName.contains(IS_MY)
            )
        }

    private suspend fun loadOtherHomeApplications(allExceptHome: Boolean = false): List<Application> {
        val savedApps = applicationDao.fetch()
        val installedApps = applicationsProvider.listPackageManagerApps()
        return installedApps
            .filter { installedApp ->
                savedApps.none { savedApp ->
                    savedApp.activityName == installedApp.activityName
                }
            }
            .map { app ->
                Application(
                    activityName = app.activityName,
                    packageName = app.packageName,
                    label = app.label,
                    order = -1,
                    isFromHome = app.packageName.contains(IS_HOME),
                    isMy = app.packageName.contains(IS_MY)
                )
            }
            .filter { app -> app.isHomeOrMy xor allExceptHome }
            .sortedBy { it.label.lowercase() }
    }


    companion object {
        const val IS_HOME = "com.darekbx.home"
        const val IS_MY = "com.darekbx"
    }
}
