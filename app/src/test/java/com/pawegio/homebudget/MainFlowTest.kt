package com.pawegio.homebudget

import androidx.lifecycle.MutableLiveData
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

@FlowPreview
internal class MainFlowTest : FlowSpec({
    "On main flow" - {
        val actions = Channel<MainAction>()
        val state = MutableLiveData<AppState>()
        val sheetsService = mock<GoogleSheetsService>()

        val flow = launch(start = CoroutineStart.LAZY) {
            MainFlow(
                actions.consumeAsFlow(),
                state,
                sheetsService
            )
        }

        "on user not signed in" - {
            whenever(sheetsService.isSignedIn) doReturn false
            flow.start()

            "set state to unauthorized" {
                state.test().assertValue(AppState.Unauthorized)
            }

            "do not sign in" {
                verifyBlocking(sheetsService, never()) { signIn() }
            }

            "on select sign in" - {
                actions.offer(MainAction.SelectSignIn)

                "sign in" {
                    verifyBlocking(sheetsService) { signIn() }
                }
            }
        }

        "on user signed in" - {
            whenever(sheetsService.isSignedIn) doReturn true
            flow.start()

            "set state to authorized" {
                state.test().assertValue(AppState.Authorized)
            }
        }
    }
})
