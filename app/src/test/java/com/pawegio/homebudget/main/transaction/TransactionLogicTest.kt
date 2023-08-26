package com.pawegio.homebudget.main.transaction

import androidx.lifecycle.MutableLiveData
import com.jakewharton.rxrelay2.PublishRelay
import com.jraska.livedata.test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import com.pawegio.homebudget.HomeBudgetApiException
import com.pawegio.homebudget.LogicSpec
import com.pawegio.homebudget.Navigator
import com.pawegio.homebudget.R
import com.pawegio.homebudget.util.MockHomeBudgetApi
import com.pawegio.homebudget.util.ToastNotifier
import com.pawegio.homebudget.util.createCategory
import com.pawegio.homebudget.util.createMonthlyBudget
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class TransactionLogicTest : LogicSpec({
    "On transaction logic" - {
        val categories = listOf(
            createCategory(0, "Jedzenie"),
            createCategory(1, "Transport"),
            createCategory(2, "Hobby")
        )
        val monthlyBudget = createMonthlyBudget(categories)
        val actions = PublishRelay.create<TransactionAction>()
        val state = MutableLiveData<TransactionState>()
        val api = MockHomeBudgetApi()
        val clock = Clock.fixed(Instant.parse("2020-06-09T17:23:04.00Z"), ZoneId.systemDefault())
        val toastNotifier = mock<ToastNotifier>()
        val navigator = mock<Navigator>()

        val logic = logicScope.launch {
            TransactionLogic(
                monthlyBudget,
                actions,
                state,
                api,
                clock,
                toastNotifier,
                navigator
            )
        }

        "set in fill out state" {
            state.test().assertValue(
                TransactionState.InFillOut(
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
                state.test().assertValue { it is TransactionState.InFillOut && it.selectedDate == selectedDate }
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
                    state.test().assertValue { it is TransactionState.InFillOut && it.selectedSubcategory == selectedCategory.subcategories.first() }
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

                            "show add transaction error" {
                                verify(toastNotifier).notify(R.string.transaction_added_message)
                            }

                            "pop back stack" {
                                verify(navigator).popBackStack()
                            }

                            "complete logic" {
                                logic.isCompleted shouldBe true
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
                        state.test().assertValue { it is TransactionState.InFillOut && it.selectedSubcategory == selectedSubcategory }
                    }
                }

                "on select new date" - {
                    val newSelectedDate = LocalDate.parse("2020-05-07")
                    actions.accept(TransactionAction.SelectDate(newSelectedDate))

                    "keep selected category" {
                        state.test().assertValue { it is TransactionState.InFillOut && it.selectedSubcategory == selectedCategory.subcategories.first() }
                    }
                }
            }
        }

        "on enter note" - {
            val note = "Bread"
            actions.accept(TransactionAction.EnterNote(note))

            "update entered note" {
                state.test().assertValue { it is TransactionState.InFillOut && it.enteredNote == note }
            }

            "on select value" - {
                val selectedValue = BigDecimal.valueOf(4.0)
                actions.accept(TransactionAction.SelectValue(selectedValue))

                "update selected value" {
                    state.test().assertValue { it is TransactionState.InFillOut && it.selectedValue == selectedValue }
                }

                "on select add" - {
                    actions.accept(TransactionAction.SelectAdd)

                    "set in progress state" {
                        state.test().assertValue(TransactionState.InProgress)
                    }

                    "add transaction to home budget" {
                        api.addTransactionCalled shouldBe true
                    }

                    "add transaction with entered note" {
                        api.addedTransactionNote shouldBe note
                    }
                }
            }
        }

        "on select value" - {
            val selectedValue = BigDecimal.valueOf(15.0)
            actions.accept(TransactionAction.SelectValue(selectedValue))

            "update selected value" {
                state.test().assertValue { it is TransactionState.InFillOut && it.selectedValue == selectedValue }
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
        }
    }
})
