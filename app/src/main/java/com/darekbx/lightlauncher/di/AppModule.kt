package com.darekbx.lightlauncher.di

import android.app.Application
import androidx.room.Room
import com.darekbx.lightlauncher.repository.local.AppDatabase
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.system.ApplicationsProvider
import com.darekbx.lightlauncher.system.BaseApplicationsProvider
import com.darekbx.lightlauncher.system.BasePackageManager
import com.darekbx.lightlauncher.system.PackageManagerWrapper
import com.darekbx.lightlauncher.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> {
        Room
            .databaseBuilder(get<Application>(), AppDatabase::class.java, AppDatabase.DB_NAME)
            .build()
    }
    single<ApplicationDao> { get<AppDatabase>().applicationDao() }
}

val appModule = module {
    single<BasePackageManager> { PackageManagerWrapper(androidContext().packageManager) }
    single<BaseApplicationsProvider> { ApplicationsProvider(get()) }
}

val viewModelModule = module {
     viewModel { SettingsViewModel(get<BaseApplicationsProvider>(), get()) }
}
