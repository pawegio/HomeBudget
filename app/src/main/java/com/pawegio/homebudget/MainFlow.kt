@file:Suppress("FunctionName")

package com.pawegio.homebudget

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first

suspend fun MainFlow(
    actions: Flow<MainAction>,
    state: MutableLiveData<AppState>,
    api: HomeBudgetApi
) {
    while (!api.isSignedIn) {
        state.value = AppState.Unauthorized
        actions.filterIsInstance<MainAction.SelectSignIn>().first()
        api.signIn()
    }
    state.value = AppState.Authorized
    api.getMonthlyBudget()
}

sealed class MainAction {
    object SelectSignIn : MainAction()
}

sealed class AppState {
    object Unauthorized : AppState()
    object Authorized : AppState()
}
