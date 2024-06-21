package com.darekbx.lightlauncher.ui.userapplications

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.system.model.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class UserApplicationsUiState {
    class Done(val applications: List<Application>) : UserApplicationsUiState()
    data object Idle : UserApplicationsUiState()
}

class UserApplicationsViewModel(
    private val applicationDao: ApplicationDao,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = mutableStateOf<UserApplicationsUiState>(UserApplicationsUiState.Idle)
    val uiState: State<UserApplicationsUiState>
        get() = _uiState

    fun loadApplications() {
        viewModelScope.launch {
            _uiState.value = UserApplicationsUiState.Idle
            withContext(ioDispatcher) {
                val savedApps = applicationDao.fetch()
                val applications = savedApps.map { app ->
                    Application(
                        packageName = app.packageName,
                        label = app.label,
                    )
                }
                _uiState.value = UserApplicationsUiState.Done(applications)
            }
        }
    }
}
