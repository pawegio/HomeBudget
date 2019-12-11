@file:Suppress("FunctionName")

package com.pawegio.homebudget.login

import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.HomeBudgetRepository
import com.pawegio.homebudget.Navigator
import com.pawegio.homebudget.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
suspend fun LoginFlow(
    actions: Flow<LoginAction>,
    repository: HomeBudgetRepository,
    api: HomeBudgetApi,
    initPickerFlow: suspend () -> Unit,
    initMainFlow: suspend () -> Unit,
    navigator: Navigator
) = try {
    cancelIfSignedIn(api)
    actions.collect {
        api.signIn()
        cancelIfSignedIn(api)
    }
} finally {
    if (repository.spreadsheetId != null) {
        navigator.navigate(R.id.action_loginFragment_to_mainFragment)
        initMainFlow()
    } else {
        navigator.navigate(R.id.action_loginFragment_to_pickerFragment)
        initPickerFlow()
    }
}

private fun cancelIfSignedIn(api: HomeBudgetApi) {
    if (api.isSignedIn) throw CancellationException()
}

sealed class LoginAction {
    object SelectSignIn : LoginAction()
}
