package com.pawegio.homebudget.util

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import androidx.cardview.widget.CardView
import splitties.views.dsl.core.NO_THEME
import splitties.views.dsl.core.Ui
import splitties.views.dsl.core.view
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// CardView

inline fun Context.cardView(
    @IdRes id: Int = View.NO_ID,
    @StyleRes theme: Int = NO_THEME,
    initView: CardView.() -> Unit = {}
): CardView {
    contract { callsInPlace(initView, InvocationKind.EXACTLY_ONCE) }
    return view(id, theme, initView)
}

inline fun View.cardView(
    @IdRes id: Int = View.NO_ID,
    @StyleRes theme: Int = NO_THEME,
    initView: CardView.() -> Unit = {}
): CardView {
    contract { callsInPlace(initView, InvocationKind.EXACTLY_ONCE) }
    return context.cardView(id, theme, initView)
}

inline fun Ui.cardView(
    @IdRes id: Int = View.NO_ID,
    @StyleRes theme: Int = NO_THEME,
    initView: CardView.() -> Unit = {}
): CardView {
    contract { callsInPlace(initView, InvocationKind.EXACTLY_ONCE) }
    return ctx.cardView(id, theme, initView)
}
