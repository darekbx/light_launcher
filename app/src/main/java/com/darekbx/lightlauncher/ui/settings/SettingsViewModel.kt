package com.darekbx.lightlauncher.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.lightlauncher.repository.local.SettingsStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsStore: SettingsStore) : ViewModel() {

    fun load(done: (usePages: Boolean, pageSize: Int) -> Unit) {
        viewModelScope.launch {
            val pageSize = settingsStore.pageSize.first()
            val shouldUsePages = settingsStore.shouldUsePages.first()
            done(shouldUsePages, pageSize)
        }
    }

    fun setUsePages(value: Boolean) {
        viewModelScope.launch {
            settingsStore.setUsePages(value)
        }
    }

    fun setPageSize(value: Int) {
        viewModelScope.launch {
            settingsStore.setPageSize(value)
        }
    }
}
