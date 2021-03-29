@file:Suppress("FunctionName")

package com.pawegio.homebudget.login

import com.pawegio.homebudget.*
import com.pawegio.homebudget.util.ToastNotifier
import io.reactivex.Observable
import kotlinx.coroutines.rx2.collect

suspend fun LoginLogic(
    actions: Observable<LoginAction>,
    repository: HomeBudgetRepository,
    api: HomeBudgetApi,
    toastNotifier: ToastNotifier,
    navigator: Navigator
) {
    actions.collect {
        try {
            api.signIn()
        } catch (e: HomeBudgetApiException) {
            toastNotifier.notify(R.string.sign_in_error)
        }
        if (api.isSignedIn) {
            if (repository.spreadsheetId != null) {
                navigator.popBackStack()
                navigator.navigate(NavGraph.Action.toMain)
            } else {
                navigator.popBackStack()
                navigator.navigate(NavGraph.Action.toPicker)
            }
        }
    }
}

sealed class LoginAction {
    object SelectSignIn : LoginAction()
}
