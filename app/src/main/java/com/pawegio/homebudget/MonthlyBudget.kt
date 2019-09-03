package com.pawegio.homebudget

import java.math.BigDecimal

data class MonthlyBudget(
    val plannedIncomes: BigDecimal,
    val plannedExpenses: BigDecimal,
    val actualIncomes: BigDecimal,
    val actualExpenses: BigDecimal,
    val categories: List<Category>
)

data class Category(
    val name: String,
    val type: Type,
    val subcategories: List<Subcategory>,
    val planned: BigDecimal,
    val actual: BigDecimal
) {
    enum class Type {
        INCOMES, EXPENSES
    }
}

data class Subcategory(
    val name: String,
    val planned: BigDecimal,
    val actual: BigDecimal
)
