@file:Suppress("unused")

package com.pawegio.homebudget

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import androidx.navigation.NavController

interface Navigator {
    fun popBackStack()
    fun navigate(destinationId: Int)
}

class NavigatorImpl(
    private val application: Application,
    private val getNavController: () -> NavController?
) : Navigator, LifecycleObserver {

    override fun navigate(destinationId: Int) {
        getNavController()?.run { navigate(destinationId) }
    }

    override fun popBackStack() {
        getNavController()?.run { popBackStack() }
    }
}
