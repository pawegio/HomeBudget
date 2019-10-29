package com.pawegio.homebudget.main

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.pawegio.homebudget.R
import com.pawegio.homebudget.Subcategory
import com.pawegio.homebudget.util.currencyValue
import kotlinx.android.synthetic.main.subcategory_view.view.*

class SubcategoryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var subcategory: Subcategory? = null
        set(value) {
            field = value
            subcategoryNameTextView.text = value?.name
            subcategoryValueTextView.text = value?.run {
                "${actual.currencyValue} / ${planned.currencyValue}"
            }
            var exceeded = false
            val progress = value?.takeIf { it.planned.toDouble() > 0.0 }?.run {
                exceeded = actual > planned
                (100 * (actual / planned).toDouble()).toInt()
            } ?: 0
            subcategoryProgressView.text = resources.getString(R.string.percent, progress)
            subcategoryProgressBar.setProgressColors(
                context.getColor(R.color.progressBackground),
                context.getColor(if (exceeded) R.color.progressExceeded else R.color.progress)
            )
            subcategoryProgressBar.progress = progress
        }

    init {
        inflate(context, R.layout.subcategory_view, this)
    }
}
