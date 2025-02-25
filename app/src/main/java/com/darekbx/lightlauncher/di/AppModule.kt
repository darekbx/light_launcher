package com.darekbx.lightlauncher.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.darekbx.lightlauncher.repository.local.AppDatabase
import com.darekbx.lightlauncher.repository.local.AppDatabase.Companion.MIGRATION_1_2
import com.darekbx.lightlauncher.repository.local.AppDatabase.Companion.MIGRATION_2_3
import com.darekbx.lightlauncher.repository.local.SettingsStore
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dao.ClickCountDao
import com.darekbx.lightlauncher.repository.local.dao.NotificationDao
import com.darekbx.lightlauncher.system.ActivityStarter
import com.darekbx.lightlauncher.system.ApplicationsProvider
import com.darekbx.lightlauncher.system.BaseApplicationsProvider
import com.darekbx.lightlauncher.system.BasePackageManager
import com.darekbx.lightlauncher.system.PackageManagerWrapper
import com.darekbx.lightlauncher.ui.settings.SettingsViewModel
import com.darekbx.lightlauncher.ui.settings.favourites.FavouritesViewModel
import com.darekbx.lightlauncher.ui.settings.order.OrderViewModel
import com.darekbx.lightlauncher.ui.settings.selforganized.SelfOrganizedCloudViewModel
import com.darekbx.lightlauncher.ui.statistics.StatisticsViewModel
import com.darekbx.lightlauncher.ui.userapplications.NotificationViewModel
import com.darekbx.lightlauncher.ui.userapplications.UserApplicationsViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "launcher_preferences")

val databaseModule = module {
    single<AppDatabase> {
        Room
            .databaseBuilder(get<Application>(), AppDatabase::class.java, AppDatabase.DB_NAME)
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_2_3)
            .build()
    }
    single<ApplicationDao> { get<AppDatabase>().applicationDao() }
    single<ClickCountDao> { get<AppDatabase>().clickCountDao() }
    single<NotificationDao> { get<AppDatabase>().notificationDao() }
}

val appModule = module {
    single(named("io_dispatcher")) { Dispatchers.IO }
    single<BasePackageManager> { PackageManagerWrapper(androidContext().packageManager) }
    single<BaseApplicationsProvider> { ApplicationsProvider(get()) }
    single { androidContext().dataStore }
    single { SettingsStore(get()) }
    single { ActivityStarter(androidContext()) }
}

val viewModelModule = module {
    viewModel {
        FavouritesViewModel(
            get<BaseApplicationsProvider>(),
            get(),
            get(named("io_dispatcher"))
        )
    }
    viewModel {
        OrderViewModel(
            get(),
            get(named("io_dispatcher"))
        )
    }
    viewModel {
        SelfOrganizedCloudViewModel(
            get(),
            get(),
            get(named("io_dispatcher"))
        )
    }
    viewModel {
        UserApplicationsViewModel(
            get(),
            get(),
            get(),
            get(),
            get(named("io_dispatcher"))
        )
    }
    viewModel {
        StatisticsViewModel(get(), get())
    }
    viewModel {
        NotificationViewModel(get())
    }
    viewModel {
        SettingsViewModel(get())
    }
}
