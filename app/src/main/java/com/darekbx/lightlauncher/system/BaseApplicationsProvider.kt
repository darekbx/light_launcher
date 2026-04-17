package com.darekbx.lightlauncher.system

import com.darekbx.lightlauncher.system.model.PackageManagerApplication

interface BaseApplicationsProvider {

    fun listPackageManagerApps(): List<PackageManagerApplication>
}