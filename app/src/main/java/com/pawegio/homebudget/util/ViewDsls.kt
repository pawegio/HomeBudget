package com.pawegio.homebudget.util

import android.text.Html
import android.text.Spanned
import android.view.View
import androidx.annotation.StringRes
import splitties.resources.str

// Html

inline fun View.html(@StringRes stringResId: Int): Spanned =
    Html.fromHtml(str(stringResId), Html.FROM_HTML_MODE_LEGACY)
