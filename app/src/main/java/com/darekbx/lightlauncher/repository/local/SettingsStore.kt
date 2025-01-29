package com.darekbx.lightlauncher.repository.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.map

class SettingsStore(
    private val dataStore: DataStore<Preferences>
) {

    val pageSize = dataStore.data.map { preferences ->
        preferences[PAGE_SIZE_KEY] ?: DEFAULT_PAGE_SIZE
    }

    val shouldUsePages = dataStore.data.map { preferences ->
        preferences[USE_PAGES_KEY] ?: DEFAULT_USE_PAGES
    }

    val shouldUseCloud = dataStore.data.map { preferences ->
        preferences[USE_CLOUD_KEY] ?: DEFAULT_USE_CLOUD
    }

    val shouldUseSelfOrganizedCloud = dataStore.data.map { preferences ->
        preferences[USE_SELF_ORGANIZED_CLOUD_KEY] ?: DEFAULT_USE_SELF_ORGANIZED_CLOUD
    }

    suspend fun setUsePages(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[USE_PAGES_KEY] = value
        }
    }

    suspend fun setUseCloud(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[USE_CLOUD_KEY] = value
        }
    }

    suspend fun setUseSelfOrganizedCloud(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[USE_SELF_ORGANIZED_CLOUD_KEY] = value
        }
    }

    suspend fun setPageSize(value: Int) {
        dataStore.edit { preferences ->
            preferences[PAGE_SIZE_KEY] = value
        }
    }

    companion object {
        private val USE_PAGES_KEY = booleanPreferencesKey("use_pages")
        private val USE_CLOUD_KEY = booleanPreferencesKey("use_cloud")
        private val USE_SELF_ORGANIZED_CLOUD_KEY = booleanPreferencesKey("use_self_organized_cloud")
        private val PAGE_SIZE_KEY = intPreferencesKey("page_size")

        private const val DEFAULT_PAGE_SIZE = 10
        private const val DEFAULT_USE_PAGES = true
        private const val DEFAULT_USE_CLOUD = false
        private const val DEFAULT_USE_SELF_ORGANIZED_CLOUD = false
    }
}
