@file:Suppress("FunctionName")

package com.pawegio.homebudget.picker

import com.pawegio.homebudget.HomeBudgetRepository
import com.pawegio.homebudget.Navigator
import com.pawegio.homebudget.R
import com.pawegio.homebudget.util.HowToLauncher
import io.reactivex.Observable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.rx2.awaitFirst

suspend fun PickerFlow(
    actions: Observable<PickerAction>,
    howToLauncher: HowToLauncher,
    repository: HomeBudgetRepository,
    parseSpreadsheetId: (String) -> String,
    initMainFlow: suspend () -> Unit,
    navigator: Navigator
) = coroutineScope {
    loop@ while (isActive) {
        when (val action = actions.awaitFirst()) {
            PickerAction.SelectHowTo -> howToLauncher.launch()
            is PickerAction.PickDocument -> {
                repository.spreadsheetId = parseSpreadsheetId(action.url)
                navigator.navigate(R.id.action_pickerFragment_to_mainFragment)
                break@loop
            }
        }
    }
    initMainFlow()
    navigator.popBackStack()
}

sealed class PickerAction {
    object SelectHowTo : PickerAction()
    data class PickDocument(val url: String) : PickerAction()
}
