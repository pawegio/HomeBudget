@file:Suppress("unused")

package com.pawegio.homebudget

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class HomeBudgetApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@HomeBudgetApplication)
            androidLogger(Level.ERROR)
            modules(appModule)
        }
    }
}
