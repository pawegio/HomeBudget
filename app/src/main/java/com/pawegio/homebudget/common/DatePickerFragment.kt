package com.pawegio.homebudget.common

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import java.time.LocalDate

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private val dateChangesRelay = PublishRelay.create<LocalDate>()
    private val dismissesRelay = PublishRelay.create<Unit>()

    val dateChanges: Observable<LocalDate> get() = dateChangesRelay
    val dismisses: Observable<Unit> get() = dismissesRelay

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(DATE) as LocalDate? ?: LocalDate.now()
        return DatePickerDialog(requireContext(), this, date.year, date.monthValue - 1, date.dayOfMonth)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dateChangesRelay.accept(LocalDate.of(year, month + 1, dayOfMonth))
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissesRelay.accept(Unit)
    }

    companion object {
        private const val DATE = "DATE"

        fun withDate(date: LocalDate?): DatePickerFragment {
            val fragment = DatePickerFragment()
            fragment.arguments = bundleOf(DATE to date)
            return fragment
        }
    }
}
