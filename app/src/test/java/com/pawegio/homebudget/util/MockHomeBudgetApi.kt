package com.pawegio.homebudget.util

import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.MonthlyBudget
import com.pawegio.homebudget.NewExpense
import org.threeten.bp.Month
import pl.mareklangiewicz.smokk.smokk

class MockHomeBudgetApi : HomeBudgetApi {

    override val isSignedIn: Boolean get() = isSignInResult

    val signIn = smokk<Unit>()
    val signOut = smokk<Unit>()
    val getMonthlyBudget = smokk<Month, MonthlyBudget>()
    val addExpense = smokk<NewExpense, Unit>()

    val addExpenseCalled get() = addExpense.invocations.count() > 0
    val addedExpenseDate get() = addExpense.invocations.last().date
    val addedExpenseSubcategory get() = addExpense.invocations.last().subcategory
    val addedExpenseValue get() = addExpense.invocations.last().value

    var isSignInResult = false

    override suspend fun signIn() = signIn.invoke()

    override suspend fun signOut() = signOut.invoke()

    override suspend fun getMonthlyBudget(month: Month): MonthlyBudget =
        getMonthlyBudget.invoke(month)

    override suspend fun addExpense(expense: NewExpense) = addExpense.invoke(expense)
}
