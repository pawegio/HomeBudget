package com.pawegio.homebudget.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pawegio.homebudget.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import splitties.views.onClick

class LoginFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private val ui by lazy { LoginUi(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ui.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui.signInButton.onClick { viewModel.loginActions.accept(LoginAction.SelectSignIn) }
    }
}
