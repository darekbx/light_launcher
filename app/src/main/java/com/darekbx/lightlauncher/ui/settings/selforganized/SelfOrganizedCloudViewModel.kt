package com.darekbx.lightlauncher.ui.settings.selforganized

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dao.ClickCountDao
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.ui.calculateFontWeight
import com.darekbx.lightlauncher.ui.getMaxCount
import com.darekbx.lightlauncher.ui.mapToFontSize
import com.darekbx.lightlauncher.ui.mapToScale
import com.darekbx.lightlauncher.ui.userapplications.UserApplicationsViewModel.Companion.IS_HOME
import com.darekbx.lightlauncher.ui.userapplications.UserApplicationsViewModel.Companion.IS_MY
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class SelfOrganizedCloudUiState {
    class Done(val applications: List<Application>) : SelfOrganizedCloudUiState()
    data object Idle : SelfOrganizedCloudUiState()
}

class SelfOrganizedCloudViewModel(
    private val applicationDao: ApplicationDao,
    private val clickCountDao: ClickCountDao,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = mutableStateOf<SelfOrganizedCloudUiState>(SelfOrganizedCloudUiState.Idle)
    val uiState: State<SelfOrganizedCloudUiState>
        get() = _uiState

    fun setLocation(application: Application) {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                applicationDao.setLocation(application.activityName, application.x, application.y)
            }
        }
    }

    fun loadApplicationsOrder() {
        viewModelScope.launch {
            _uiState.value = SelfOrganizedCloudUiState.Idle
            withContext(ioDispatcher) {
                val savedApps = applicationDao.fetch()
                val maxCount = getMaxCount(clickCountDao, savedApps) { application, clickCount ->
                    application.activityName == clickCount.activityName
                }
                val userApplications = savedApps.map { dto ->
                    val clickCount = clickCountDao.get(dto.activityName)?.count ?: 0
                    Application(
                        activityName = dto.activityName,
                        packageName = dto.packageName,
                        label = dto.label,
                        icon = null,
                        order = dto.order,
                        x = dto.x,
                        y = dto.y
                    ).apply {
                        fontWeight = calculateFontWeight(clickCount, maxCount)
                        scale = mapToScale(fontWeight)
                        fontSize = mapToFontSize(fontWeight)
                        isFromHome = dto.packageName.contains(IS_HOME)
                        isMy = dto.packageName.contains(IS_MY)
                    }
                }
                _uiState.value = SelfOrganizedCloudUiState.Done(userApplications)
            }
        }
    }
}