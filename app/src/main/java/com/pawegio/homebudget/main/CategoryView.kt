package com.pawegio.homebudget.main

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.pawegio.homebudget.Category
import com.pawegio.homebudget.R
import com.pawegio.homebudget.databinding.CategoryViewBinding
import com.pawegio.homebudget.util.currencyValue
import splitties.systemservices.layoutInflater
import kotlin.math.roundToInt

class CategoryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var _binding: CategoryViewBinding? = null
    private val binding get() = _binding!!

    var category: Category? = null
        set(value) {
            field = value
            binding.categoryNameTextView.text = value?.name
            binding.categoryValueTextView.text = value?.run {
                "${actual.currencyValue} / ${planned.currencyValue}"
            }
            val progress = value?.takeIf { it.planned.toDouble() > 0.0 }?.run {
                (100.0 * actual.toDouble() / planned.toDouble()).roundToInt()
            }
            binding.categoryProgressView.text = progress?.let { resources.getString(R.string.percent, it) }
            binding.subcategoriesLayout.removeAllViews()
            value?.subcategories
                ?.filter { it.actual.toDouble() > 0.0 || it.planned.toDouble() > 0.0 }
                ?.forEach { subcategory ->
                    binding.subcategoriesLayout.addView(SubcategoryView(context).apply {
                        this.subcategory = subcategory
                    })
                }
        }

    init {
        _binding = CategoryViewBinding.inflate(layoutInflater, this)
    }
}
