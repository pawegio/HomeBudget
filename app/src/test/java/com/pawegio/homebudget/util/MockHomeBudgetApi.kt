package com.pawegio.homebudget.util

import com.pawegio.homebudget.HomeBudgetApi
import com.pawegio.homebudget.MonthlyBudget
import com.pawegio.homebudget.Transaction
import pl.mareklangiewicz.smokk.smokk
import java.time.Month

class MockHomeBudgetApi : HomeBudgetApi {

    override val isSignedIn: Boolean get() = isSignInResult

    val signIn = smokk<Unit>()
    val signOut = smokk<Unit>()
    val getFinancialYear = smokk<Int?>()
    val getMonthlyBudget = smokk<Month, MonthlyBudget>()
    val addTransaction = smokk<Transaction, Unit>()

    val addTransactionCalled get() = addTransaction.invocations.isNotEmpty()
    val addedTransactionNote get() = addTransaction.invocations.last().note
    val addedTransactionDate get() = addTransaction.invocations.last().date
    val addedTransactionSubcategory get() = addTransaction.invocations.last().subcategory
    val addedTransactionValue get() = addTransaction.invocations.last().value

    var isSignInResult = false

    override suspend fun signIn() = signIn.invoke()

    override suspend fun signOut() = signOut.invoke()

    override suspend fun getFinancialYear(): Int? = getFinancialYear.invoke()

    override suspend fun getMonthlyBudget(month: Month): MonthlyBudget =
        getMonthlyBudget.invoke(month)

    override suspend fun addTransaction(transaction: Transaction) = addTransaction.invoke(transaction)
}
