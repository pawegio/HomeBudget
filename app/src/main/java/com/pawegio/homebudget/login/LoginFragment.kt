package com.pawegio.homebudget.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.getViewModel
import splitties.views.onClick

class LoginFragment : Fragment() {

    private val ui by lazy { LoginUi(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ui.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = getViewModel<LoginViewModel>()
        ui.signInButton.onClick { viewModel.actions.accept(LoginAction.SelectSignIn) }
    }
}
