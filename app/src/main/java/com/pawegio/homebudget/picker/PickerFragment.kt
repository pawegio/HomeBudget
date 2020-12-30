package com.pawegio.homebudget.picker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.pawegio.homebudget.MainViewModel
import com.pawegio.homebudget.R
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import splitties.views.onClick

class PickerFragment : Fragment(), TextWatcher {

    private val viewModel by sharedViewModel<MainViewModel>()
    private val ui by lazy { PickerUi(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ui.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTemplateSpinner()
        ui.spreadsheetUrlEditText.addTextChangedListener(this)
        ui.connectSpreadsheetButton.onClick {
            val url = ui.spreadsheetUrlEditText.editableText.toString()
            viewModel.pickerActions.accept(PickerAction.PickDocument(url))
        }
        ui.howToButton.onClick {
            viewModel.pickerActions.accept(PickerAction.SelectHowTo)
        }
        ui.templateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val template = when (position) {
                    0 -> 2021
                    1 -> 2020
                    else -> 2019
                }
                viewModel.pickerActions.accept(PickerAction.SelectTemplate(template))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    override fun onDestroyView() {
        ui.spreadsheetUrlEditText.removeTextChangedListener(this)
        super.onDestroyView()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun afterTextChanged(s: Editable) {
        ui.connectSpreadsheetButton.isEnabled = s.isNotBlank()
    }

    private fun setupTemplateSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.templates_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            ui.templateSpinner.adapter = adapter
        }
    }
}
