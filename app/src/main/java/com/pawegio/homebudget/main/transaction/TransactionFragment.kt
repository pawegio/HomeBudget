package com.pawegio.homebudget.main.transaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.maltaisn.calcdialog.CalcDialog
import com.pawegio.homebudget.Category
import com.pawegio.homebudget.NavGraph
import com.pawegio.homebudget.common.DatePickerFragment
import com.pawegio.homebudget.main.transaction.TransactionAction.*
import io.reactivex.Observable
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate

class TransactionFragment : Fragment(), CalcDialog.CalcDialogCallback {

    private val viewModel by viewModel<TransactionViewModel> {
        parametersOf(requireArguments()[NavGraph.Args.monthlyBudget])
    }

    private val ui by lazy { TransactionUi(requireContext()) }
    private val calcDialog = CalcDialog()
    private var isTransactionReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        Observable.mergeArray(
            ui.backClicks.map { SelectBack },
            ui.noteChanges.map { EnterNote(it.value) },
            ui.categorySelections.map(::SelectCategory),
            ui.subcategorySelections.map(::SelectSubcategory),
            ui.amountChanges.map { SelectValue(it.value) },
            ui.addClicks.filter { isTransactionReady }.map { SelectAdd }
        ).subscribe(viewModel.transactionActions)
        ui.dateClicks.subscribe(::showDatePicker)
        ui.amountClicks.subscribe { showCalcDialog() }
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

    private fun showCalcDialog() {
        calcDialog.settings.run {
            requestCode = CALC_DIALOG_REQUEST_CODE
            initialValue = ui.amount
            isExpressionShown = true
            isExpressionEditable = true
            isSignBtnShown = false
            numberFormat = (DecimalFormat.getInstance() as DecimalFormat).apply {
                decimalFormatSymbols = DecimalFormatSymbols().apply {
                    decimalSeparator = ','
                }
            }
        }
        calcDialog.show(childFragmentManager, "calc_dialog")
    }

    override fun onValueEntered(requestCode: Int, value: BigDecimal?) {
        if (requestCode == CALC_DIALOG_REQUEST_CODE) {
            ui.amount = value
        }
    }

    private fun updateState(state: TransactionState) {
        when (state) {
            is TransactionState.InFillOut -> {
                ui.isInProgress = false
                ui.date = state.selectedDate
                isTransactionReady = state.selectedValue != null
            }
            TransactionState.InProgress -> {
                ui.isInProgress = true
            }
        }
    }

    private fun updateCategories(categories: List<Category>) {
        ui.categories = categories
    }
}

private const val CALC_DIALOG_REQUEST_CODE = 759
