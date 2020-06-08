package com.pawegio.homebudget.main.newexpense

import androidx.lifecycle.MutableLiveData
import com.jakewharton.rxrelay2.PublishRelay
import com.jraska.livedata.test
import com.pawegio.homebudget.LogicSpec
import com.pawegio.homebudget.util.MockHomeBudgetApi
import io.kotlintest.shouldBe
import kotlinx.coroutines.launch
import org.threeten.bp.Clock
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

internal class NewExpenseLogicTest : LogicSpec({
    "On new expense logic" - {
        val actions = PublishRelay.create<NewExpenseAction>()
        val state = MutableLiveData<NewExpenseState>()
        val api = MockHomeBudgetApi()
        val clock = Clock.fixed(Instant.parse("2020-06-09T17:23:04.00Z"), ZoneId.systemDefault())

        launch {
            NewExpenseLogic(
                actions,
                state,
                api,
                clock
            )
        }

        "on select date" - {
            val selectedDate = LocalDate.parse("2020-06-07")
            actions.accept(NewExpenseAction.SelectDate(selectedDate))

            "update selected date" {
                state.test().assertValue { it.selectedDate == selectedDate }
            }

            "on select add" - {
                actions.accept(NewExpenseAction.SelectAdd)

                "add expense to home budget" {
                    api.addExpenseCalled shouldBe true
                }

                "add expense for selected date" {
                    api.addedExpenseDate shouldBe LocalDate.parse("2020-06-07")
                }
            }

            "on select category" - {
                val selectedCategory = "Hobby"
                actions.accept(NewExpenseAction.SelectCategory(selectedCategory))

                "update category" {
                    state.test().assertValue { it.selectedCategory == selectedCategory }
                }
            }
        }

        "on select add" - {
            actions.accept(NewExpenseAction.SelectAdd)

            "add expense to home budget" {
                api.addExpenseCalled shouldBe true
            }

            "add expense for current date" {
                api.addedExpenseDate shouldBe LocalDate.parse("2020-06-09")
            }
        }
    }
})
