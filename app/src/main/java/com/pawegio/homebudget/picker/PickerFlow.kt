@file:Suppress("FunctionName")

package com.pawegio.homebudget.picker

import com.pawegio.homebudget.HomeBudgetRepository
import com.pawegio.homebudget.Navigator
import com.pawegio.homebudget.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first

suspend fun PickerFlow(
    actions: Flow<PickerAction>,
    repository: HomeBudgetRepository,
    initMainFlow: suspend () -> Unit,
    navigator: Navigator
) {
    repository.spreadsheetId = actions
        .filterIsInstance<PickerAction.PickDocument>()
        .first()
        .spreadsheetId
    navigator.navigate(R.id.action_pickerFragment_to_mainFragment)
    initMainFlow()
}

sealed class PickerAction {
    data class PickDocument(val spreadsheetId: String) : PickerAction()
}
