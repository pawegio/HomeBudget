@file:Suppress("FunctionName")

package com.pawegio.homebudget.main.newexpense

import androidx.lifecycle.MutableLiveData
import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.NewExpense
import io.reactivex.Observable
import kotlinx.coroutines.rx2.awaitFirst
import org.threeten.bp.Clock
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

suspend fun NewExpenseLogic(
    actions: Observable<NewExpenseAction>,
    state: MutableLiveData<NewExpenseState>,
    api: HomeBudgetApi,
    clock: Clock
) {
    var selectedDate = clock.instant().atZone(ZoneId.systemDefault()).toLocalDate()
    while (true) {
        when (val action = actions.awaitFirst()) {
            is NewExpenseAction.SelectDate -> {
                selectedDate = action.date
                state.value = NewExpenseState(selectedDate)
            }
            NewExpenseAction.SelectAdd -> {
                val expense = NewExpense(selectedDate)
                api.addExpense(expense)
            }
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
