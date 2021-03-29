package com.pawegio.homebudget.main

import androidx.lifecycle.MutableLiveData
import com.jakewharton.rxrelay2.PublishRelay
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyBlocking
import com.pawegio.homebudget.*
import com.pawegio.homebudget.util.*
import io.kotlintest.shouldBe
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import org.threeten.bp.Clock
import org.threeten.bp.Instant
import org.threeten.bp.Month
import org.threeten.bp.ZoneId
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class MainLogicTest : LogicSpec({
    "On main logic" - {
        val actions = PublishRelay.create<MainAction>()
        val monthlyBudget = MutableLiveData<MonthlyBudget>()
        val monthType = MutableLiveData<MonthType>()
        val isLoading = MutableLiveData<Boolean>()
        val repository = mock<HomeBudgetRepository>()
        val api = MockHomeBudgetApi()
        val spreadsheetLauncher = mock<SpreadsheetLauncher>()
        var clock = Clock.fixed(Instant.parse("2019-04-01T10:15:00.00Z"), ZoneId.systemDefault())
        val navigator = mock<Navigator>()

        val logic = launch(start = CoroutineStart.LAZY) {
            MainLogic(
                actions,
                monthType,
                monthlyBudget,
                isLoading,
                repository,
                api,
                spreadsheetLauncher,
                clock,
                navigator
            )
        }

        "on January" - {
            clock = Clock.fixed(Instant.parse("2019-01-01T10:15:00.00Z"), ZoneId.systemDefault())
            logic.start()

            "is first month" {
                monthType.test().assertValue(MonthType.FIRST)
            }
        }

        "on December" - {
            clock = Clock.fixed(Instant.parse("2019-12-01T10:15:00.00Z"), ZoneId.systemDefault())
            logic.start()

            "is last month" {
                monthType.test().assertValue(MonthType.LAST)
            }
        }

        logic.start()

        "is middle month" {
            monthType.test().assertValue(MonthType.MIDDLE)
        }

        "get monthly budget for current month" {
            api.getMonthlyBudget.invocations shouldBe listOf(Month.APRIL)
        }

        "show loader" {
            isLoading.test().assertValue(true)
        }

        "on monthly budget loaded with success" - {
            val loadedMonthlyBudget = createMonthlyBudget()
            api.getMonthlyBudget.resume(loadedMonthlyBudget)

            "hide loader" {
                isLoading.test().assertValue(false)
            }

            "update monthly budget" {
                monthlyBudget.test().assertValue(loadedMonthlyBudget)
            }

            "on refresh" - {
                actions.accept(MainAction.Refresh)

                "get refreshed monthly budget for current month" {
                    api.getMonthlyBudget.invocations.run {
                        count() shouldBe 2
                        last() shouldBe Month.APRIL
                    }
                }
            }

            "on open spreadsheet" - {
                actions.accept(MainAction.OpenSpreadsheet)

                "launch spreadsheet" {
                    verify(spreadsheetLauncher).launch()
                }
            }

            "on select prev month" - {
                actions.accept(MainAction.SelectPrevMonth)

                "get monthly budget for previous month" {
                    api.getMonthlyBudget.invocations.last() shouldBe Month.MARCH
                }
            }

            "on select next month" - {
                actions.accept(MainAction.SelectNextMonth)

                "get monthly budget for next month" {
                    api.getMonthlyBudget.invocations.last() shouldBe Month.MAY
                }
            }

            "on add transaction" - {
                actions.accept(MainAction.AddTransaction)

                "navigate to transaction screen" {
                    verify(navigator).navigate(
                        NavGraph.Action.toTransaction,
                        NavGraph.Args.monthlyBudget to loadedMonthlyBudget
                    )
                }

                "on resume" - {
                    actions.accept(MainAction.Resume)

                    "get refreshed monthly budget for current month" {
                        api.getMonthlyBudget.invocations.run {
                            count() shouldBe 2
                            last() shouldBe Month.APRIL
                        }
                    }
                }
            }

            "on sign out" - {
                actions.accept(MainAction.SignOut)

                "clear spreadsheet id" {
                    verify(repository).spreadsheetId = null
                }

                "sign out" {
                    api.signOut.invocations shouldBe 1
                }

                "on sign out success" - {
                    api.signOut.resume(Unit)

                    "pop back stack" {
                        verify(navigator).popBackStack()
                    }
                }
            }

            "on select about" - {
                actions.accept(MainAction.SelectAbout)

                "navigate to about screen" {
                    verify(navigator).navigate(NavGraph.Action.toAbout)
                }
            }
        }

        "on monthly budget load error" - {
            api.getMonthlyBudget.resumeWithException(HomeBudgetApiException())

            "hide loader" {
                isLoading.test().assertValue(false)
            }

            "clear monthly budget" {
                monthlyBudget.test().assertNullValue()
            }

            "show error dialog" {
                verify(navigator).navigate(NavGraph.Action.toLoadError)
            }

            "on try again" - {
                actions.accept(MainAction.TryAgain)

                "get refreshed monthly budget for current month" {
                    api.getMonthlyBudget.invocations.run {
                        count() shouldBe 2
                        last() shouldBe Month.APRIL
                    }
                }
            }

            "on pick document again" - {
                actions.accept(MainAction.PickDocumentAgain)

                "navigate to picker screen" {
                    verify(navigator).navigate(NavGraph.Action.toPicker)
                }
            }
        }
    }
})
