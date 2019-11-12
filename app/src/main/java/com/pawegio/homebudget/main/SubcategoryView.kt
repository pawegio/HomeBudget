package com.pawegio.homebudget.main

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.pawegio.homebudget.Category
import com.pawegio.homebudget.R
import com.pawegio.homebudget.Subcategory
import com.pawegio.homebudget.util.currencyValue
import kotlinx.android.synthetic.main.subcategory_view.view.*
import kotlin.math.roundToInt

class SubcategoryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var subcategory: Subcategory? = null
        set(value) {
            field = value
            subcategoryNameTextView.text = value?.name
            subcategoryValueTextView.text = value?.run {
                "${actual.currencyValue} / ${planned.currencyValue}"
            }
            val progress = value?.takeIf { it.planned.toDouble() > 0.0 }?.run {
                (100.0 * actual.toDouble() / planned.toDouble()).roundToInt()
            }
            val exceeded = progress == null || progress > 100
            subcategoryProgressView.text = progress?.let {
                resources.getString(R.string.percent, it)
            }
            subcategoryProgressBar.setProgressColors(
                context.getColor(R.color.progressBackground),
                context.getColor(
                    when {
                        exceeded && value?.type == Category.Type.EXPENSES -> R.color.progressExceededNegative
                        exceeded && value?.type == Category.Type.INCOMES -> R.color.progressExceededPositive
                        else -> R.color.progress
                    }
                )
            )
            subcategoryProgressBar.progress = progress ?: 100
        }

    init {
        inflate(context, R.layout.subcategory_view, this)
    }
}
