package com.pawegio.homebudget.util

import android.content.Context
import android.widget.Toast

interface ToastNotifier {
    fun notify(textResId: Int)
    fun notify(textResId: Int, arg: Any)
}

class ToastNotifierImpl(private val context: Context) : ToastNotifier {

    private var toast: Toast? = null

    override fun notify(textResId: Int) {
        toast?.cancel()
        toast = Toast.makeText(context, textResId, Toast.LENGTH_SHORT).apply { show() }
    }

    override fun notify(textResId: Int, arg: Any) {
        toast?.cancel()
        val message = context.getString(textResId, arg.toString())
        toast = Toast.makeText(context, message, Toast.LENGTH_LONG).apply { show() }
    }
}
