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
    monthlyBudget: LiveData<MonthlyBudget>,
    api: HomeBudgetApi,
    clock: Clock,
    toastNotifier: ToastNotifier,
    navigator: Navigator
) {
    var date = clock.instant().atZone(ZoneId.systemDefault()).toLocalDate()
    val category = checkNotNull(monthlyBudget.value)
        .categories.first { it.type == Category.Type.EXPENSES }
    var subcategory = category.subcategories.first()
    var value: BigDecimal? = null
    state.value = NewExpenseState(date, category, subcategory, value)
    loop@ while (true) {
        when (val action = actions.awaitFirst()) {
            is SelectDate -> {
                date = action.date
                state.value = NewExpenseState(date, category, subcategory, value)
            }
            is SelectCategory -> {
                subcategory = action.category.subcategories.first()
                state.value = NewExpenseState(date, category, subcategory, value)
            }
            is SelectSubcategory -> {
                subcategory = action.subcategory
                state.value = NewExpenseState(date, category, subcategory, value)
            }
            is SelectValue -> {
                value = action.value
                state.value = NewExpenseState(date, category, subcategory, value)
            }
            SelectAdd -> {
                val expense = NewExpense(date, subcategory, checkNotNull(value))
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
    val selectedCategory: Category,
    val selectedSubcategory: Subcategory,
    val selectedValue: BigDecimal?
)

sealed class NewExpenseAction {
    data class SelectDate(val date: LocalDate) : NewExpenseAction()
    data class SelectCategory(val category: Category) : NewExpenseAction()
    data class SelectSubcategory(val subcategory: Subcategory) : NewExpenseAction()
    data class SelectValue(val value: BigDecimal) : NewExpenseAction()
    object SelectAdd : NewExpenseAction()
    object SelectBack : NewExpenseAction()
}
