package com.darekbx.lightlauncher.ui.settings.favourites

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dto.ApplicationDto
import com.darekbx.lightlauncher.system.BaseApplicationsProvider
import com.darekbx.lightlauncher.system.model.FavouriteApplication
import kotlinx.coroutines.launch

sealed class FavouritesUiState {
    class Done(val applications: List<FavouriteApplication>) : FavouritesUiState()
    data object Idle : FavouritesUiState()
}

class FavouritesViewModel(
    private val applicationsProvider: BaseApplicationsProvider,
    private val applicationDao: ApplicationDao
) : ViewModel() {

    private val _uiState = mutableStateOf<FavouritesUiState>(FavouritesUiState.Idle)
    val uiState: State<FavouritesUiState>
        get() = _uiState

    fun setFavourite(packageName: String, isFavourite: Boolean) {
        viewModelScope.launch {
            if (isFavourite) {
                applicationDao.add(ApplicationDto(null, packageName))
            } else {
                applicationDao.delete(packageName)
            }
            loadFavouriteApplications()
        }
    }

    fun loadFavouriteApplications() {
        viewModelScope.launch {
            val installedApps = applicationsProvider.listInstalledApplications()
            val savedApps = applicationDao.fetch()
            val favouriteApplication = installedApps.map { installedApp ->
                val savedApp = savedApps.find { it.packageName == installedApp.packageName }
                FavouriteApplication(
                    packageName = installedApp.packageName,
                    label = installedApp.label,
                    isFavourite = savedApp != null
                )
            }
            _uiState.value = FavouritesUiState.Done(favouriteApplication)
        }
    }
}
