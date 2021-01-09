@file:Suppress("FunctionName")

package com.pawegio.homebudget.main

import androidx.lifecycle.MutableLiveData
import com.pawegio.homebudget.*
import com.pawegio.homebudget.main.MainAction.*
import com.pawegio.homebudget.main.transaction.TransactionResult
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
    toastNotifier: ToastNotifier,
    clock: Clock,
    initPicker: suspend () -> Unit,
    initTransaction: suspend () -> TransactionResult,
    navigator: Navigator
) {
    var month = clock.instant().atZone(ZoneId.systemDefault()).month
    coroutineScope {
        launch { loadMonth(month, monthType, monthlyBudget, isLoading, api, navigator) }
        loop@ while (isActive) {
            when (actions.awaitFirst()) {
                Refresh, TryAgain -> {
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
                    navigator.navigate(R.id.action_mainFragment_to_transactionFragment)
                    val result = initTransaction()
                    if (result == TransactionResult.SUCCESS) {
                        toastNotifier.notify(R.string.transaction_added_message)
                        loadMonth(month, monthType, monthlyBudget, isLoading, api, navigator)
                    }
                }
                PickDocumentAgain -> {
                    navigator.navigate(R.id.action_mainFragment_to_pickerFragment)
                    initPicker()
                }
                SelectAbout -> navigator.navigate(R.id.action_mainFragment_to_aboutFragment)
                SignOut -> {
                    repository.spreadsheetId = null
                    api.signOut()
                    break@loop
                }
            }
        }
    }
    navigator.popBackStack()
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
        navigator.navigate(R.id.action_mainFragment_to_loadErrorFragment)
    } finally {
        isLoading.value = false
    }
}

enum class MonthType {
    FIRST, MIDDLE, LAST
}

sealed class MainAction {
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
