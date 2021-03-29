package com.pawegio.homebudget

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class MonthlyBudget(
    val month: String,
    val plannedIncomes: BigDecimal,
    val plannedExpenses: BigDecimal,
    val actualIncomes: BigDecimal,
    val actualExpenses: BigDecimal,
    val categories: List<Category>
) : Parcelable

@Parcelize
data class Category(
    val index: Int,
    val name: String,
    val type: Type,
    val subcategories: List<Subcategory>,
    val planned: BigDecimal,
    val actual: BigDecimal
) : Parcelable {
    enum class Type {
        INCOMES, EXPENSES
    }
}

@Parcelize
data class Subcategory(
    val index: Int,
    val name: String,
    val planned: BigDecimal,
    val actual: BigDecimal,
    val type: Category.Type
) : Parcelable
