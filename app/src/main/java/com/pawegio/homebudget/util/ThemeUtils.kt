@file:Suppress("NOTHING_TO_INLINE")

package com.pawegio.homebudget.util

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

@ColorInt
inline fun View.colorAttr(@AttrRes resId: Int): Int = context.theme.colorAttr(resId)

@ColorInt
inline fun Resources.Theme.colorAttr(@AttrRes resId: Int): Int {
    val typedValue = TypedValue()
    if (!resolveAttribute(resId, typedValue, true)) {
        throw IllegalArgumentException("Failed to resolve attribute")
    }
    return typedValue.data
}

inline fun View.resourceAttr(@AttrRes resId: Int): Int = context.theme.resourceAttr(resId)

inline fun Resources.Theme.resourceAttr(@AttrRes resId: Int): Int {
    val typedValue = TypedValue()
    if (!resolveAttribute(resId, typedValue, true)) {
        throw IllegalArgumentException("Failed to resolve attribute")
    }
    return typedValue.resourceId
}
