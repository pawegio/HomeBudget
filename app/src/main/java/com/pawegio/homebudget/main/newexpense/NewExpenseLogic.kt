@file:Suppress("FunctionName")

package com.pawegio.homebudget.main.newexpense

import com.pawegio.homebudget.HomeBudgetApi
import io.reactivex.Observable
import kotlinx.coroutines.rx2.awaitFirst

suspend fun NewExpenseLogic(
    actions: Observable<NewExpenseAction>,
    api: HomeBudgetApi
) {
    actions.awaitFirst()
    api.addExpense()
}

sealed class NewExpenseAction {
    object SelectAdd : NewExpenseAction()
}
