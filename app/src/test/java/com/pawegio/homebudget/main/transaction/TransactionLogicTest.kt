package com.pawegio.homebudget.main.transaction

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
import com.pawegio.homebudget.util.createCategory
import com.pawegio.homebudget.util.createMonthlyBudget
import io.kotlintest.shouldBe
import kotlinx.coroutines.launch
import org.threeten.bp.Clock
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import java.math.BigDecimal
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class TransactionLogicTest : LogicSpec({
    "On transaction logic" - {
        val actions = PublishRelay.create<TransactionAction>()
        val state = MutableLiveData<TransactionState>()
        val categories = listOf(
            createCategory(0, "Jedzenie"),
            createCategory(1, "Transport"),
            createCategory(2, "Hobby")
        )
        val api = MockHomeBudgetApi()
        val clock = Clock.fixed(Instant.parse("2020-06-09T17:23:04.00Z"), ZoneId.systemDefault())
        val toastNotifier = mock<ToastNotifier>()
        val navigator = mock<Navigator>()

        lateinit var result: TransactionResult
        val logic = launch {
            result = TransactionLogic(
                actions,
                state,
                MutableLiveData(createMonthlyBudget(categories)),
                api,
                clock,
                toastNotifier,
                navigator
            )
        }

        "set default state" {
            state.test().assertValue(
                TransactionState(
                    enteredNote = null,
                    selectedDate = LocalDate.parse("2020-06-09"),
                    selectedCategory = categories.first(),
                    selectedSubcategory = categories.first().subcategories.first(),
                    selectedValue = null
                )
            )
        }

        "on select date" - {
            val selectedDate = LocalDate.parse("2020-06-07")
            actions.accept(TransactionAction.SelectDate(selectedDate))

            "update selected date" {
                state.test().assertValue { it.selectedDate == selectedDate }
            }

            "on select value" - {
                val selectedValue = BigDecimal.valueOf(7.0)
                actions.accept(TransactionAction.SelectValue(selectedValue))

                "on select add" - {
                    actions.accept(TransactionAction.SelectAdd)

                    "add transaction to home budget" {
                        api.addTransactionCalled shouldBe true
                    }

                    "add transaction for selected date" {
                        api.addedTransactionDate shouldBe LocalDate.parse("2020-06-07")
                    }

                    "add transaction value" {
                        api.addedTransactionValue shouldBe selectedValue
                    }
                }
            }

            "on select category" - {
                val selectedCategory = categories[2]
                actions.accept(TransactionAction.SelectCategory(selectedCategory))

                "update category" {
                    state.test().assertValue { it.selectedSubcategory == selectedCategory.subcategories.first() }
                }

                "on select value" - {
                    val selectedValue = BigDecimal.valueOf(300.0)
                    actions.accept(TransactionAction.SelectValue(selectedValue))

                    "on select add" - {
                        actions.accept(TransactionAction.SelectAdd)

                        "add transaction to home budget" {
                            api.addTransactionCalled shouldBe true
                        }

                        "add transaction for selected category" {
                            api.addedTransactionSubcategory shouldBe selectedCategory.subcategories.first()
                        }

                        "add transaction value" {
                            api.addedTransactionValue shouldBe selectedValue
                        }

                        "on transaction added" - {
                            api.addTransaction.resume(Unit)

                            "pop back stack" {
                                verify(navigator).popBackStack()
                            }

                            "complete logic" {
                                logic.isCompleted shouldBe true
                            }

                            "return success result" {
                                result shouldBe TransactionResult.SUCCESS
                            }
                        }

                        "on add transaction failure" - {
                            api.addTransaction.resumeWithException(HomeBudgetApiException())

                            "show add transaction error" {
                                verify(toastNotifier).notify(R.string.add_transaction_error_message)
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

                "on select subcategory" - {
                    val selectedSubcategory = selectedCategory.subcategories[1]
                    actions.accept(TransactionAction.SelectSubcategory(selectedSubcategory))

                    "update subcategory" {
                        state.test().assertValue { it.selectedSubcategory == selectedSubcategory }
                    }
                }

                "on select new date" - {
                    val newSelectedDate = LocalDate.parse("2020-05-07")
                    actions.accept(TransactionAction.SelectDate(newSelectedDate))

                    "keep selected category" {
                        state.test().assertValue { it.selectedSubcategory == selectedCategory.subcategories.first() }
                    }
                }
            }
        }

        "on enter note" - {
            val note = "Bread"
            actions.accept(TransactionAction.EnterNote(note))

            "update entered note" {
                state.test().assertValue { it.enteredNote == note }
            }
        }

        "on select value" - {
            val selectedValue = BigDecimal.valueOf(15.0)
            actions.accept(TransactionAction.SelectValue(selectedValue))

            "update selected value" {
                state.test().assertValue { it.selectedValue == selectedValue }
            }

            "on select add" - {
                actions.accept(TransactionAction.SelectAdd)

                "add transaction to home budget" {
                    api.addTransactionCalled shouldBe true
                }

                "add transaction for current date" {
                    api.addedTransactionDate shouldBe LocalDate.parse("2020-06-09")
                }

                "add transaction for first category and first subcategory" {
                    api.addedTransactionSubcategory shouldBe categories.first().subcategories.first()
                }

                "add transaction value" {
                    api.addedTransactionValue shouldBe selectedValue
                }
            }
        }

        "on select back" - {
            actions.accept(TransactionAction.SelectBack)

            "pop back stack" {
                verify(navigator).popBackStack()
            }

            "complete logic" {
                logic.isCompleted shouldBe true
            }

            "return canceled result" {
                result shouldBe TransactionResult.CANCELED
            }
        }
    }
})
