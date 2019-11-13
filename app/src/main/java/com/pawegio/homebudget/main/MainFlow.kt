@file:Suppress("FunctionName")

package com.pawegio.homebudget.main

import androidx.lifecycle.MutableLiveData
import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.MonthlyBudget
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
    api: HomeBudgetApi,
    spreadsheetLauncher: SpreadsheetLauncher,
    clock: Clock
) {
    var month = clock.instant().atZone(ZoneId.systemDefault()).month
    coroutineScope {
        launch {
            actions.collectLatest { action ->
                when (action) {
                    MainAction.OpenSpreadsheet -> spreadsheetLauncher.launch()
                    MainAction.SelectPrevMonth -> {
                        month -= 1
                        loadMonth(month, monthType, monthlyBudget, api)
                    }
                    MainAction.SelectNextMonth -> {
                        month += 1
                        loadMonth(month, monthType, monthlyBudget, api)
                    }
                }
            }
        }
        loadMonth(month, monthType, monthlyBudget, api)
    }
}

private suspend fun loadMonth(
    month: Month,
    monthType: MutableLiveData<MonthType>,
    monthlyBudget: MutableLiveData<MonthlyBudget>,
    api: HomeBudgetApi
) {
    monthType.value = when (month) {
        Month.JANUARY -> MonthType.FIRST
        Month.DECEMBER -> MonthType.LAST
        else -> MonthType.MIDDLE
    }
    monthlyBudget.value = api.getMonthlyBudget(month)
}

enum class MonthType {
    FIRST, MIDDLE, LAST
}

sealed class MainAction {
    object OpenSpreadsheet : MainAction()
    object SelectPrevMonth : MainAction()
    object SelectNextMonth : MainAction()
}
