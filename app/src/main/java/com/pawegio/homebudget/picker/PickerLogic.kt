@file:Suppress("FunctionName")

package com.pawegio.homebudget.picker

import com.pawegio.homebudget.HomeBudgetRepository
import com.pawegio.homebudget.Navigator
import com.pawegio.homebudget.R
import com.pawegio.homebudget.picker.PickerAction.*
import com.pawegio.homebudget.util.HowToLauncher
import io.reactivex.Observable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.rx2.awaitFirst

suspend fun PickerLogic(
    actions: Observable<PickerAction>,
    howToLauncher: HowToLauncher,
    repository: HomeBudgetRepository,
    parseSpreadsheetId: (String) -> String,
    initMain: suspend () -> Unit,
    navigator: Navigator
) = coroutineScope {
    loop@ while (isActive) {
        when (val action = actions.awaitFirst()) {
            SelectHowTo -> howToLauncher.launch()
            is SelectTemplate -> repository.spreadsheetTemplate = action.template
            is PickDocument -> {
                repository.spreadsheetId = parseSpreadsheetId(action.url)
                navigator.navigate(R.id.action_pickerFragment_to_mainFragment)
                initMain()
                break@loop
            }
            SelectBack -> break@loop
        }
    }
    navigator.popBackStack()
}

sealed class PickerAction {
    object SelectHowTo : PickerAction()
    data class SelectTemplate(val template: Int) : PickerAction()
    data class PickDocument(val url: String) : PickerAction()
    object SelectBack : PickerAction()
}
