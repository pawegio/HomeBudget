package com.pawegio.homebudget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.MainScope
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        lifecycle.addObserver(CurrentActivityObserver)
        viewModel.appState.observe(this, Observer(::updateView))
        signInButton.setOnClickListener { viewModel.mainEvents.offer(MainEvent.SelectSignIn) }
    }

    private fun updateView(state: AppState?) {
        signInButton.isVisible = state is AppState.Unauthorized
    }
}
