package com.pawegio.homebudget.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.pawegio.homebudget.MainViewModel
import com.pawegio.homebudget.R
import kotlinx.android.synthetic.main.login_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment : Fragment(R.layout.login_fragment) {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signInButton.setOnClickListener { viewModel.loginActions.accept(LoginAction.SelectSignIn) }
    }
}
