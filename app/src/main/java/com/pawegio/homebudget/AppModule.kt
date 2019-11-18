package com.pawegio.homebudget

import com.pawegio.homebudget.util.SpreadsheetLauncher
import com.pawegio.homebudget.util.SpreadsheetLauncherImpl
import kotlinx.coroutines.FlowPreview
import org.koin.android.experimental.dsl.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.threeten.bp.Clock

@FlowPreview
val appModule = module {
    single<HomeBudgetApi> { HomeBudgetApiImpl(androidContext()) }
    single<Clock> { Clock.systemDefaultZone() }
    single<SpreadsheetLauncher> { SpreadsheetLauncherImpl(androidContext()) }
    single<Navigator> { NavigatorImpl(androidApplication(), ::appNavController) }
    viewModel<MainViewModel>()
}
