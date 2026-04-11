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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class LoadMode {
    ALL, ONLY_HOME, ALL_EXCEPT_HOME
}

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
        const val IS_HOME = "com.darekbx.home"
        const val IS_MY = "com.darekbx"
    }

    private var allApplicationsCache: MutableMap<LoadMode, List<Application>> = mutableMapOf()

    val applicationsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            checkIfAppShouldBeRemoved(intent)
            //restoreRemovedApplications(intent)
            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED -> {
                    loadAllApplications(loadMode = LoadMode.ALL)
                }
            }
        }
    }

    private fun checkIfAppShouldBeRemoved(intent: Intent) {
        if (intent.action == Intent.ACTION_PACKAGE_REMOVED) {
            intent.data?.schemeSpecificPart?.let { packageName ->
                if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                    // Just updating the app, skip app removing
                    return
                }
                viewModelScope.launch {
                    withContext(ioDispatcher) {
                        // Add removed apps to cache, they will be restored then this is an update action
                        //removedApplications.addAll(
                        //    applicationDao.fetch().filter { it.packageName == packageName })
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

    fun loadAllApplications(loadMode: LoadMode, forceReload: Boolean = false) {
        viewModelScope.launch {
            if (forceReload) {
                allApplicationsCache.clear()
            }

            val cached = allApplicationsCache.get(loadMode)
            if (cached != null) {
                _uiState.value = UserApplicationsUiState.Done(cached)
            } else {
                _uiState.value = UserApplicationsUiState.Idle
                withContext(ioDispatcher) {
                    val savedApps = applicationDao.fetch()
                    val installedApps = applicationsProvider.listInstalledApplications()
                    val applications = installedApps
                        .filter { installedApp ->
                            savedApps.none { savedApp ->
                                savedApp.activityName == installedApp.activityName
                            }
                        }
                        .map { app ->
                            with(app) {
                                isFromHome = packageName.contains(IS_HOME)
                                isMy = packageName.contains(IS_MY)
                            }
                            app
                        }
                        .filter { app ->
                            when (loadMode) {
                                LoadMode.ALL -> true
                                LoadMode.ONLY_HOME -> (app.isFromHome || app.isMy)
                                LoadMode.ALL_EXCEPT_HOME -> (!app.isFromHome && !app.isMy)
                            }
                        }
                        .sortedBy { it.label.lowercase() }

                    allApplicationsCache[loadMode] = applications
                    _uiState.value = UserApplicationsUiState.Done(applications)
                }
            }
        }
    }

    fun loadApplications() {
        viewModelScope.launch {
            _uiState.value = UserApplicationsUiState.Idle
            withContext(ioDispatcher) {
                val savedApps = applicationDao.fetch()
                val applications = savedApps.map { dto ->
                    Application(
                        activityName = dto.activityName,
                        packageName = dto.packageName,
                        label = dto.label,
                        icon = null,
                        order = dto.order,
                        x = dto.x,
                        y = dto.y,
                    ).apply {
                        isFromHome = dto.packageName.contains(IS_HOME)
                        isMy = dto.packageName.contains(IS_MY)
                    }
                }
                _uiState.value = UserApplicationsUiState.Done(applications)
            }
        }
    }
}
