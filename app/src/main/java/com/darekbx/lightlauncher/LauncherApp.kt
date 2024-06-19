package com.darekbx.lightlauncher

import android.app.Application
import com.darekbx.lightlauncher.di.appModule
import com.darekbx.lightlauncher.di.databaseModule
import com.darekbx.lightlauncher.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext

class LauncherApp: Application() {

    override fun onCreate() {
        super.onCreate()

        GlobalContext.startKoin {
            androidLogger()
            androidContext(this@LauncherApp)
            modules(appModule, databaseModule, viewModelModule)
        }
    }
}
