package com.pawegio.homebudget

data class MonthlyBudget(
    val plannedIncomes: String,
    val plannedExpenses: String,
    val actualIncomes: String,
    val actualExpenses: String,
    val categories: List<Category>
)

data class Category(
    val name: String,
    val type: Type,
    val subcategories: List<Subcategory>,
    val planned: String,
    val actual: String
) {
    enum class Type {
        INCOMES, EXPENSES
    }
}

data class Subcategory(
    val name: String,
    val planned: String,
    val actual: String
)
