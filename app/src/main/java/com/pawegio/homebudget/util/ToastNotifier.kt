package com.pawegio.homebudget.util

import android.content.Context
import android.widget.Toast

interface ToastNotifier {
    fun notify(textResId: Int)
}

class ToastNotifierImpl(private val context: Context) : ToastNotifier {

    override fun notify(textResId: Int) {
        Toast.makeText(context, textResId, Toast.LENGTH_SHORT).show()
    }
}
