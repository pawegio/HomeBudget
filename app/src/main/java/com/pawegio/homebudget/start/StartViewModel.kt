package com.pawegio.homebudget.start

import androidx.lifecycle.*
import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.HomeBudgetRepository
import com.pawegio.homebudget.Navigator
import kotlinx.coroutines.launch

class StartViewModel(
    private val repository: HomeBudgetRepository,
    private val api: HomeBudgetApi,
    private val navigator: Navigator,
) : ViewModel(), LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        println("START LOGIC")
        viewModelScope.launch {
            StartLogic(
                repository,
                api,
                navigator,
            )
        }
    }
}
