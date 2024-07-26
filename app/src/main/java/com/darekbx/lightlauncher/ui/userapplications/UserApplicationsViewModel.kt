package com.darekbx.lightlauncher.ui.userapplications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dao.ClickCountDao
import com.darekbx.lightlauncher.repository.local.dto.ClickCountDto
import com.darekbx.lightlauncher.system.BaseApplicationsProvider
import com.darekbx.lightlauncher.system.BasePackageManager
import com.darekbx.lightlauncher.system.model.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class UserApplicationsUiState {
    class Done(val applications: List<Application>) : UserApplicationsUiState()
    data object Idle : UserApplicationsUiState()
}

class UserApplicationsViewModel(
    private val applicationsProvider: BaseApplicationsProvider,
    private val packageManager: BasePackageManager,
    private val applicationDao: ApplicationDao,
    private val clickCountDao: ClickCountDao,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    companion object {
        private val IS_HOME = "com.darekbx.home"
    }

    val applicationsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            checkIfAppShouldBeRemoved(intent)
            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED -> {
                    loadAllApplications()
                }
            }
        }
    }

    private fun checkIfAppShouldBeRemoved(intent: Intent) {
        if (intent.action == Intent.ACTION_PACKAGE_REMOVED) {
            intent.data?.schemeSpecificPart?.let { packageName ->
                viewModelScope.launch {
                    withContext(ioDispatcher) {
                        applicationDao.delete(packageName)
                    }
                }
            }
        }
    }

    private val _uiState = mutableStateOf<UserApplicationsUiState>(UserApplicationsUiState.Idle)
    val uiState: State<UserApplicationsUiState>
        get() = _uiState

    fun increaseClickCount(item: Application) {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                with(clickCountDao) {
                    if (get(item.activityName) == null) {
                        add(ClickCountDto(null, item.activityName, 1))
                    } else {
                        increaseClicks(item.activityName)
                    }
                }
            }
        }
    }

    fun loadAllApplications() {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                delay(250)
                val installedApps = applicationsProvider.listInstalledApplications()
                val savedApps = applicationDao.fetch()
                val applications = installedApps
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
                            icon = app.icon,
                            order = -1,
                            isFromHome = app.packageName.contains(IS_HOME)
                        )
                    }
                    .sortedBy { it.label }
                _uiState.value = UserApplicationsUiState.Done(applications)
            }
        }
    }

    fun loadApplications() = flow {
        val savedApps = applicationDao.fetch()
        val applications = savedApps.map { app ->
            Application(
                activityName = app.activityName,
                packageName = app.packageName,
                label = app.label,
                icon = packageManager.getApplicationIcon(app.packageName),
                order = app.order,
                isFromHome = app.packageName.contains(IS_HOME)
            )
        }
        emit(applications)
    }
}
