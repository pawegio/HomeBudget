package com.pawegio.homebudget

import androidx.navigation.NavController

interface Navigator {
    fun restart(graphId: Int)
    fun popBackStack()
    fun navigate(destinationId: Int)
}

class NavigatorImpl(private val getNavController: () -> NavController?) : Navigator {

    override fun restart(graphId: Int) {
        getNavController()?.setGraph(graphId)
    }

    override fun navigate(destinationId: Int) {
        getNavController()?.navigate(destinationId)
    }

    override fun popBackStack() {
        getNavController()?.popBackStack()
    }
}
