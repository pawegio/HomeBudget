package com.pawegio.homebudget

import androidx.lifecycle.*
import com.jakewharton.rxrelay2.PublishRelay
import com.pawegio.homebudget.login.LoginAction
import com.pawegio.homebudget.login.LoginFlow
import com.pawegio.homebudget.main.MainAction
import com.pawegio.homebudget.main.MainFlow
import com.pawegio.homebudget.main.MonthType
import com.pawegio.homebudget.picker.PickerAction
import com.pawegio.homebudget.picker.PickerFlow
import com.pawegio.homebudget.picker.parseSpreadsheetId
import com.pawegio.homebudget.util.SpreadsheetLauncher
import com.pawegio.homebudget.util.ToastNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.threeten.bp.Clock

class MainViewModel(
    private val repository: HomeBudgetRepository,
    private val api: HomeBudgetApi,
    private val spreadsheetLauncher: SpreadsheetLauncher,
    private val clock: Clock,
    private val navigator: Navigator,
    private val toastNotifier: ToastNotifier
) : ViewModel(), LifecycleObserver, CoroutineScope by MainScope() {

    val monthlyBudget: LiveData<MonthlyBudget> get() = _monthlyBudget
    val monthType: LiveData<MonthType> get() = _monthType
    val isLoading: LiveData<Boolean> get() = _isLoading

    val loginActions = PublishRelay.create<LoginAction>()
    val pickerActions = PublishRelay.create<PickerAction>()
    val mainActions = PublishRelay.create<MainAction>()

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
            loginActions,
            repository,
            api,
            toastNotifier,
            ::initPickerFlow,
            ::initMainFlow,
            navigator
        )
    }

    private suspend fun initPickerFlow() {
        PickerFlow(
            pickerActions,
            repository,
            ::parseSpreadsheetId,
            ::initMainFlow,
            navigator
        )
    }

    private suspend fun initMainFlow() {
        MainFlow(
            mainActions,
            _monthType,
            _monthlyBudget,
            _isLoading,
            repository,
            api,
            spreadsheetLauncher,
            clock,
            ::initPickerFlow,
            navigator
        )
    }
}
