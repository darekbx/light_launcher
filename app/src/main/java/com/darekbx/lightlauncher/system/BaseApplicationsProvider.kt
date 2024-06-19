package com.darekbx.lightlauncher.system

import com.darekbx.lightlauncher.system.model.Application

interface BaseApplicationsProvider {

    fun listInstalledApplications(): List<Application>
}