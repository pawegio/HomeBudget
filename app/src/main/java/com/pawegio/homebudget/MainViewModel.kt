package com.pawegio.homebudget

import androidx.lifecycle.*
import com.pawegio.homebudget.login.LoginAction
import com.pawegio.homebudget.login.LoginFlow
import com.pawegio.homebudget.main.MainAction
import com.pawegio.homebudget.main.MainFlow
import com.pawegio.homebudget.main.MonthType
import com.pawegio.homebudget.picker.PickerAction
import com.pawegio.homebudget.picker.PickerFlow
import com.pawegio.homebudget.util.SpreadsheetLauncher
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import org.threeten.bp.Clock

@ExperimentalCoroutinesApi
@FlowPreview
class MainViewModel(
    private val repository: HomeBudgetRepository,
    private val api: HomeBudgetApi,
    private val spreadsheetLauncher: SpreadsheetLauncher,
    private val clock: Clock,
    private val navigator: Navigator
) : ViewModel(), LifecycleObserver, CoroutineScope by MainScope() {

    val monthlyBudget: LiveData<MonthlyBudget> get() = _monthlyBudget
    val monthType: LiveData<MonthType> get() = _monthType
    val isLoading: LiveData<Boolean> get() = _isLoading

    val loginActions = Channel<LoginAction>()
    val pickerActions = Channel<PickerAction>()
    val mainActions = Channel<MainAction>()

    private val _monthlyBudget = MutableLiveData<MonthlyBudget>()
    private val _monthType = MutableLiveData<MonthType>()
    private val _isLoading = MutableLiveData<Boolean>()

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(source: LifecycleOwner) {
        if (source is MainActivity) {
            navigator.restart(R.navigation.app_navigation)
            launch { initLoginFlow() }
        }
    }

    private suspend fun initLoginFlow() {
        LoginFlow(
            loginActions.consumeAsFlow(),
            repository,
            api,
            ::initPickerFlow,
            ::initMainFlow,
            navigator
        )
    }

    private suspend fun initPickerFlow() {
        PickerFlow(
            pickerActions.consumeAsFlow(),
            ::initMainFlow,
            navigator
        )
    }

    private suspend fun initMainFlow() {
        MainFlow(
            mainActions.consumeAsFlow(),
            _monthType,
            _monthlyBudget,
            _isLoading,
            api,
            spreadsheetLauncher,
            clock
        )
    }
}
