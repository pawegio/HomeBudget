package com.pawegio.homebudget.main.newexpense

import androidx.lifecycle.MutableLiveData
import com.jakewharton.rxrelay2.PublishRelay
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.pawegio.homebudget.HomeBudgetApiException
import com.pawegio.homebudget.LogicSpec
import com.pawegio.homebudget.Navigator
import com.pawegio.homebudget.R
import com.pawegio.homebudget.util.MockHomeBudgetApi
import com.pawegio.homebudget.util.ToastNotifier
import io.kotlintest.shouldBe
import kotlinx.coroutines.launch
import org.threeten.bp.Clock
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import java.math.BigDecimal
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class NewExpenseLogicTest : LogicSpec({
    "On new expense logic" - {
        val actions = PublishRelay.create<NewExpenseAction>()
        val state = MutableLiveData<NewExpenseState>()
        val categories = MutableLiveData(listOf("Jedzenie", "Transport", "Hobby"))
        val api = MockHomeBudgetApi()
        val clock = Clock.fixed(Instant.parse("2020-06-09T17:23:04.00Z"), ZoneId.systemDefault())
        val toastNotifier = mock<ToastNotifier>()
        val navigator = mock<Navigator>()

        val logic = launch {
            NewExpenseLogic(
                actions,
                state,
                categories,
                api,
                clock,
                toastNotifier,
                navigator
            )
        }

        "set default state" {
            state.test().assertValue(
                NewExpenseState(
                    selectedDate = LocalDate.parse("2020-06-09"),
                    selectedCategory = "Jedzenie",
                    selectedValue = null
                )
            )
        }

        "on select date" - {
            val selectedDate = LocalDate.parse("2020-06-07")
            actions.accept(NewExpenseAction.SelectDate(selectedDate))

            "update selected date" {
                state.test().assertValue { it.selectedDate == selectedDate }
            }

            "on select value" - {
                val selectedValue = BigDecimal.valueOf(7.0)
                actions.accept(NewExpenseAction.SelectValue(selectedValue))

                "on select add" - {
                    actions.accept(NewExpenseAction.SelectAdd)

                    "add expense to home budget" {
                        api.addExpenseCalled shouldBe true
                    }

                    "add expense for selected date" {
                        api.addedExpenseDate shouldBe LocalDate.parse("2020-06-07")
                    }

                    "add expense value" {
                        api.addedExpenseValue shouldBe selectedValue
                    }
                }
            }

            "on select category" - {
                val selectedCategory = "Hobby"
                actions.accept(NewExpenseAction.SelectCategory(selectedCategory))

                "update category" {
                    state.test().assertValue { it.selectedCategory == selectedCategory }
                }

                "on select value" - {
                    val selectedValue = BigDecimal.valueOf(300.0)
                    actions.accept(NewExpenseAction.SelectValue(selectedValue))

                    "on select add" - {
                        actions.accept(NewExpenseAction.SelectAdd)

                        "add expense to home budget" {
                            api.addExpenseCalled shouldBe true
                        }

                        "add expense for selected category" {
                            api.addedExpenseCategory shouldBe selectedCategory
                        }

                        "add expense value" {
                            api.addedExpenseValue shouldBe selectedValue
                        }

                        "on expense added" - {
                            api.addExpense.resume(Unit)

                            "pop back stack" {
                                verify(navigator).popBackStack()
                            }

                            "complete logic" {
                                logic.isCompleted shouldBe true
                            }
                        }

                        "on add expense failure" - {
                            api.addExpense.resumeWithException(HomeBudgetApiException())

                            "show add expense error" {
                                verify(toastNotifier).notify(R.string.add_expense_error_message)
                            }

                            "do not pop back stack" {
                                verify(navigator, never()).popBackStack()
                            }

                            "do not complete logic" {
                                logic.isCompleted shouldBe false
                            }
                        }
                    }
                }

                "on select new date" - {
                    val newSelectedDate = LocalDate.parse("2020-05-07")
                    actions.accept(NewExpenseAction.SelectDate(newSelectedDate))

                    "keep selected category" {
                        state.test().assertValue { it.selectedCategory == selectedCategory }
                    }
                }
            }
        }

        "on select value" - {
            val selectedValue = BigDecimal.valueOf(15.0)
            actions.accept(NewExpenseAction.SelectValue(selectedValue))

            "update selected value" {
                state.test().assertValue { it.selectedValue == selectedValue }
            }

            "on select add" - {
                actions.accept(NewExpenseAction.SelectAdd)

                "add expense to home budget" {
                    api.addExpenseCalled shouldBe true
                }

                "add expense for current date" {
                    api.addedExpenseDate shouldBe LocalDate.parse("2020-06-09")
                }

                "add expense for first category" {
                    api.addedExpenseCategory shouldBe categories.value?.first()
                }

                "add expense value" {
                    api.addedExpenseValue shouldBe selectedValue
                }
            }
        }

        "on select back" - {
            actions.accept(NewExpenseAction.SelectBack)

            "pop back stack" {
                verify(navigator).popBackStack()
            }

            "complete logic" {
                logic.isCompleted shouldBe true
            }
        }
    }
})
