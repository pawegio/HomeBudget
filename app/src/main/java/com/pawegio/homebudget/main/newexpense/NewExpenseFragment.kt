package com.pawegio.homebudget.main.newexpense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.pawegio.homebudget.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import splitties.views.onClick

class NewExpenseFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private val ui by lazy { NewExpenseUi(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ui.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.newExpenseState.observe(viewLifecycleOwner, Observer(::updateState))
        viewModel.categories.observe(viewLifecycleOwner, Observer(::updateCategories))
        ui.addExpenseButton.onClick {
            viewModel.newExpenseActions.accept(NewExpenseAction.SelectAdd)
        }
        ui.onBackClick = {
            viewModel.newExpenseActions.accept(NewExpenseAction.SelectBack)
        }
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
