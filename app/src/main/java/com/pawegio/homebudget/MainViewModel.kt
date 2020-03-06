package com.pawegio.homebudget

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import com.pawegio.homebudget.login.LoginAction
import com.pawegio.homebudget.login.LoginLogic
import com.pawegio.homebudget.main.MainAction
import com.pawegio.homebudget.main.MainLogic
import com.pawegio.homebudget.main.MonthType
import com.pawegio.homebudget.picker.PickerAction
import com.pawegio.homebudget.picker.PickerLogic
import com.pawegio.homebudget.picker.parseSpreadsheetId
import com.pawegio.homebudget.util.HowToLauncher
import com.pawegio.homebudget.util.SpreadsheetLauncher
import com.pawegio.homebudget.util.ToastNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.threeten.bp.Clock

class MainViewModel(
    private val repository: HomeBudgetRepository,
    private val api: HomeBudgetApi,
    private val howToLauncher: HowToLauncher,
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

    init {
        navigator.restart(R.navigation.app_navigation)
        launch { initLogin() }
    }

    private suspend fun initLogin() {
        LoginLogic(
            loginActions,
            repository,
            api,
            toastNotifier,
            ::initPicker,
            ::initMain,
            navigator
        )
    }

    private suspend fun initPicker() {
        PickerLogic(
            pickerActions,
            howToLauncher,
            repository,
            ::parseSpreadsheetId,
            ::initMain,
            navigator
        )
    }

    private suspend fun initMain() {
        MainLogic(
            mainActions,
            _monthType,
            _monthlyBudget,
            _isLoading,
            repository,
            api,
            spreadsheetLauncher,
            clock,
            ::initPicker,
            navigator
        )
    }
}
