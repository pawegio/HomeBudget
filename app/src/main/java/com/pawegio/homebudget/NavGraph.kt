package com.pawegio.homebudget

import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.createGraph
import androidx.navigation.fragment.dialog
import androidx.navigation.fragment.fragment
import com.pawegio.homebudget.about.AboutFragment
import com.pawegio.homebudget.faq.FaqFragment
import com.pawegio.homebudget.login.LoginFragment
import com.pawegio.homebudget.main.LoadErrorFragment
import com.pawegio.homebudget.main.MainFragment
import com.pawegio.homebudget.main.transaction.TransactionFragment
import com.pawegio.homebudget.picker.PickerFragment
import com.pawegio.homebudget.start.StartFragment

object NavGraph {
    const val id = 1

    object Dest {
        const val start = 100
        const val login = 101
        const val picker = 102
        const val main = 103
        const val loadError = 104
        const val about = 105
        const val transaction = 106
        const val faq = 107
    }

    object Action {
        const val toLogin = 1001
        const val toPicker = 1002
        const val toMain = 1003
        const val toLoadError = 1004
        const val toAbout = 1005
        const val toTransaction = 1006
        const val toFaq = 1007
    }

    object Args {
        const val monthlyBudget = "monthly_budget"
    }
}

fun createNavGraph(navController: NavController) {
    navController.graph = navController.createGraph(NavGraph.id, NavGraph.Dest.start) {
        action(NavGraph.Action.toLogin) { destinationId = NavGraph.Dest.login }
        action(NavGraph.Action.toPicker) { destinationId = NavGraph.Dest.picker }
        action(NavGraph.Action.toMain) { destinationId = NavGraph.Dest.main }
        action(NavGraph.Action.toLoadError) { destinationId = NavGraph.Dest.loadError }
        action(NavGraph.Action.toAbout) { destinationId = NavGraph.Dest.about }
        action(NavGraph.Action.toTransaction) { destinationId = NavGraph.Dest.transaction }
        action(NavGraph.Action.toFaq) { destinationId = NavGraph.Dest.faq }

        fragment<StartFragment>(NavGraph.Dest.start)
        fragment<LoginFragment>(NavGraph.Dest.login)
        fragment<PickerFragment>(NavGraph.Dest.picker)
        fragment<MainFragment>(NavGraph.Dest.main)
        dialog<LoadErrorFragment>(NavGraph.Dest.loadError)
        dialog<AboutFragment>(NavGraph.Dest.about)
        fragment<TransactionFragment>(NavGraph.Dest.transaction) {
            argument(NavGraph.Args.monthlyBudget) {
                type = NavType.ParcelableType(MonthlyBudget::class.java)
            }
        }
        fragment<FaqFragment>(NavGraph.Dest.faq)
    }
}
