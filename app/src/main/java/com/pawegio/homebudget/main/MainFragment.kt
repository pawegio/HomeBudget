package com.pawegio.homebudget.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.pawegio.homebudget.MainViewModel
import com.pawegio.homebudget.MonthlyBudget
import com.pawegio.homebudget.R
import com.pawegio.homebudget.util.currencyValue
import kotlinx.android.synthetic.main.month_summary_surface.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import splitties.views.onClick

class MainFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private val ui by lazy { MainUi(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ui.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.monthType.observe(viewLifecycleOwner, Observer(::updateMonthType))
        viewModel.monthlyBudget.observe(viewLifecycleOwner, Observer(::updateMonthlyBudget))
        viewModel.isLoading.observe(viewLifecycleOwner, Observer(::updateProgress))
        subscribeToActions()
    }

    private fun subscribeToActions() {
        ui.swipeRefreshLayout.setOnRefreshListener {
            viewModel.mainActions.accept(MainAction.Refresh)
            ui.swipeRefreshLayout.isRefreshing = false
        }
        ui.prevMonthButton.onClick {
            viewModel.mainActions.accept(MainAction.SelectPrevMonth)
        }
        ui.nextMonthButton.onClick {
            viewModel.mainActions.accept(MainAction.SelectNextMonth)
        }
        ui.openSpreadsheetButton.onClick {
            viewModel.mainActions.accept(MainAction.OpenSpreadsheet)
        }
        ui.moreButton.onClick { openPopupMenu() }
    }

    private fun openPopupMenu() {
        PopupMenu(requireContext(), ui.moreButton).apply {
            menuInflater.inflate(R.menu.main_popup_menu, menu)
            setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_add_expense -> MainAction.AddExpense
                    R.id.action_pick_document -> MainAction.PickDocumentAgain
                    R.id.action_about -> MainAction.SelectAbout
                    R.id.action_sign_out -> MainAction.SignOut
                    else -> throw IllegalArgumentException()
                }.let(viewModel.mainActions::accept)
                true
            }
        }.show()
    }

    private fun updateMonthType(monthType: MonthType?) {
        ui.prevMonthButton.isInvisible = monthType == MonthType.FIRST
        ui.nextMonthButton.isInvisible = monthType == MonthType.LAST
    }

    private fun updateMonthlyBudget(monthlyBudget: MonthlyBudget?) {
        monthlyBudget?.let { (month, plannedIncomes, plannedExpenses, actualIncomes, actualExpenses, categories) ->
            ui.monthHeaderView.text = month
            incomesTextView.text = actualIncomes.currencyValue
            plannedIncomesView.text = getString(R.string.of, plannedIncomes.currencyValue)
            expensesTextView.text = actualExpenses.currencyValue
            plannedExpensesView.text = getString(R.string.of, plannedExpenses.currencyValue)
            totalTextView.text = (actualIncomes - actualExpenses).currencyValue
            ui.incomes = categories.first().subcategories
            ui.expenses = categories.drop(1)
        }
    }

    private fun updateProgress(isLoading: Boolean?) {
        ui.isLoading = isLoading == true
    }
}
