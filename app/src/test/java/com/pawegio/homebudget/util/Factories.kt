package com.pawegio.homebudget.util

import com.pawegio.homebudget.Category
import com.pawegio.homebudget.MonthlyBudget
import com.pawegio.homebudget.Subcategory
import java.math.BigDecimal

fun createMonthlyBudget() = MonthlyBudget(
    month = "Kwiecień",
    plannedIncomes = BigDecimal(10000),
    plannedExpenses = BigDecimal(10000),
    actualIncomes = BigDecimal(11000),
    actualExpenses = BigDecimal(9000),
    categories = emptyList()
)

fun createCategory(
    index: Int = 0,
    name: String = "Jedzenie"
) = Category(
    index = index,
    name = name,
    type = Category.Type.EXPENSES,
    subcategories = List(3) { createSubcategory() },
    planned = BigDecimal(5),
    actual = BigDecimal(3)
)

fun createSubcategory(
    name: String = "Jedzenie na mieście"
) = Subcategory(
    name = name,
    planned = BigDecimal(5),
    actual = BigDecimal(3),
    type = Category.Type.EXPENSES
)