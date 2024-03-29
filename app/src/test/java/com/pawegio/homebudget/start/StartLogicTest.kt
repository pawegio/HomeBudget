package com.pawegio.homebudget.start

import org.mockito.kotlin.*
import com.pawegio.homebudget.HomeBudgetRepository
import com.pawegio.homebudget.LogicSpec
import com.pawegio.homebudget.NavGraph
import com.pawegio.homebudget.Navigator
import com.pawegio.homebudget.util.MockHomeBudgetApi
import com.pawegio.homebudget.util.SuspendFunction
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

internal class StartLogicTest : LogicSpec({
    "On start logic" - {
        val repository = mock<HomeBudgetRepository>()
        val api = MockHomeBudgetApi()
        val navigator = mock<Navigator>()

        val logic = logicScope.launch(start = CoroutineStart.LAZY) {
            StartLogic(
                repository,
                api,
                navigator,
            )
        }

        "on user signed in" - {
            api.isSignInResult = true

            "on spreadsheet not picked" - {
                whenever(repository.spreadsheetId) doReturn null
                logic.start()

                "pop back stack" {
                    verify(navigator).popBackStack()
                }

                "navigate to picker screen" {
                    verify(navigator).navigate(NavGraph.Action.toPicker)
                }
            }

            "on spreadsheet picked" - {
                whenever(repository.spreadsheetId) doReturn "id"
                logic.start()

                "pop back stack" {
                    verify(navigator).popBackStack()
                }

                "navigate to main screen" {
                    verify(navigator).navigate(NavGraph.Action.toMain)
                }
            }
        }

        "on user not signed in" - {
            api.isSignInResult = false
            logic.start()

            "pop back stack" {
                verify(navigator).popBackStack()
            }

            "navigate to login screen" {
                verify(navigator).navigate(NavGraph.Action.toLogin)
            }
        }
    }
})
