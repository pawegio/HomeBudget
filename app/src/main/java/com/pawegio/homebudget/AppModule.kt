package com.pawegio.homebudget

import kotlinx.coroutines.FlowPreview
import org.koin.android.experimental.dsl.viewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

@FlowPreview
val appModule = module {
    single<HomeBudgetApi> { HomeBudgetApiImpl(androidContext()) }
    viewModel<MainViewModel>()
}
