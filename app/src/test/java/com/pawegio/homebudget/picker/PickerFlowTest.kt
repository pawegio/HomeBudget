package com.pawegio.homebudget.picker

import com.nhaarman.mockitokotlin2.*
import com.pawegio.homebudget.FlowSpec
import com.pawegio.homebudget.HomeBudgetRepository
import com.pawegio.homebudget.Navigator
import com.pawegio.homebudget.R
import com.pawegio.homebudget.util.SuspendFunction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

internal class PickerFlowTest : FlowSpec({
    "On picker flow" - {
        val actions = Channel<PickerAction>()
        val repository = mock<HomeBudgetRepository>()
        val parseSpreadsheetId = mock<(String) -> String>()
        val initMainFlow = mock<SuspendFunction<Unit>>()
        val navigator = mock<Navigator>()

        launch {
            @Suppress("EXPERIMENTAL_API_USAGE")
            PickerFlow(
                actions.consumeAsFlow(),
                repository,
                parseSpreadsheetId,
                initMainFlow::invokeSuspend,
                navigator
            )
        }

        "on pick document" - {
            val url = "https://dummySpreadsheetUrl"
            val spreadsheetId = "dummySpreadsheetId"
            whenever(parseSpreadsheetId.invoke(any())) doReturn spreadsheetId
            actions.offer(PickerAction.PickDocument(url))

            "parse spreadsheet id" {
                verify(parseSpreadsheetId).invoke(url)
            }

            "save picked spreadsheet id in repository" {
                verify(repository).spreadsheetId = spreadsheetId
            }

            "navigate to main screen" {
                verify(navigator).navigate(R.id.action_pickerFragment_to_mainFragment)
            }

            "init main flow" {
                verifyBlocking(initMainFlow) { invokeSuspend() }
            }
        }
    }
})
