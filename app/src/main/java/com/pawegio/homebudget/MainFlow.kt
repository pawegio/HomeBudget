@file:Suppress("FunctionName")

package com.pawegio.homebudget

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first

suspend fun MainFlow(
    actions: Flow<MainAction>,
    state: MutableLiveData<AppState>,
    sheetsService: GoogleSheetsService
) {
    while (!sheetsService.isSignedIn) {
        state.value = AppState.Unauthorized
        actions.filterIsInstance<MainAction.SelectSignIn>().first()
        sheetsService.signIn()
    }
    state.value = AppState.Authorized
    sheetsService.getMonthlyBudget()
}

sealed class MainAction {
    object SelectSignIn : MainAction()
}

sealed class AppState {
    object Unauthorized : AppState()
    object Authorized : AppState()
}
