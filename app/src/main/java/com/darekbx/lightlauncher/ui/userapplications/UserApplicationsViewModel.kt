package com.darekbx.lightlauncher.ui.userapplications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dao.ClickCountDao
import com.darekbx.lightlauncher.repository.local.dto.ApplicationDto
import com.darekbx.lightlauncher.repository.local.dto.ClickCountDto
import com.darekbx.lightlauncher.system.BaseApplicationsProvider
import com.darekbx.lightlauncher.system.BasePackageManager
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.ui.calculateFontWeight
import com.darekbx.lightlauncher.ui.getMaxCount
import com.darekbx.lightlauncher.ui.mapToFontSize
import com.darekbx.lightlauncher.ui.mapToScale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
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
        const val IS_HOME = "com.darekbx.home"
    }

    val applicationsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            checkIfAppShouldBeRemoved(intent)
            //restoreRemovedApplications(intent)
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

    fun loadAllApplications() {
        viewModelScope.launch {
            _uiState.value = UserApplicationsUiState.Idle
            withContext(ioDispatcher) {
                delay(250)
                val savedApps = applicationDao.fetch()
                val maxCount = getMaxCount(clickCountDao, savedApps) { application, clickCount ->
                    application.activityName != clickCount.activityName
                }
                val installedApps = applicationsProvider.listInstalledApplications()
                val applications = installedApps
                    .filter { installedApp ->
                        savedApps.none { savedApp ->
                            savedApp.activityName == installedApp.activityName
                        }
                    }
                    .map { app ->
                        with(app) {
                            val clickCount = clickCountDao.get(activityName)?.count ?: 0
                            fontWeight = calculateFontWeight(clickCount, maxCount)
                            scale = mapToScale(fontWeight)
                            fontSize = mapToFontSize(fontWeight)
                            isFromHome = packageName.contains(IS_HOME)
                        }
                        app
                    }
                    .sortedBy { it.label.lowercase() }
                _uiState.value = UserApplicationsUiState.Done(applications)
            }
        }
    }

    fun loadApplications() {
        viewModelScope.launch {
            _uiState.value = UserApplicationsUiState.Idle
            withContext(ioDispatcher) {
                val savedApps = applicationDao.fetch()
                val maxCount = getMaxCount(clickCountDao, savedApps) { application, clickCount ->
                    application.activityName == clickCount.activityName
                }
                val applications = savedApps.map { dto ->
                    val clickCount = clickCountDao.get(dto.activityName)?.count ?: 0
                    Application(
                        activityName = dto.activityName,
                        packageName = dto.packageName,
                        label = dto.label,
                        icon = packageManager.getApplicationIcon(dto.packageName),
                        order = dto.order,
                        x = dto.x,
                        y = dto.y,
                    ).apply {
                        fontWeight = calculateFontWeight(clickCount, maxCount)
                        scale = mapToScale(fontWeight)
                        fontSize = mapToFontSize(fontWeight)
                        isFromHome = dto.packageName.contains(IS_HOME)
                    }
                }
                _uiState.value = UserApplicationsUiState.Done(applications)
            }
        }
    }
}
