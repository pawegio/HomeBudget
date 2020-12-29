package com.pawegio.homebudget.main.transaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.pawegio.homebudget.Category
import com.pawegio.homebudget.MainViewModel
import com.pawegio.homebudget.R
import com.pawegio.homebudget.common.DatePickerFragment
import com.pawegio.homebudget.main.transaction.TransactionAction.*
import io.reactivex.Observable
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.threeten.bp.LocalDate

class TransactionFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private val ui by lazy { TransactionUi(requireContext()) }
    private var isTransactionReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.transaction_menu, menu)
        menu.findItem(R.id.action_add).isEnabled = isTransactionReady
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
        viewModel.transactionState.observe(viewLifecycleOwner, Observer(::updateState))
        viewModel.categories.observe(viewLifecycleOwner, Observer(::updateCategories))
        Observable.merge(
            ui.backClicks.map { SelectBack },
            ui.categorySelections.map(::SelectCategory),
            ui.subcategorySelections.map(::SelectSubcategory),
            ui.amountChanges.map { SelectValue(it.value) }
        ).subscribe(viewModel.transactionActions)
        ui.dateClicks.subscribe(::showDatePicker)
    }

    private fun showDatePicker(date: LocalDate?) {
        DatePickerFragment.withDate(date).apply {
            dateChanges
                .takeUntil(dismisses)
                .subscribe { selectedDate ->
                    viewModel.transactionActions.accept(SelectDate(selectedDate))
                }
        }.show(childFragmentManager, "datePicker")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> viewModel.transactionActions.accept(SelectAdd)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateState(state: TransactionState) {
        ui.date = state.selectedDate
        isTransactionReady = state.selectedValue != null
        activity?.invalidateOptionsMenu()
    }

    private fun updateCategories(categories: List<Category>) {
        ui.categories = categories
    }
}
