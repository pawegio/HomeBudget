package com.pawegio.homebudget.main.transaction

import androidx.lifecycle.*
import com.jakewharton.rxrelay2.PublishRelay
import com.pawegio.homebudget.Category
import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.MonthlyBudget
import com.pawegio.homebudget.Navigator
import com.pawegio.homebudget.util.ToastNotifier
import kotlinx.coroutines.launch
import java.time.Clock

class TransactionViewModel(
    private val monthlyBudget: MonthlyBudget,
    private val api: HomeBudgetApi,
    private val clock: Clock,
    private val navigator: Navigator,
    private val toastNotifier: ToastNotifier
) : ViewModel() {

    val transactionState: LiveData<TransactionState> get() = _transactionState
    val categories: LiveData<List<Category>> get() = MutableLiveData(monthlyBudget.categories)

    val transactionActions = PublishRelay.create<TransactionAction>()

    private val _transactionState = MutableLiveData<TransactionState>()

    init {
        viewModelScope.launch {
            TransactionLogic(
                monthlyBudget,
                transactionActions,
                _transactionState,
                api,
                clock,
                toastNotifier,
                navigator
            )
        }
    }
}
