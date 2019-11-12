package com.pawegio.homebudget.main

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.pawegio.homebudget.Category
import com.pawegio.homebudget.R
import com.pawegio.homebudget.util.currencyValue
import kotlinx.android.synthetic.main.category_view.view.*
import kotlin.math.roundToInt

class CategoryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var category: Category? = null
        set(value) {
            field = value
            categoryNameTextView.text = value?.name
            categoryValueTextView.text = value?.run {
                "${actual.currencyValue} / ${planned.currencyValue}"
            }
            val progress = value?.takeIf { it.planned.toDouble() > 0.0 }?.run {
                (100.0 * actual.toDouble() / planned.toDouble()).roundToInt()
            }
            categoryProgressView.text = progress?.let { resources.getString(R.string.percent, it) }
            subcategoriesLayout.removeAllViews()
            value?.subcategories
                ?.filter { it.actual.toDouble() > 0.0 || it.planned.toDouble() > 0.0 }
                ?.forEach { subcategory ->
                    subcategoriesLayout.addView(SubcategoryView(context).apply {
                        this.subcategory = subcategory
                    })
                }
        }

    init {
        inflate(context, R.layout.category_view, this)
    }
}
