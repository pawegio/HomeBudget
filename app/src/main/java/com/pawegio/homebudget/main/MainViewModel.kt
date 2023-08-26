package com.pawegio.homebudget.main

import androidx.lifecycle.*
import com.jakewharton.rxrelay2.PublishRelay
import com.pawegio.homebudget.*
import com.pawegio.homebudget.util.SpreadsheetLauncher
import com.pawegio.homebudget.util.ToastNotifier
import kotlinx.coroutines.launch
import java.time.Clock

class MainViewModel(
    private val repository: HomeBudgetRepository,
    private val api: HomeBudgetApi,
    private val spreadsheetLauncher: SpreadsheetLauncher,
    private val clock: Clock,
    private val toastNotifier: ToastNotifier,
    private val navigator: Navigator,
) : ViewModel() {
    val monthlyBudget: LiveData<MonthlyBudget> get() = _monthlyBudget
    val monthType: LiveData<MonthType> get() = _monthType
    val isLoading: LiveData<Boolean> get() = _isLoading

    val actions = PublishRelay.create<MainAction>()

    private val _monthlyBudget = MutableLiveData<MonthlyBudget>()
    private val _monthType = MutableLiveData<MonthType>()
    private val _isLoading = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch {
            MainLogic(
                actions,
                _monthType,
                _monthlyBudget,
                _isLoading,
                repository,
                api,
                spreadsheetLauncher,
                clock,
                toastNotifier,
                navigator
            )
        }
    }
}
