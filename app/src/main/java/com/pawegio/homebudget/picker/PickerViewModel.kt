package com.pawegio.homebudget.picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakewharton.rxrelay2.PublishRelay
import com.pawegio.homebudget.HomeBudgetRepository
import com.pawegio.homebudget.Navigator
import com.pawegio.homebudget.util.HowToLauncher
import kotlinx.coroutines.launch

class PickerViewModel(
    private val howToLauncher: HowToLauncher,
    private val repository: HomeBudgetRepository,
    private val navigator: Navigator,
) : ViewModel() {

    val actions = PublishRelay.create<PickerAction>()

    init {
        viewModelScope.launch {
            PickerLogic(
                actions,
                howToLauncher,
                repository,
                ::parseSpreadsheetId,
                navigator,
            )
        }
    }
}
