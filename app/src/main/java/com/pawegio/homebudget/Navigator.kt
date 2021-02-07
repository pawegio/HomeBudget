@file:Suppress("unused")

package com.pawegio.homebudget

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.NavController

interface Navigator {
    fun popBackStack()
    fun navigate(destinationId: Int)
}

class NavigatorImpl(
    private val application: Application,
    private val getNavController: () -> NavController?
) : Navigator, LifecycleObserver {

    private val pendingActions = mutableListOf<() -> Any?>()

    private var isAppInForeground = false

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppInForeground() {
        isAppInForeground = true
        pendingActions.run {
            forEach { it.invoke() }
            clear()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppInBackground() {
        isAppInForeground = false
    }

    override fun navigate(destinationId: Int) = tryToInvoke {
        getNavController()?.run { navigate(destinationId) }
    }

    override fun popBackStack() = tryToInvoke {
        getNavController()?.run { popBackStack() }
    }

    private fun tryToInvoke(action: () -> Any?) {
        if (isAppInForeground) {
            action.invoke()
        } else {
            pendingActions.add(action)
        }
    }
}
