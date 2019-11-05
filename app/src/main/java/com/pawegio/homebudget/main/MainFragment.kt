package com.pawegio.homebudget.main

import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.pawegio.homebudget.MainViewModel
import com.pawegio.homebudget.MonthlyBudget
import com.pawegio.homebudget.R
import com.pawegio.homebudget.util.currencyValue
import kotlinx.android.synthetic.main.main_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MainFragment : Fragment(R.layout.main_fragment) {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.monthType.observe(this, Observer(::updateMonthType))
        viewModel.monthlyBudget.observe(this, Observer(::updateMonthlyBudget))
        prevMonthButton.setOnClickListener {
            viewModel.mainActions.offer(MainAction.SelectPrevMonth)
        }
        nextMonthButton.setOnClickListener {
            viewModel.mainActions.offer(MainAction.SelectNextMonth)
        }
        openSpreadsheetButton.setOnClickListener {
            viewModel.mainActions.offer(MainAction.OpenSpreadsheet)
        }
    }

    private fun updateMonthType(monthType: MonthType?) {
        prevMonthButton.isInvisible = monthType == MonthType.FIRST
        nextMonthButton.isInvisible = monthType == MonthType.LAST
    }

    private fun updateMonthlyBudget(monthlyBudget: MonthlyBudget?) {
        monthlyBudget?.let { (month, plannedIncomes, plannedExpenses, actualIncomes, actualExpenses, categories) ->
            monthHeaderView.text = month
            incomesTextView.text = actualIncomes.currencyValue
            plannedIncomesView.text = getString(R.string.of, plannedIncomes.currencyValue)
            expensesTextView.text = actualExpenses.currencyValue
            plannedExpensesView.text = getString(R.string.of, plannedExpenses.currencyValue)
            totalTextView.text = (actualIncomes - actualExpenses).currencyValue
            val incomes = categories.first()
            allIncomesLayout.removeAllViews()
            incomes.subcategories
                .filter { it.actual.toDouble() > 0.0 || it.planned.toDouble() > 0.0 }
                .forEach { subcategory ->
                    allIncomesLayout.addView(SubcategoryView(requireContext()).apply {
                        this.subcategory = subcategory
                    })
                }
            val allExpenses = categories.drop(1)
            allExpensesLayout.removeAllViews()
            allExpenses.forEach { category ->
                allExpensesLayout.addView(CategoryView(requireContext()).apply {
                    this.category = category
                })
            }
        }
    }
}
