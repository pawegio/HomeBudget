package com.pawegio.homebudget.main

import androidx.lifecycle.MutableLiveData
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import com.pawegio.homebudget.FlowSpec
import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.MonthlyBudget
import com.pawegio.homebudget.util.SpreadsheetLauncher
import com.pawegio.homebudget.util.createMonthlyBudget
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import org.threeten.bp.Clock
import org.threeten.bp.Instant
import org.threeten.bp.Month
import org.threeten.bp.ZoneId

internal class MainFlowTest : FlowSpec({
    "On main flow" - {
        val actions = Channel<MainAction>()
        val monthlyBudget = MutableLiveData<MonthlyBudget>()
        val loadedMonthlyBudget = createMonthlyBudget()
        val api = mock<HomeBudgetApi> {
            onBlocking { getMonthlyBudget(any()) } doReturn loadedMonthlyBudget
        }
        val spreadsheetLauncher = mock<SpreadsheetLauncher>()
        val clock = Clock.fixed(Instant.parse("2019-04-01T10:15:00.00Z"), ZoneId.systemDefault())

        launch {
            @Suppress("EXPERIMENTAL_API_USAGE")
            MainFlow(
                actions.consumeAsFlow(),
                monthlyBudget,
                api,
                spreadsheetLauncher,
                clock
            )
        }

        "get monthly budget for current month" {
            verifyBlocking(api) { getMonthlyBudget(Month.APRIL) }
        }

        "on monthly budget loaded with success" - {

            "update monthly budget" {
                monthlyBudget.test().assertValue(loadedMonthlyBudget)
            }
        }

        "on open spreadsheet" - {
            actions.offer(MainAction.OpenSpreadsheet)

            "launch spreadsheet" {
                verify(spreadsheetLauncher).launch()
            }
        }
    }
})
