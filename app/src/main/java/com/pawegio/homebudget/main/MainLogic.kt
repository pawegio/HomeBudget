@file:Suppress("FunctionName")

package com.pawegio.homebudget.main

import androidx.lifecycle.MutableLiveData
import com.pawegio.homebudget.*
import com.pawegio.homebudget.main.MainAction.*
import com.pawegio.homebudget.util.SpreadsheetLauncher
import com.pawegio.homebudget.util.ToastNotifier
import io.reactivex.Observable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.awaitFirst
import org.threeten.bp.Clock
import org.threeten.bp.Month
import org.threeten.bp.ZoneId

suspend fun MainLogic(
    actions: Observable<MainAction>,
    monthType: MutableLiveData<MonthType>,
    monthlyBudget: MutableLiveData<MonthlyBudget>,
    isLoading: MutableLiveData<Boolean>,
    repository: HomeBudgetRepository,
    api: HomeBudgetApi,
    spreadsheetLauncher: SpreadsheetLauncher,
    clock: Clock,
    toastNotifier: ToastNotifier,
    navigator: Navigator
) {
    var month = clock.instant().atZone(ZoneId.systemDefault()).month
    var afterTransaction = false
    coroutineScope {
        launch { loadMonth(month, monthType, monthlyBudget, isLoading, api, navigator) }
        launch { checkFinancialYear(api, clock, toastNotifier) }
        loop@ while (isActive) {
            when (actions.awaitFirst()) {
                Resume -> {
                    if (afterTransaction) {
                        loadMonth(month, monthType, monthlyBudget, isLoading, api, navigator)
                        afterTransaction = false
                    }
                }
                Resume, Refresh, TryAgain -> {
                    loadMonth(month, monthType, monthlyBudget, isLoading, api, navigator)
                }
                OpenSpreadsheet -> spreadsheetLauncher.launch()
                SelectPrevMonth -> {
                    month -= 1
                    loadMonth(month, monthType, monthlyBudget, isLoading, api, navigator)
                }
                SelectNextMonth -> {
                    month += 1
                    loadMonth(month, monthType, monthlyBudget, isLoading, api, navigator)
                }
                AddTransaction -> {
                    afterTransaction = true
                    navigator.navigate(
                        NavGraph.Action.toTransaction,
                        NavGraph.Args.monthlyBudget to monthlyBudget.value
                    )
                }
                PickDocumentAgain -> navigator.navigate(NavGraph.Action.toPicker)
                SelectAbout -> navigator.navigate(NavGraph.Action.toAbout)
                SignOut -> {
                    repository.spreadsheetId = null
                    api.signOut()
                    navigator.popBackStack()
                    navigator.navigate(NavGraph.Action.toLogin)
                    break@loop
                }
            }
        }
    }
}

private suspend fun loadMonth(
    month: Month,
    monthType: MutableLiveData<MonthType>,
    monthlyBudget: MutableLiveData<MonthlyBudget>,
    isLoading: MutableLiveData<Boolean>,
    api: HomeBudgetApi,
    navigator: Navigator
) {
    isLoading.value = true
    monthType.value = when (month) {
        Month.JANUARY -> MonthType.FIRST
        Month.DECEMBER -> MonthType.LAST
        else -> MonthType.MIDDLE
    }
    try {
        monthlyBudget.value = api.getMonthlyBudget(month)
    } catch (e: HomeBudgetApiException) {
        monthlyBudget.value = null
        navigator.navigate(NavGraph.Action.toLoadError)
    } finally {
        isLoading.value = false
    }
}

private suspend fun checkFinancialYear(
    api: HomeBudgetApi,
    clock: Clock,
    toastNotifier: ToastNotifier
) {
    val year = api.getFinancialYear()
    val currentYear = clock.instant().atZone(ZoneId.systemDefault()).year
    if (year != null && year < currentYear) {
        toastNotifier.notify(R.string.old_financial_year_warning, year)
    }
}

enum class MonthType {
    FIRST, MIDDLE, LAST
}

sealed class MainAction {
    object Resume : MainAction()
    object Refresh : MainAction()
    object OpenSpreadsheet : MainAction()
    object SelectPrevMonth : MainAction()
    object SelectNextMonth : MainAction()
    object TryAgain : MainAction()
    object AddTransaction : MainAction()
    object PickDocumentAgain : MainAction()
    object SelectAbout : MainAction()
    object SignOut : MainAction()
}
