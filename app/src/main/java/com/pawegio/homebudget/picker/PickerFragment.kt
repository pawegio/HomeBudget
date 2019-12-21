package com.pawegio.homebudget.picker

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.pawegio.homebudget.MainViewModel
import com.pawegio.homebudget.R
import kotlinx.android.synthetic.main.picker_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PickerFragment : Fragment(R.layout.picker_fragment) {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectSpreadsheetButton.setOnClickListener {
            val url = spreadsheetUrlEditText.editableText.toString()
            viewModel.pickerActions.accept(PickerAction.PickDocument(url))
        }
        howToButton.setOnClickListener {
            viewModel.pickerActions.accept(PickerAction.SelectHowTo)
        }
    }
}
