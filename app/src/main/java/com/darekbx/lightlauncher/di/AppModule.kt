package com.darekbx.lightlauncher.di

import android.app.Application
import androidx.room.Room
import com.darekbx.lightlauncher.repository.local.AppDatabase
import com.darekbx.lightlauncher.repository.local.dao.ApplicationDao
import com.darekbx.lightlauncher.repository.local.dao.ClickCountDao
import com.darekbx.lightlauncher.system.ApplicationsProvider
import com.darekbx.lightlauncher.system.BaseApplicationsProvider
import com.darekbx.lightlauncher.system.BasePackageManager
import com.darekbx.lightlauncher.system.PackageManagerWrapper
import com.darekbx.lightlauncher.ui.settings.favourites.FavouritesViewModel
import com.darekbx.lightlauncher.ui.settings.order.OrderViewModel
import com.darekbx.lightlauncher.ui.userapplications.UserApplicationsViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> {
        Room
            .databaseBuilder(get<Application>(), AppDatabase::class.java, AppDatabase.DB_NAME)
            .build()
    }
    single<ApplicationDao> { get<AppDatabase>().applicationDao() }
    single<ClickCountDao> { get<AppDatabase>().clickCountDao() }
}

val appModule = module {
    single(named("io_dispatcher")) { Dispatchers.IO }
    single<BasePackageManager> { PackageManagerWrapper(androidContext().packageManager) }
    single<BaseApplicationsProvider> { ApplicationsProvider(get()) }
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
        UserApplicationsViewModel(
            get(),
            get(),
            get(),
            get(named("io_dispatcher")),
            androidContext()
        )
    }
}
