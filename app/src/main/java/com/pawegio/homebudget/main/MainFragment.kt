package com.pawegio.homebudget.main


import android.os.Bundle
import android.view.View
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
        viewModel.monthlyBudget.observe(this, Observer(::updateMonthlyBudget))
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
            incomes.subcategories
                .filter { it.actual.toDouble() > 0.0 || it.planned.toDouble() > 0.0 }
                .forEach { subcategory ->
                    allIncomesLayout.addView(SubcategoryView(requireContext()).apply {
                        this.subcategory = subcategory
                    })
                }
            val allExpenses = categories.drop(1)
            allExpenses.forEach { category ->
                allExpensesLayout.addView(CategoryView(requireContext()).apply {
                    this.category = category
                })
            }
        }
    }
}
