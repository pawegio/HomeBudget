package com.pawegio.homebudget

import androidx.navigation.NavController
import androidx.navigation.createGraph
import androidx.navigation.fragment.dialog
import androidx.navigation.fragment.fragment
import com.pawegio.homebudget.about.AboutFragment
import com.pawegio.homebudget.login.LoginFragment
import com.pawegio.homebudget.main.LoadErrorFragment
import com.pawegio.homebudget.main.MainFragment
import com.pawegio.homebudget.main.transaction.TransactionFragment
import com.pawegio.homebudget.picker.PickerFragment

object NavGraph {
    const val id = 1

    object Dest {
        const val login = 101
        const val picker = 102
        const val main = 103
        const val loadError = 104
        const val about = 105
        const val transaction = 106
    }

    object Action {
        const val toLogin = 1001
        const val toPicker = 1002
        const val toMain = 1003
        const val toLoadError = 1004
        const val toAbout = 1005
        const val toTransaction = 1006
    }
}

fun createNavGraph(navController: NavController) {
    navController.graph = navController.createGraph(NavGraph.id, NavGraph.Dest.login) {
        action(NavGraph.Action.toLogin) { destinationId = NavGraph.Dest.login }
        action(NavGraph.Action.toPicker) { destinationId = NavGraph.Dest.picker }
        action(NavGraph.Action.toMain) { destinationId = NavGraph.Dest.main }
        action(NavGraph.Action.toLoadError) { destinationId = NavGraph.Dest.loadError }
        action(NavGraph.Action.toAbout) { destinationId = NavGraph.Dest.about }
        action(NavGraph.Action.toTransaction) { destinationId = NavGraph.Dest.transaction }

        fragment<LoginFragment>(NavGraph.Dest.login)
        fragment<PickerFragment>(NavGraph.Dest.picker)
        fragment<MainFragment>(NavGraph.Dest.main)
        dialog<LoadErrorFragment>(NavGraph.Dest.loadError)
        dialog<AboutFragment>(NavGraph.Dest.about)
        fragment<TransactionFragment>(NavGraph.Dest.transaction)
    }
}