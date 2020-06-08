@file:Suppress("FunctionName")

package com.pawegio.homebudget.main.newexpense

import androidx.lifecycle.LiveData
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
    categories: LiveData<List<String>>,
    api: HomeBudgetApi,
    clock: Clock
) {
    var selectedDate = clock.instant().atZone(ZoneId.systemDefault()).toLocalDate()
    var selectedCategory = categories.value?.first().orEmpty()
    while (true) {
        when (val action = actions.awaitFirst()) {
            is NewExpenseAction.SelectDate -> {
                selectedDate = action.date
                state.value = NewExpenseState(selectedDate, "")
            }
            is NewExpenseAction.SelectCategory -> {
                selectedCategory = action.category
                state.value = NewExpenseState(selectedDate, selectedCategory)
            }
            NewExpenseAction.SelectAdd -> {
                val expense = NewExpense(selectedDate, selectedCategory)
                api.addExpense(expense)
            }
        }
    }
}

data class NewExpenseState(
    val selectedDate: LocalDate,
    val selectedCategory: String
)

sealed class NewExpenseAction {
    data class SelectDate(val date: LocalDate) : NewExpenseAction()
    data class SelectCategory(val category: String) : NewExpenseAction()
    object SelectAdd : NewExpenseAction()
}
