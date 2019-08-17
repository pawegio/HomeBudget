@file:Suppress("unused")

package com.pawegio.homebudget

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HomeBudgetApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@HomeBudgetApplication)
            modules(appModule)
        }
    }
}
