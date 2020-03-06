package com.pawegio.homebudget.picker

import com.jakewharton.rxrelay2.PublishRelay
import com.nhaarman.mockitokotlin2.*
import com.pawegio.homebudget.FlowSpec
import com.pawegio.homebudget.HomeBudgetRepository
import com.pawegio.homebudget.Navigator
import com.pawegio.homebudget.R
import com.pawegio.homebudget.util.HowToLauncher
import com.pawegio.homebudget.util.SuspendFunction
import kotlinx.coroutines.launch

internal class PickerLogicTest : FlowSpec({
    "On picker logic" - {
        val actions = PublishRelay.create<PickerAction>()
        val howToLauncher = mock<HowToLauncher>()
        val repository = mock<HomeBudgetRepository>()
        val parseSpreadsheetId = mock<(String) -> String>()
        val initMain = mock<SuspendFunction<Unit>>()
        val navigator = mock<Navigator>()

        launch {
            @Suppress("EXPERIMENTAL_API_USAGE")
            PickerLogic(
                actions,
                howToLauncher,
                repository,
                parseSpreadsheetId,
                initMain::invokeSuspend,
                navigator
            )
        }

        "on select how to" - {
            actions.accept(PickerAction.SelectHowTo)

            "launch how to" {
                verify(howToLauncher).launch()
            }

            "do not navigate to main screen" {
                verify(navigator, never()).navigate(R.id.action_pickerFragment_to_mainFragment)
            }

            "do not init main flow" {
                verifyBlocking(initMain, never()) { invokeSuspend() }
            }
        }

        "on select template" - {
            val template = 2020
            actions.accept(PickerAction.SelectTemplate(template))

            "save selected template in repository" {
                verify(repository).spreadsheetTemplate = template
            }
        }

        "on pick document" - {
            val url = "https://dummySpreadsheetUrl"
            val spreadsheetId = "dummySpreadsheetId"
            whenever(parseSpreadsheetId.invoke(any())) doReturn spreadsheetId
            actions.accept(PickerAction.PickDocument(url))

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
                verifyBlocking(initMain) { invokeSuspend() }
            }
        }
    }
})
