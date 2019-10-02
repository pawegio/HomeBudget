package com.pawegio.homebudget.login

import com.nhaarman.mockitokotlin2.*
import com.pawegio.homebudget.*
import com.pawegio.homebudget.util.SuspendFunction
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

internal class LoginFlowTest : FlowSpec({
    "On login flow" - {
        val actions = Channel<LoginAction>()
        val api = mock<HomeBudgetApi>()
        val initMainFlow = mock<SuspendFunction<Unit>>()
        val navigator = mock<Navigator>()

        val flow = launch(start = CoroutineStart.LAZY) {
            @Suppress("EXPERIMENTAL_API_USAGE")
            LoginFlow(
                actions.consumeAsFlow(),
                api,
                initMainFlow::invokeSuspend,
                navigator
            )
        }

        "on user not signed in" - {
            whenever(api.isSignedIn) doReturn false
            flow.start()

            "do not sign in" {
                verifyBlocking(api, never()) { signIn() }
            }

            "on select sign in" - {
                actions.offer(LoginAction.SelectSignIn)

                "sign in" {
                    verifyBlocking(api) { signIn() }
                }
            }
        }

        "on user signed in" - {
            whenever(api.isSignedIn) doReturn true
            flow.start()

            "navigate to main screen" {
                verify(navigator).navigate(R.id.action_loginFragment_to_mainFragment)
            }
        }
    }
})
