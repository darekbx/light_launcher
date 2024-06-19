package com.darekbx.lightlauncher.ui.settings

import androidx.lifecycle.ViewModel
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.system.BaseApplicationsProvider
import kotlinx.coroutines.flow.flow

class SettingsViewModel(
    private val applicationsProvider: BaseApplicationsProvider,
    private val applicationDao: ApplicationDao
) : ViewModel() {

    fun loadApplications() = flow {
        emit(applicationsProvider.listInstalledApplications())
    }
}
