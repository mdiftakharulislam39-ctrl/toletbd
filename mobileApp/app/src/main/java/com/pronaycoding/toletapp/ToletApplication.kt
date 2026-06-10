package com.pronaycoding.toletapp

import android.app.Application
import com.pronaycoding.toletapp.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ToletApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ToletApplication)
            modules(appModules)
        }
    }
}
