package com.pawegio.homebudget.main

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.compose.ui.res.colorResource
import com.pawegio.homebudget.Category
import com.pawegio.homebudget.R
import com.pawegio.homebudget.Subcategory
import com.pawegio.homebudget.databinding.SubcategoryViewBinding
import com.pawegio.homebudget.util.currencyValue
import splitties.systemservices.layoutInflater
import kotlin.math.roundToInt

class SubcategoryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var _binding: SubcategoryViewBinding? = null
    private val binding get() = _binding!!

    var subcategory: Subcategory? = null
        set(value) {
            field = value
            binding.subcategoryNameTextView.text = value?.name
            binding.subcategoryValueTextView.text = value?.run {
                "${actual.currencyValue} / ${planned.currencyValue}"
            }
            val progress = value?.takeIf { it.planned.toDouble() > 0.0 }?.run {
                actual.toDouble() / planned.toDouble()
            }
            val exceeded = progress == null || progress > 1
            val percent = (progress?.times(100.0))?.roundToInt()
            binding.subcategoryProgressView.text = percent?.let {
                resources.getString(R.string.percent, it)
            }
            binding.subcategoryProgressBar.setContent {
                RoundedProgressBar(
                    progress = progress?.toFloat()?.coerceAtMost(1f) ?: 1f,
                    color = when {
                        exceeded && value?.type == Category.Type.EXPENSES -> R.color.progressExceededNegative
                        exceeded && value?.type == Category.Type.INCOMES -> R.color.progressExceededPositive
                        else -> R.color.progress
                    }.let { colorResource(it) }
                )
            }
        }

    init {
        _binding = SubcategoryViewBinding.inflate(layoutInflater, this)
    }
}
