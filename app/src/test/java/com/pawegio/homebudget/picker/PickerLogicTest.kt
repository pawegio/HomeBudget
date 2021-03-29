package com.pawegio.homebudget.picker

import com.jakewharton.rxrelay2.PublishRelay
import com.nhaarman.mockitokotlin2.*
import com.pawegio.homebudget.*
import com.pawegio.homebudget.util.HowToLauncher
import com.pawegio.homebudget.util.SuspendFunction
import io.kotlintest.shouldBe
import kotlinx.coroutines.launch

internal class PickerLogicTest : LogicSpec({
    "On picker logic" - {
        val actions = PublishRelay.create<PickerAction>()
        val howToLauncher = mock<HowToLauncher>()
        val repository = mock<HomeBudgetRepository>()
        val parseSpreadsheetId = mock<(String) -> String>()
        val navigator = mock<Navigator>()

        val logic = launch {
            @Suppress("EXPERIMENTAL_API_USAGE")
            PickerLogic(
                actions,
                howToLauncher,
                repository,
                parseSpreadsheetId,
                navigator
            )
        }

        "on select how to" - {
            actions.accept(PickerAction.SelectHowTo)

            "launch how to" {
                verify(howToLauncher).launch()
            }

            "do not navigate to main screen" {
                verify(navigator, never()).navigate(NavGraph.Action.toMain)
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
                verify(navigator).navigate(NavGraph.Action.toMain)
            }
        }

        "on select back" - {
            actions.accept(PickerAction.SelectBack)

            "pop back stack" {
                verify(navigator).popBackStack()
            }

            "complete logic" {
                logic.isCompleted shouldBe true
            }
        }
    }
})
