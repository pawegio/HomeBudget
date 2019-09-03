package com.pawegio.homebudget

import java.math.BigDecimal

fun createMonthlyBudget() =
    MonthlyBudget(
        plannedIncomes = BigDecimal(10000),
        plannedExpenses = BigDecimal(10000),
        actualIncomes = BigDecimal(11000),
        actualExpenses = BigDecimal(9000),
        categories = emptyList()
    )
