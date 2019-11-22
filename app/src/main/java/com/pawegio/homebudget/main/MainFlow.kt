@file:Suppress("FunctionName")

package com.pawegio.homebudget.main

import androidx.lifecycle.MutableLiveData
import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.MonthlyBudget
import com.pawegio.homebudget.main.MainAction.*
import com.pawegio.homebudget.util.SpreadsheetLauncher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.threeten.bp.Clock
import org.threeten.bp.Month
import org.threeten.bp.ZoneId

@ExperimentalCoroutinesApi
suspend fun MainFlow(
    actions: Flow<MainAction>,
    monthType: MutableLiveData<MonthType>,
    monthlyBudget: MutableLiveData<MonthlyBudget>,
    isLoading: MutableLiveData<Boolean>,
    api: HomeBudgetApi,
    spreadsheetLauncher: SpreadsheetLauncher,
    clock: Clock
) {
    var month = clock.instant().atZone(ZoneId.systemDefault()).month
    coroutineScope {
        launch {
            actions.collectLatest { action ->
                when (action) {
                    Refresh -> loadMonth(month, monthType, monthlyBudget, isLoading, api)
                    OpenSpreadsheet -> spreadsheetLauncher.launch()
                    SelectPrevMonth -> {
                        month -= 1
                        loadMonth(month, monthType, monthlyBudget, isLoading, api)
                    }
                    SelectNextMonth -> {
                        month += 1
                        loadMonth(month, monthType, monthlyBudget, isLoading, api)
                    }
                }
            }
        }
        loadMonth(month, monthType, monthlyBudget, isLoading, api)
    }
}

private suspend fun loadMonth(
    month: Month,
    monthType: MutableLiveData<MonthType>,
    monthlyBudget: MutableLiveData<MonthlyBudget>,
    isLoading: MutableLiveData<Boolean>,
    api: HomeBudgetApi
) {
    isLoading.value = true
    monthType.value = when (month) {
        Month.JANUARY -> MonthType.FIRST
        Month.DECEMBER -> MonthType.LAST
        else -> MonthType.MIDDLE
    }
    monthlyBudget.value = api.getMonthlyBudget(month)
    isLoading.value = false
}

enum class MonthType {
    FIRST, MIDDLE, LAST
}

sealed class MainAction {
    object Refresh : MainAction()
    object OpenSpreadsheet : MainAction()
    object SelectPrevMonth : MainAction()
    object SelectNextMonth : MainAction()
}
