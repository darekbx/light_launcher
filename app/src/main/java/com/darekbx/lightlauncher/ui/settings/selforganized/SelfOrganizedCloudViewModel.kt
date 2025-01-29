package com.darekbx.lightlauncher.ui.settings.selforganized

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.system.model.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class SelfOrganizedCloudUiState {
    class Done(val applications: List<Application>) : SelfOrganizedCloudUiState()
    data object Idle : SelfOrganizedCloudUiState()
}

class SelfOrganizedCloudViewModel(
    private val applicationDao: ApplicationDao,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = mutableStateOf<SelfOrganizedCloudUiState>(SelfOrganizedCloudUiState.Idle)
    val uiState: State<SelfOrganizedCloudUiState>
        get() = _uiState

    fun loadApplicationsOrder() {
        viewModelScope.launch {
            _uiState.value = SelfOrganizedCloudUiState.Idle
            withContext(ioDispatcher) {
                val savedApps = applicationDao.fetch()
                val userApplications = savedApps.map { dto ->
                    Application(
                        activityName = dto.activityName,
                        packageName = dto.packageName,
                        label = dto.label,
                        icon = null,
                        order = dto.order,
                        x = dto.x,
                        y = dto.y
                    )
                }
                _uiState.value = SelfOrganizedCloudUiState.Done(userApplications)
            }
        }
    }
}