package com.pawegio.homebudget.util

import com.pawegio.homebudget.MonthlyBudget
import java.math.BigDecimal

fun createMonthlyBudget() =
    MonthlyBudget(
        month = "Kwiecień",
        plannedIncomes = BigDecimal(10000),
        plannedExpenses = BigDecimal(10000),
        actualIncomes = BigDecimal(11000),
        actualExpenses = BigDecimal(9000),
        categories = emptyList()
    )
