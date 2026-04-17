package com.darekbx.lightlauncher.ui.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.lightlauncher.domain.FetchApplicationsDataUseCase
import com.darekbx.lightlauncher.repository.local.dao.ClickCountDao
import com.darekbx.lightlauncher.repository.local.dto.ClickCountDto
import com.darekbx.lightlauncher.system.model.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainScreenViewModel(
    private val fetchApplicationsDataUseCase: FetchApplicationsDataUseCase,
    private val clickCountDao: ClickCountDao,
) : ViewModel() {

    private val _applicationsData = mutableStateOf(emptyList<List<Application>>())
    val applicationsData: State<List<List<Application>>>
        get() = _applicationsData

    fun loadApplications(forceRefresh: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _applicationsData.value = fetchApplicationsDataUseCase(forceReload = forceRefresh)
            }
        }
    }

    fun increaseClickCount(item: Application) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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
}