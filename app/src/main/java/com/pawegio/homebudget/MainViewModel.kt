package com.pawegio.homebudget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pawegio.homebudget.login.LoginAction
import com.pawegio.homebudget.login.LoginFlow
import com.pawegio.homebudget.main.MainAction
import com.pawegio.homebudget.main.MainFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import org.threeten.bp.Clock

@FlowPreview
class MainViewModel(
    private val api: HomeBudgetApi,
    private val clock: Clock,
    private val navigator: Navigator
) : ViewModel(), CoroutineScope by MainScope() {

    val monthlyBudget: LiveData<MonthlyBudget> get() = _monthlyBudget

    val loginActions = Channel<LoginAction>()
    val mainActions = Channel<MainAction>()

    private val _monthlyBudget = MutableLiveData<MonthlyBudget>()

    init {
        launch { initLoginFlow() }
    }

    private suspend fun initLoginFlow() {
        LoginFlow(
            loginActions.consumeAsFlow(),
            api,
            ::initMainFlow,
            navigator
        )
    }

    private suspend fun initMainFlow() {
        MainFlow(
            mainActions.consumeAsFlow(),
            _monthlyBudget,
            api,
            clock
        )
    }
}
