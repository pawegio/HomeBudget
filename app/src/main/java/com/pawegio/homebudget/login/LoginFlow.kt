@file:Suppress("FunctionName")

package com.pawegio.homebudget.login

import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.HomeBudgetRepository
import com.pawegio.homebudget.Navigator
import com.pawegio.homebudget.R
import io.reactivex.Observable
import kotlinx.coroutines.rx2.collect

suspend fun LoginFlow(
    actions: Observable<LoginAction>,
    repository: HomeBudgetRepository,
    api: HomeBudgetApi,
    initPickerFlow: suspend () -> Unit,
    initMainFlow: suspend () -> Unit,
    navigator: Navigator
) {
    if (api.isSignedIn) proceed(repository, navigator, initMainFlow, initPickerFlow)
    actions.collect {
        api.signIn()
        if (api.isSignedIn) proceed(repository, navigator, initMainFlow, initPickerFlow)
    }
}

private suspend fun proceed(
    repository: HomeBudgetRepository,
    navigator: Navigator,
    initMainFlow: suspend () -> Unit,
    initPickerFlow: suspend () -> Unit
) {
    if (repository.spreadsheetId != null) {
        navigator.navigate(R.id.action_loginFragment_to_mainFragment)
        initMainFlow()
    } else {
        navigator.navigate(R.id.action_loginFragment_to_pickerFragment)
        initPickerFlow()
    }
}

sealed class LoginAction {
    object SelectSignIn : LoginAction()
}
