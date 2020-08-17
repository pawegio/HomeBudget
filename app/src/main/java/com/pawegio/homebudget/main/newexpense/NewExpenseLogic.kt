@file:Suppress("FunctionName")

package com.pawegio.homebudget.main.newexpense

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pawegio.homebudget.*
import com.pawegio.homebudget.main.newexpense.NewExpenseAction.*
import com.pawegio.homebudget.util.ToastNotifier
import io.reactivex.Observable
import kotlinx.coroutines.rx2.awaitFirst
import org.threeten.bp.Clock
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import java.math.BigDecimal

suspend fun NewExpenseLogic(
    actions: Observable<NewExpenseAction>,
    state: MutableLiveData<NewExpenseState>,
    categories: LiveData<List<String>>,
    api: HomeBudgetApi,
    clock: Clock,
    toastNotifier: ToastNotifier,
    navigator: Navigator
) {
    var selectedDate = clock.instant().atZone(ZoneId.systemDefault()).toLocalDate()
    var selectedCategory = categories.value?.first().orEmpty()
    var selectedValue: BigDecimal? = null
    state.value = NewExpenseState(selectedDate, selectedCategory, selectedValue)
    loop@ while (true) {
        when (val action = actions.awaitFirst()) {
            is SelectDate -> {
                selectedDate = action.date
                state.value = NewExpenseState(selectedDate, selectedCategory, selectedValue)
            }
            is SelectCategory -> {
                selectedCategory = action.category
                state.value = NewExpenseState(selectedDate, selectedCategory, selectedValue)
            }
            is SelectValue -> {
                selectedValue = action.value
                state.value = NewExpenseState(selectedDate, selectedCategory, selectedValue)
            }
            SelectAdd -> {
                val expense = NewExpense(
                    selectedDate,
                    selectedCategory,
                    checkNotNull(selectedValue)
                )
                try {
                    api.addExpense(expense)
                    break@loop
                } catch (e: HomeBudgetApiException) {
                    toastNotifier.notify(R.string.add_expense_error_message)
                }
            }
            SelectBack -> break@loop
        }
    }
    navigator.popBackStack()
}

data class NewExpenseState(
    val selectedDate: LocalDate,
    val selectedCategory: String,
    val selectedValue: BigDecimal?
)

sealed class NewExpenseAction {
    data class SelectDate(val date: LocalDate) : NewExpenseAction()
    data class SelectCategory(val category: String) : NewExpenseAction()
    data class SelectValue(val value: BigDecimal) : NewExpenseAction()
    object SelectAdd : NewExpenseAction()
    object SelectBack : NewExpenseAction()
}
