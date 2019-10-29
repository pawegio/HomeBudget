@file:Suppress("FunctionName")

package com.pawegio.homebudget.main

import androidx.lifecycle.MutableLiveData
import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.MonthlyBudget
import com.pawegio.homebudget.util.SpreadsheetLauncher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.threeten.bp.Clock
import org.threeten.bp.ZoneId

suspend fun MainFlow(
    actions: Flow<MainAction>,
    monthlyBudget: MutableLiveData<MonthlyBudget>,
    api: HomeBudgetApi,
    spreadshetLauncher: SpreadsheetLauncher,
    clock: Clock
) = coroutineScope {
    launch {
        while (isActive) {
            actions.filterIsInstance<MainAction.OpenSpreadsheet>().first()
            spreadshetLauncher.launch()
        }
    }
    val month = clock.instant().atZone(ZoneId.systemDefault()).month
    monthlyBudget.value = api.getMonthlyBudget(month)
}

sealed class MainAction {
    object OpenSpreadsheet : MainAction()
}
