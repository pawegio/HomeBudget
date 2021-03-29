package com.pawegio.homebudget.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakewharton.rxrelay2.PublishRelay
import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.HomeBudgetRepository
import com.pawegio.homebudget.Navigator
import com.pawegio.homebudget.util.ToastNotifier
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: HomeBudgetRepository,
    private val api: HomeBudgetApi,
    private val toastNotifier: ToastNotifier,
    private val navigator: Navigator,
) : ViewModel() {

    val actions = PublishRelay.create<LoginAction>()

    init {
        viewModelScope.launch {
            LoginLogic(
                actions,
                repository,
                api,
                toastNotifier,
                navigator,
            )
        }
    }
}
