@file:Suppress("FunctionName")

package com.pawegio.homebudget.main.transaction

import androidx.lifecycle.MutableLiveData
import com.pawegio.homebudget.*
import com.pawegio.homebudget.main.transaction.TransactionAction.*
import com.pawegio.homebudget.util.ToastNotifier
import io.reactivex.Observable
import kotlinx.coroutines.rx2.awaitFirst
import org.threeten.bp.Clock
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import java.math.BigDecimal

suspend fun TransactionLogic(
    monthlyBudget: MonthlyBudget,
    actions: Observable<TransactionAction>,
    state: MutableLiveData<TransactionState>,
    api: HomeBudgetApi,
    clock: Clock,
    toastNotifier: ToastNotifier,
    navigator: Navigator
) {
    var note: String? = null
    var date = clock.instant().atZone(ZoneId.systemDefault()).toLocalDate()
    val category = monthlyBudget.categories.first { it.type == Category.Type.EXPENSES }
    var subcategory = category.subcategories.first()
    var value: BigDecimal? = null
    state.value = TransactionState(note, date, category, subcategory, value)
    loop@ while (true) {
        when (val action = actions.awaitFirst()) {
            is EnterNote -> {
                note = action.note
                state.value = TransactionState(note, date, category, subcategory, value)
            }
            is SelectDate -> {
                date = action.date
                state.value = TransactionState(note, date, category, subcategory, value)
            }
            is SelectCategory -> {
                subcategory = action.category.subcategories.first()
                state.value = TransactionState(note, date, category, subcategory, value)
            }
            is SelectSubcategory -> {
                subcategory = action.subcategory
                state.value = TransactionState(note, date, category, subcategory, value)
            }
            is SelectValue -> {
                value = action.value
                state.value = TransactionState(note, date, category, subcategory, value)
            }
            SelectAdd -> {
                val transaction = Transaction(note, date, subcategory, checkNotNull(value))
                try {
                    api.addTransaction(transaction)
                    toastNotifier.notify(R.string.transaction_added_message)
                    break@loop
                } catch (e: HomeBudgetApiException) {
                    toastNotifier.notify(R.string.add_transaction_error_message)
                }
            }
            SelectBack -> break@loop
        }
    }
    navigator.popBackStack()
}

data class TransactionState(
    val enteredNote: String?,
    val selectedDate: LocalDate,
    val selectedCategory: Category,
    val selectedSubcategory: Subcategory,
    val selectedValue: BigDecimal?
)

sealed class TransactionAction {
    data class EnterNote(val note: String?) : TransactionAction()
    data class SelectDate(val date: LocalDate) : TransactionAction()
    data class SelectCategory(val category: Category) : TransactionAction()
    data class SelectSubcategory(val subcategory: Subcategory) : TransactionAction()
    data class SelectValue(val value: BigDecimal?) : TransactionAction()
    object SelectAdd : TransactionAction()
    object SelectBack : TransactionAction()
}
