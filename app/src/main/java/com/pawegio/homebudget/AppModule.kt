package com.pawegio.homebudget

import com.pawegio.homebudget.util.*
import org.koin.android.experimental.dsl.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.threeten.bp.Clock

val appModule = module {
    single<HomeBudgetRepository> { HomeBudgetRepositoryImpl(androidContext()) }
    single<ColumnResolver> { ColumnResolverImpl }
    single<HomeBudgetApi> { HomeBudgetApiImpl(androidContext(), get(), get()) }
    single<Clock> { Clock.systemDefaultZone() }
    single<HowToLauncher> { HowToLauncherImpl(androidContext()) }
    single<SpreadsheetLauncher> { SpreadsheetLauncherImpl(androidContext(), get()) }
    single<Navigator> { NavigatorImpl(androidApplication(), ::appNavController) }
    single<ToastNotifier> { ToastNotifierImpl(androidContext()) }
    viewModel<MainViewModel>()
}
