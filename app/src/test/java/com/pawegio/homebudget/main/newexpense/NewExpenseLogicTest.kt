package com.pawegio.homebudget.main.newexpense

import androidx.lifecycle.MutableLiveData
import com.jakewharton.rxrelay2.PublishRelay
import com.jraska.livedata.test
import com.pawegio.homebudget.LogicSpec
import com.pawegio.homebudget.util.MockHomeBudgetApi
import io.kotlintest.shouldBe
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

internal class NewExpenseLogicTest : LogicSpec({
    "On new expense logic" - {
        val actions = PublishRelay.create<NewExpenseAction>()
        val state = MutableLiveData<NewExpenseState>()
        val api = MockHomeBudgetApi()

        launch {
            NewExpenseLogic(actions, state, api)
        }

        "on select date" - {
            val selectedDate = LocalDate.of(2020, 5, 8)
            actions.accept(NewExpenseAction.SelectDate(selectedDate))

            "update selected date" {
                state.test().assertValue { it.selectedDate == selectedDate }
            }
        }

        "on select add" - {
            actions.accept(NewExpenseAction.SelectAdd)

            "add expense to home budget" {
                api.addExpense.invocations shouldBe 1
            }
        }
    }
})
