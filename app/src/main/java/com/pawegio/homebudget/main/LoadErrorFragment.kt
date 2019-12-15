package com.pawegio.homebudget.main

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.pawegio.homebudget.MainViewModel
import com.pawegio.homebudget.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.viewmodel.ext.android.sharedViewModel
import splitties.alertdialog.appcompat.*

@FlowPreview
@ExperimentalCoroutinesApi
class LoadErrorFragment : DialogFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        requireContext().alertDialog {
            titleResource = R.string.load_error_title
            messageResource = R.string.load_error_message
            positiveButton(R.string.load_error_positive) {
                dismissAllowingStateLoss()
                viewModel.mainActions.offer(MainAction.TryAgain)
            }
            negativeButton(R.string.load_error_negative) {
                dismissAllowingStateLoss()
                viewModel.mainActions.offer(MainAction.PickDocumentAgain)
            }
        }
}
