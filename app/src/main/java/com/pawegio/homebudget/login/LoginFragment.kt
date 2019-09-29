package com.pawegio.homebudget.login

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.pawegio.homebudget.AppState
import com.pawegio.homebudget.MainAction
import com.pawegio.homebudget.MainViewModel
import com.pawegio.homebudget.R
import kotlinx.android.synthetic.main.login_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment : Fragment(R.layout.login_fragment) {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.appState.observe(this, Observer(::updateView))
        signInButton.setOnClickListener { viewModel.mainActions.offer(MainAction.SelectSignIn) }
    }

    private fun updateView(state: AppState?) {
        signInButton.isVisible = state is AppState.Unauthorized
    }
}
