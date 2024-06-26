package com.darekbx.lightlauncher.ui.settings.order

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.system.model.ApplicationOrder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class OrderedUiState {
    class Done(val applications: List<ApplicationOrder>) : OrderedUiState()
    data object Idle : OrderedUiState()
}

class OrderViewModel(
    private val applicationDao: ApplicationDao,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = mutableStateOf<OrderedUiState>(OrderedUiState.Idle)
    val uiState: State<OrderedUiState>
        get() = _uiState

    fun setOrder(order: List<ApplicationOrder>) {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                order.forEach {
                    applicationDao.setOrder(it.activityName, (it.order ?: 0))
                }
            }
        }
    }

    fun loadApplicationsOrder() {
        viewModelScope.launch {
            _uiState.value = OrderedUiState.Idle
            withContext(ioDispatcher) {
                val savedApps = applicationDao.fetch()
                val favouriteApplication = savedApps.map { app ->
                    ApplicationOrder(
                        activityName = app.activityName,
                        packageName = app.packageName,
                        label = app.label,
                        order = app?.order
                    )
                }
                _uiState.value = OrderedUiState.Done(favouriteApplication)
            }
        }
    }
}
