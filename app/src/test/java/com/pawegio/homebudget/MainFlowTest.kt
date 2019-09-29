package com.pawegio.homebudget

import androidx.lifecycle.MutableLiveData
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.CoroutineStart
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
        val state = MutableLiveData<AppState>()
        val monthlyBudget = MutableLiveData<MonthlyBudget>()
        val loadedMonthlyBudget = createMonthlyBudget()
        val api = mock<HomeBudgetApi> {
            onBlocking { getMonthlyBudget(any()) } doReturn loadedMonthlyBudget
        }
        val clock = Clock.fixed(Instant.parse("2019-04-01T10:15:00.00Z"), ZoneId.systemDefault())
        val navigator = mock<Navigator>()

        val flow = launch(start = CoroutineStart.LAZY) {
            @Suppress("EXPERIMENTAL_API_USAGE")
            MainFlow(
                actions.consumeAsFlow(),
                state,
                monthlyBudget,
                api,
                clock,
                navigator
            )
        }

        "on user not signed in" - {
            whenever(api.isSignedIn) doReturn false
            flow.start()

            "set state to unauthorized" {
                state.test().assertValue(AppState.Unauthorized)
            }

            "do not sign in" {
                verifyBlocking(api, never()) { signIn() }
            }

            "on select sign in" - {
                actions.offer(MainAction.SelectSignIn)

                "sign in" {
                    verifyBlocking(api) { signIn() }
                }
            }
        }

        "on user signed in" - {
            whenever(api.isSignedIn) doReturn true
            flow.start()

            "set state to authorized" {
                state.test().assertValue(AppState.Authorized)
            }

            "navigate to main screen" {
                verify(navigator).navigate(R.id.action_loginFragment_to_mainFragment)
            }

            "get monthly budget for current month" {
                verifyBlocking(api) { getMonthlyBudget(Month.APRIL) }
            }

            "on monthly budget loaded with success" - {

                "update monthly budget" {
                    monthlyBudget.test().assertValue(loadedMonthlyBudget)
                }
            }
        }
    }
})
