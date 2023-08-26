package com.pawegio.homebudget

import com.pawegio.homebudget.login.LoginViewModel
import com.pawegio.homebudget.main.MainViewModel
import com.pawegio.homebudget.main.transaction.TransactionViewModel
import com.pawegio.homebudget.picker.PickerViewModel
import com.pawegio.homebudget.start.StartViewModel
import com.pawegio.homebudget.util.*
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.time.Clock

val appModule = module {
    single<HomeBudgetRepository> { HomeBudgetRepositoryImpl(androidContext()) }
    single<ColumnResolver> { ColumnResolverImpl }
    single<HomeBudgetApi> { HomeBudgetApiImpl(androidContext(), get(), get()) }
    single<Clock> { Clock.systemDefaultZone() }
    single<HowToLauncher> { HowToLauncherImpl(androidContext()) }
    single<SpreadsheetLauncher> { SpreadsheetLauncherImpl(androidContext(), get()) }
    single<Navigator> { NavigatorImpl(androidApplication(), ::appNavController) }
    single<ToastNotifier> { ToastNotifierImpl(androidContext()) }
    viewModel { StartViewModel(get(), get(), get()) }
    viewModel { LoginViewModel(get(), get(), get(), get()) }
    viewModel { PickerViewModel(get(), get(), get()) }
    viewModel { MainViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { parameters -> TransactionViewModel(parameters.get(), get(), get(), get(), get()) }
}
