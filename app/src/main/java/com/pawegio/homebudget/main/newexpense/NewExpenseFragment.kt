package com.pawegio.homebudget.main.newexpense

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.pawegio.homebudget.MainViewModel
import com.pawegio.homebudget.R
import com.pawegio.homebudget.common.DatePickerFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.threeten.bp.LocalDate

class NewExpenseFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private val ui by lazy { NewExpenseUi(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.new_expense_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ui.root

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.newExpenseState.observe(viewLifecycleOwner, Observer(::updateState))
        viewModel.categories.observe(viewLifecycleOwner, Observer(::updateCategories))
        ui.backClicks.map { NewExpenseAction.SelectBack }.subscribe(viewModel.newExpenseActions)
        ui.dateClicks.subscribe(::showDatePicker)
    }

    private fun showDatePicker(date: LocalDate?) {
        DatePickerFragment.withDate(date).apply {
            dateChanges
                .takeUntil(dismisses)
                .subscribe { selectedDate ->
                    viewModel.newExpenseActions.accept(NewExpenseAction.SelectDate(selectedDate))
                }
        }.show(childFragmentManager, "datePicker")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> viewModel.newExpenseActions.accept(NewExpenseAction.SelectAdd)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateState(state: NewExpenseState) {
        ui.date = state.selectedDate
    }

    private fun updateCategories(categories: List<String>) {
        ui.categorySpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            categories
        )
    }
}
