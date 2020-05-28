package com.pawegio.homebudget.main.newexpense

import com.jakewharton.rxrelay2.PublishRelay
import com.pawegio.homebudget.LogicSpec
import com.pawegio.homebudget.util.MockHomeBudgetApi
import io.kotlintest.shouldBe
import kotlinx.coroutines.launch

internal class NewExpenseLogicTest : LogicSpec({
    "On new expense logic" - {
        val actions = PublishRelay.create<NewExpenseAction>()
        val api = MockHomeBudgetApi()

        launch {
            NewExpenseLogic(actions, api)
        }

        "on select add" - {
            actions.accept(NewExpenseAction.SelectAdd)

            "add expense to home budget" {
                api.addExpense.invocations shouldBe 1
            }
        }
    }
})
