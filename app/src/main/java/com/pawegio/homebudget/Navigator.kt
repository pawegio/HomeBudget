@file:Suppress("unused")

package com.pawegio.homebudget

import android.app.Application
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleObserver
import androidx.navigation.NavController
import androidx.navigation.navOptions

interface Navigator {
    fun popBackStack()
    fun navigate(destinationId: Int)
    fun navigate(destinationId: Int, argument: Pair<String, Any?>)
}

class NavigatorImpl(
    private val application: Application,
    private val getNavController: () -> NavController?
) : Navigator, LifecycleObserver {

    override fun navigate(destinationId: Int) {
        getNavController()?.run { navigate(destinationId) }
    }

    override fun navigate(destinationId: Int, argument: Pair<String, Any?>) {
        getNavController()?.run { navigate(destinationId, bundleOf(argument)) }
    }

    override fun popBackStack() {
        getNavController()?.run { popBackStack() }
    }
}
