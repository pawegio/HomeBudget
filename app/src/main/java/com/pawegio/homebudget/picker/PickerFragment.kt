package com.pawegio.homebudget.picker

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.pawegio.homebudget.MainViewModel
import com.pawegio.homebudget.R
import kotlinx.android.synthetic.main.picker_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PickerFragment : Fragment(R.layout.picker_fragment) {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTemplateSpinner()
        connectSpreadsheetButton.setOnClickListener {
            val url = spreadsheetUrlEditText.editableText.toString()
            viewModel.pickerActions.accept(PickerAction.PickDocument(url))
        }
        howToButton.setOnClickListener {
            viewModel.pickerActions.accept(PickerAction.SelectHowTo)
        }
        templateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val template = if (position == 0) 2020 else 2019
                viewModel.pickerActions.accept(PickerAction.SelectTemplate(template))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun setupTemplateSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.templates_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            templateSpinner.adapter = adapter
        }
    }
}
