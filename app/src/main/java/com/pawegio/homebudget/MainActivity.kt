package com.pawegio.homebudget

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.pawegio.homebudget.main.transaction.TransactionAction
import com.pawegio.homebudget.picker.PickerAction
import com.pawegio.homebudget.util.CurrentActivityObserver
import kotlinx.android.synthetic.main.main_activity.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(CurrentActivityObserver)
        appNavController = navHostFragment.findNavController()
        lifecycle.addObserver(getViewModel<MainViewModel>())
    }

    override fun onBackPressed() =
        when (appNavController?.currentDestination?.id) {
            R.id.transactionFragment -> viewModel.transactionActions.accept(TransactionAction.SelectBack)
            R.id.pickerFragment -> viewModel.pickerActions.accept(PickerAction.SelectBack)
            else -> finish()
        }
}

@SuppressLint("StaticFieldLeak")
var appNavController: NavController? = null
