package com.pawegio.homebudget

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.pawegio.homebudget.main.transaction.TransactionAction
import com.pawegio.homebudget.picker.PickerAction
import com.pawegio.homebudget.util.CurrentActivityObserver
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(CurrentActivityObserver)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        appNavController = navHostFragment.navController.also(::createNavGraph)
    }
}

@SuppressLint("StaticFieldLeak")
var appNavController: NavController? = null
