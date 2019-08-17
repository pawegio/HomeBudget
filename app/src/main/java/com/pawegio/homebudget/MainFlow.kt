@file:Suppress("FunctionName")

package com.pawegio.homebudget

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first

suspend fun MainFlow(
    events: Flow<MainEvent>,
    state: MutableLiveData<AppState>,
    sheetsService: GoogleSheetsService
) {
    while (!sheetsService.isSignedIn) {
        state.value = AppState.Unauthorized
        events.filterIsInstance<MainEvent.SelectSignIn>().first()
        sheetsService.signIn()
    }
    state.value = AppState.Authorized
}

sealed class MainEvent {
    object SelectSignIn : MainEvent()
}

sealed class AppState {
    object Unauthorized : AppState()
    object Authorized : AppState()
}
