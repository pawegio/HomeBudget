@file:Suppress("FunctionName")

package com.pawegio.homebudget.picker

import com.pawegio.homebudget.HomeBudgetRepository
import com.pawegio.homebudget.Navigator
import com.pawegio.homebudget.R
import io.reactivex.Observable
import io.reactivex.rxkotlin.ofType
import kotlinx.coroutines.rx2.awaitFirst

suspend fun PickerFlow(
    actions: Observable<PickerAction>,
    repository: HomeBudgetRepository,
    parseSpreadsheetId: (String) -> String,
    initMainFlow: suspend () -> Unit,
    navigator: Navigator
) {
    val url = actions.ofType<PickerAction.PickDocument>().awaitFirst().url
    repository.spreadsheetId = parseSpreadsheetId(url)
    navigator.navigate(R.id.action_pickerFragment_to_mainFragment)
    initMainFlow()
    navigator.popBackStack()
}

sealed class PickerAction {
    data class PickDocument(val url: String) : PickerAction()
}
