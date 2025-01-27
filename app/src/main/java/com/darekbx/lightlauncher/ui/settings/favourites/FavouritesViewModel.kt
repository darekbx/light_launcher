package com.darekbx.lightlauncher.ui.settings.favourites

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dto.ApplicationDto
import com.darekbx.lightlauncher.system.BaseApplicationsProvider
import com.darekbx.lightlauncher.system.model.FavouriteApplication
import com.darekbx.lightlauncher.ui.userapplications.UserApplicationsViewModel.Companion.IS_HOME
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class FavouritesUiState {
    class Done(val applications: List<FavouriteApplication>) : FavouritesUiState()
    data object Idle : FavouritesUiState()
}

class FavouritesViewModel(
    private val applicationsProvider: BaseApplicationsProvider,
    private val applicationDao: ApplicationDao,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = mutableStateOf<FavouritesUiState>(FavouritesUiState.Idle)
    val uiState: State<FavouritesUiState>
        get() = _uiState

    fun setFavourite(application: FavouriteApplication, isFavourite: Boolean) {
        viewModelScope.launch {
            if (isFavourite) {
                applicationDao.add(
                    ApplicationDto(
                        null,
                        application.activityName,
                        application.packageName,
                        application.label
                    )
                )
            } else {
                applicationDao.delete(application.packageName)
            }
        }
    }

    fun loadFavouriteApplications() {
        viewModelScope.launch {
            _uiState.value = FavouritesUiState.Idle
            withContext(ioDispatcher) {
                val installedApps = applicationsProvider.listInstalledApplications()
                val savedApps = applicationDao.fetch()
                val favouriteApplication = installedApps.map { installedApp ->
                    val savedApp = savedApps.find { it.activityName == installedApp.activityName }
                    FavouriteApplication(
                        activityName = installedApp.activityName,
                        packageName = installedApp.packageName,
                        label = installedApp.label,
                        isFavourite = savedApp != null,
                        isFromHome = savedApp?.packageName?.contains(IS_HOME) ?: false
                    )
                }
                _uiState.value = FavouritesUiState.Done(favouriteApplication)
            }
        }
    }
}
