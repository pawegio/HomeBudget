package com.pawegio.homebudget.about

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.pawegio.homebudget.R
import splitties.alertdialog.appcompat.alertDialog
import splitties.alertdialog.appcompat.okButton
import splitties.alertdialog.appcompat.titleResource

class AboutFragment : DialogFragment() {

    private val ui by lazy { AboutUi(requireActivity()) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        requireActivity().alertDialog {
            titleResource = R.string.about_app
            setView(ui.root)
            okButton()
        }
}
