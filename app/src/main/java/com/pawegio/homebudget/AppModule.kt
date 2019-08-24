package com.pawegio.homebudget

import kotlinx.coroutines.FlowPreview
import org.koin.android.experimental.dsl.viewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.threeten.bp.Clock

@FlowPreview
val appModule = module {
    single<HomeBudgetApi> { HomeBudgetApiImpl(androidContext()) }
    single<Clock> { Clock.systemDefaultZone() }
    viewModel<MainViewModel>()
}
