package com.pawegio.homebudget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import org.threeten.bp.Clock

class MainViewModel(
    api: HomeBudgetApi,
    clock: Clock
) : ViewModel(), CoroutineScope by MainScope() {

    val appState: LiveData<AppState> get() = _appState
    val monthlyBudget: LiveData<MonthlyBudget> get() = _monthlyBudget

    val mainActions = Channel<MainAction>()

    private val _appState = MutableLiveData<AppState>()
    private val _monthlyBudget = MutableLiveData<MonthlyBudget>()

    init {
        launch {
            @Suppress("EXPERIMENTAL_API_USAGE")
            MainFlow(
                mainActions.consumeAsFlow(),
                _appState,
                _monthlyBudget,
                api,
                clock
            )
        }
    }
}
