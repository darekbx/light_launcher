package com.darekbx.lightlauncher.ui.settings

import androidx.lifecycle.ViewModel
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.system.BaseApplicationsProvider
import com.darekbx.lightlauncher.system.model.OrderedApplication

class SettingsViewModel(
    private val applicationsProvider: BaseApplicationsProvider,
    private val applicationDao: ApplicationDao
) : ViewModel() {

    suspend fun loadApplications(): List<OrderedApplication> {
        val installedApps = applicationsProvider.listInstalledApplications()
        val savedApps = applicationDao.fetch()
        return installedApps.map { installedApp ->
            val savedApp = savedApps.find { it.packageName == installedApp.packageName }
            OrderedApplication(
                packageName = installedApp.packageName,
                label = installedApp.label,
                localId = savedApp?.id,
                order = savedApp?.order
            )
        }
    }
}
