@file:Suppress("FunctionName")

package com.pawegio.homebudget.start

import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.HomeBudgetRepository
import com.pawegio.homebudget.NavGraph
import com.pawegio.homebudget.Navigator

fun StartLogic(
    repository: HomeBudgetRepository,
    api: HomeBudgetApi,
    navigator: Navigator
) = when {
    api.isSignedIn && repository.spreadsheetId != null -> navigator.navigate(NavGraph.Action.toMain)
    api.isSignedIn -> navigator.navigate(NavGraph.Action.toPicker)
    else -> navigator.navigate(NavGraph.Action.toLogin)
}
