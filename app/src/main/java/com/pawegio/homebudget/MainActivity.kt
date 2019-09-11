package com.pawegio.homebudget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.pawegio.homebudget.util.currencyValue
import kotlinx.android.synthetic.main.main_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        lifecycle.addObserver(CurrentActivityObserver)
        viewModel.appState.observe(this, Observer(::updateView))
        viewModel.monthlyBudget.observe(this, Observer(::updateMonthlyBudget))
        signInButton.setOnClickListener { viewModel.mainActions.offer(MainAction.SelectSignIn) }
    }

    private fun updateView(state: AppState?) {
        signInButton.isVisible = state is AppState.Unauthorized
    }

    private fun updateMonthlyBudget(monthlyBudget: MonthlyBudget?) {
        monthlyBudget?.let { (month, _, _, actualIncomes, actualExpenses, categories) ->
            monthHeaderView.text = month
            incomesTextView.text = actualIncomes.currencyValue
            expensesTextView.text = actualExpenses.currencyValue
            totalTextView.text = (actualIncomes - actualExpenses).currencyValue
            val incomes = categories.first()
            allIncomesTextView.text = listOf(
                incomes.subcategories.joinToString("\n\n") {
                    "${it.name}:\n${it.actual.currencyValue} / ${it.planned.currencyValue}"
                }
            ).joinToString("\n")
        }
    }
}
