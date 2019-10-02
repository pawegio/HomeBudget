@file:Suppress("FunctionName")

package com.pawegio.homebudget.login

import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.Navigator
import com.pawegio.homebudget.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first

suspend fun LoginFlow(
    actions: Flow<LoginAction>,
    api: HomeBudgetApi,
    initMainFlow: suspend () -> Unit,
    navigator: Navigator
) {
    while (!api.isSignedIn) {
        actions.filterIsInstance<LoginAction.SelectSignIn>().first()
        api.signIn()
    }
    navigator.navigate(R.id.action_loginFragment_to_mainFragment)
    initMainFlow()
}

sealed class LoginAction {
    object SelectSignIn : LoginAction()
}
