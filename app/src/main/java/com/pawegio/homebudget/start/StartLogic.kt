@file:Suppress("FunctionName")

package com.pawegio.homebudget.start

import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.HomeBudgetRepository
import com.pawegio.homebudget.NavGraph
import com.pawegio.homebudget.Navigator

suspend fun StartLogic(
    repository: HomeBudgetRepository,
    api: HomeBudgetApi,
    initLogin: suspend () -> Unit,
    initPicker: suspend () -> Unit,
    initMain: suspend () -> Unit,
    navigator: Navigator
) {
    when {
        api.isSignedIn && repository.spreadsheetId != null -> {
            navigator.navigate(NavGraph.Action.toMain)
            initMain()
        }
        api.isSignedIn -> {
            navigator.navigate(NavGraph.Action.toPicker)
            initPicker()
        }
        else -> {
            navigator.navigate(NavGraph.Action.toLogin)
            initLogin()
        }
    }
}
