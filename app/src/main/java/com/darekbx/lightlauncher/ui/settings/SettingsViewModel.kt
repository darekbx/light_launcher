package com.darekbx.lightlauncher.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.lightlauncher.repository.local.SettingsStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsStore: SettingsStore) : ViewModel() {

    fun load(done: (usePages: Boolean, useCloud: Boolean, shouldUseSelfOrganizedCloud: Boolean, pageSize: Int) -> Unit) {
        viewModelScope.launch {
            val pageSize = settingsStore.pageSize.first()
            val shouldUsePages = settingsStore.shouldUsePages.first()
            val shoulduseCloud = settingsStore.shouldUseCloud.first()
            val shouldUseSelfOrganizedCloud = settingsStore.shouldUseSelfOrganizedCloud.first()
            done(shouldUsePages, shoulduseCloud, shouldUseSelfOrganizedCloud, pageSize)
        }
    }

    fun setUsePages(value: Boolean) {
        viewModelScope.launch {
            settingsStore.setUsePages(value)
        }
    }

    fun setUseCloud(value: Boolean) {
        viewModelScope.launch {
            settingsStore.setUseCloud(value)
        }
    }

    fun setUseSelfOrganizedCloud(value: Boolean) {
        viewModelScope.launch {
            settingsStore.setUseSelfOrganizedCloud(value)
        }
    }

    fun setPageSize(value: Int) {
        viewModelScope.launch {
            settingsStore.setPageSize(value)
        }
    }
}
