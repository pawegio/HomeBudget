package com.pawegio.homebudget

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

var currentActivity: FragmentActivity? = null

@Suppress("unused")
object CurrentActivityObserver : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create(owner: LifecycleOwner) {
        currentActivity = owner as? FragmentActivity
    }
}
