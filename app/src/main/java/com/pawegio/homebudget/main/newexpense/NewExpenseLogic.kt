@file:Suppress("FunctionName")

package com.pawegio.homebudget.main.newexpense

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.Navigator
import com.pawegio.homebudget.NewExpense
import com.pawegio.homebudget.main.newexpense.NewExpenseAction.*
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
                api.addExpense(expense)
            }
            SelectBack -> {
                navigator.popBackStack()
                break@loop
            }
        }
    }
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
