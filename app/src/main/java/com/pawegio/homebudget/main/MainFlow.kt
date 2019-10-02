@file:Suppress("FunctionName")

package com.pawegio.homebudget.main

import androidx.lifecycle.MutableLiveData
import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.MonthlyBudget
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.Clock
import org.threeten.bp.ZoneId

suspend fun MainFlow(
    actions: Flow<MainAction>,
    monthlyBudget: MutableLiveData<MonthlyBudget>,
    api: HomeBudgetApi,
    clock: Clock
) {
    val month = clock.instant().atZone(ZoneId.systemDefault()).month
    monthlyBudget.value = api.getMonthlyBudget(month)
}

sealed class MainAction
