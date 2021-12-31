package com.pawegio.homebudget.util

import android.content.Context
import android.widget.Toast

interface ToastNotifier {
    fun notify(textResId: Int)
    fun notify(textResId: Int, vararg args: Any)
}

class ToastNotifierImpl(private val context: Context) : ToastNotifier {

    private var toast: Toast? = null

    override fun notify(textResId: Int) {
        toast?.cancel()
        toast = Toast.makeText(context, textResId, Toast.LENGTH_SHORT).apply { show() }
    }

    override fun notify(textResId: Int, vararg args: Any) {
        toast?.cancel()
        toast = Toast.makeText(context, context.getString(textResId, args), Toast.LENGTH_SHORT).apply { show() }
    }
}
