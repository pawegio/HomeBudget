@file:Suppress("FunctionName")

package com.pawegio.homebudget.login

import com.pawegio.homebudget.*
import com.pawegio.homebudget.util.ToastNotifier
import io.reactivex.Observable
import kotlinx.coroutines.rx2.collect

suspend fun LoginFlow(
    actions: Observable<LoginAction>,
    repository: HomeBudgetRepository,
    api: HomeBudgetApi,
    toastNotifier: ToastNotifier,
    initPickerFlow: suspend () -> Unit,
    initMainFlow: suspend () -> Unit,
    navigator: Navigator
) {
    if (api.isSignedIn) proceed(repository, navigator, initMainFlow, initPickerFlow)
    actions.collect {
        try {
            api.signIn()
        } catch (e: HomeBudgetApiException) {
            toastNotifier.notify(R.string.sign_in_error)
        }
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
