package com.darekbx.lightlauncher.ui.userapplications

import androidx.lifecycle.ViewModel
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.system.BaseApplicationsProvider
import com.darekbx.lightlauncher.system.model.Application
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class UserApplicationsViewModel(
    private val applicationsProvider: BaseApplicationsProvider,
    private val applicationDao: ApplicationDao,
) : ViewModel() {

    fun loadAllApplications() = flow {
        delay(100)
        val installedApps = applicationsProvider.listInstalledApplications()
        val savedApps = applicationDao.fetch()
        val applications = installedApps
            .filter { installedApp ->
                savedApps.none { savedApp ->
                    savedApp.packageName == installedApp.packageName
                }
            }
            .map { app ->
                Application(
                    packageName = app.packageName,
                    label = app.label,
                )
            }
        emit(applications)
    }

    fun loadApplications() = flow {
        val savedApps = applicationDao.fetch()
        val applications = savedApps.map { app ->
            Application(
                packageName = app.packageName,
                label = app.label,
            )
        }
        emit(applications)
    }
}
