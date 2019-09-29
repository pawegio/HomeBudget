@file:Suppress("FunctionName")

package com.pawegio.homebudget

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import org.threeten.bp.Clock
import org.threeten.bp.ZoneId

suspend fun MainFlow(
    actions: Flow<MainAction>,
    state: MutableLiveData<AppState>,
    monthlyBudget: MutableLiveData<MonthlyBudget>,
    api: HomeBudgetApi,
    clock: Clock,
    navigator: Navigator
) {
    while (!api.isSignedIn) {
        state.value = AppState.Unauthorized
        actions.filterIsInstance<MainAction.SelectSignIn>().first()
        api.signIn()
    }
    state.value = AppState.Authorized
    navigator.navigate(R.id.action_loginFragment_to_mainFragment)
    val month = clock.instant().atZone(ZoneId.systemDefault()).month
    monthlyBudget.value = api.getMonthlyBudget(month)
}

sealed class MainAction {
    object SelectSignIn : MainAction()
}

sealed class AppState {
    object Unauthorized : AppState()
    object Authorized : AppState()
}
