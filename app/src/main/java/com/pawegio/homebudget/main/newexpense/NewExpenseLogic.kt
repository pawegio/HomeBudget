@file:Suppress("FunctionName")

package com.pawegio.homebudget.main.newexpense

import androidx.lifecycle.MutableLiveData
import com.pawegio.homebudget.HomeBudgetApi
import io.reactivex.Observable
import kotlinx.coroutines.rx2.awaitFirst
import org.threeten.bp.LocalDate

suspend fun NewExpenseLogic(
    actions: Observable<NewExpenseAction>,
    state: MutableLiveData<NewExpenseState>,
    api: HomeBudgetApi
) {
    var selectedDate: LocalDate
    while (true) {
        when (val action = actions.awaitFirst()) {
            is NewExpenseAction.SelectDate -> {
                selectedDate = action.date
                state.value = NewExpenseState(selectedDate)
            }
            NewExpenseAction.SelectAdd -> api.addExpense()
        }
    }
}

data class NewExpenseState(
    val selectedDate: LocalDate
)

sealed class NewExpenseAction {
    data class SelectDate(val date: LocalDate) : NewExpenseAction()
    object SelectAdd : NewExpenseAction()
}
