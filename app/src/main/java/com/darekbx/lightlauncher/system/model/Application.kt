package com.darekbx.lightlauncher.system.model

import androidx.compose.runtime.Stable
import com.darekbx.lightlauncher.repository.local.dto.ApplicationCacheDto

@Stable
data class Application(
    val activityName: String,
    val packageName: String,
    val label: String,
    val order: Int = -1,
    val isFromHome: Boolean = false,
    val isMy: Boolean = false
) {
    val isHomeOrMy = isFromHome || isMy

    companion object {

        fun ApplicationCacheDto.toApplicationCache() =
            Application(activityName, packageName, label, order, isFromHome, isMy)

        fun Application.toApplicationCacheDto(page: Int) =
            ApplicationCacheDto(null, activityName, packageName, label, order, isFromHome, isMy, page)
    }
}

@Stable
data class PackageManagerApplication(
    val activityName: String,
    val packageName: String,
    val label: String
)
