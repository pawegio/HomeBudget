package com.pawegio.homebudget.main

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.card.MaterialCardView
import com.pawegio.homebudget.Category
import com.pawegio.homebudget.R
import com.pawegio.homebudget.Subcategory
import com.pawegio.homebudget.util.colorAttr
import splitties.dimensions.dip
import splitties.dimensions.dp
import splitties.views.*
import splitties.views.dsl.constraintlayout.*
import splitties.views.dsl.core.*
import splitties.views.dsl.material.floatingActionButton
import splitties.views.dsl.material.materialCardView

class MainUi(override val ctx: Context) : Ui {

    var month: String
        get() = monthHeaderView.text.toString()
        set(value) {
            monthHeaderView.text = value
        }

    var incomes: List<Subcategory> = emptyList()
        set(value) {
            field = value
            allIncomesLayout.removeAllViews()
            value.filter { it.actual.toDouble() > 0.0 || it.planned.toDouble() > 0.0 }
                .forEach { subcategory ->
                    allIncomesLayout.addView(SubcategoryView(ctx).apply {
                        this.subcategory = subcategory
                    })
                }
        }

    var expenses: List<Category> = emptyList()
        set(value) {
            field = value
            allExpensesLayout.removeAllViews()
            value.forEach { category ->
                allExpensesLayout.addView(CategoryView(ctx).apply {
                    this.category = category
                })
            }
        }

    var isLoading: Boolean = false
        set(value) {
            field = value
            if (value) {
                incomes = emptyList()
                expenses = emptyList()
            }
            incomesHeaderView.isVisible = !value
            expensesHeaderView.isVisible = !value
            monthlyBudgetProgressBar.isVisible = value
        }

    private val headerView = view(::View) {
        backgroundColor = colorAttr(R.attr.colorPrimary)
    }

    val monthHeaderView = textView {
        textAppearance = R.style.TextAppearance_MaterialComponents_Headline5
        setTextColor(colorAttr(R.attr.colorOnPrimary))
        isSingleLine = true
        gravity = gravityStart
    }

    val moreButton = imageButton {
        imageResource = R.drawable.ic_more
        setColorFilter(colorAttr(R.attr.colorOnPrimary))
    }

    val openSpreadsheetButton = imageButton {
        imageResource = R.drawable.ic_open_spreadsheet
        setColorFilter(colorAttr(R.attr.colorOnPrimary))
    }

    val nextMonthButton = imageButton {
        imageResource = R.drawable.ic_next
        setColorFilter(colorAttr(R.attr.colorOnPrimary))
    }

    val prevMonthButton = imageButton {
        imageResource = R.drawable.ic_prev
        setColorFilter(colorAttr(R.attr.colorOnPrimary))
    }

    val monthSummarySurface = inflate<MaterialCardView>(R.layout.month_summary_surface)

    private val incomesHeaderView = textView {
        textAppearance = R.style.TextAppearance_MaterialComponents_Headline6
        textResource = R.string.incomes
    }

    private val allIncomesLayout = verticalLayout {
        layoutTransition = LayoutTransition()
    }

    private val allIncomesCardView = materialCardView {
        add(allIncomesLayout, lParams(matchParent, wrapContent) {
            topMargin = dip(8)
            bottomMargin = dip(8)
            minimumHeight = dip(48)
        })
    }

    private val expensesHeaderView = textView {
        textAppearance = R.style.TextAppearance_MaterialComponents_Headline6
        textResource = R.string.expenses
    }

    private val allExpensesLayout = verticalLayout {
        layoutTransition = LayoutTransition()
    }

    private val monthlyBudgetProgressBar = view(::ProgressBar) {
        elevation = dp(4f)
    }

    val floatingActionButton = floatingActionButton {
        imageResource = R.drawable.ic_add
        isVisible = false
    }

    private val constraintLayout = constraintLayout {
        add(headerView, lParams(matchConstraints, dip(100)) {
            topOfParent()
            centerHorizontally()
        })
        add(monthHeaderView, lParams(matchConstraints, wrapContent) {
            topOfParent(dip(8))
            startOfParent(dip(16))
            endToStartOf(prevMonthButton)
        })
        add(moreButton, lParams(wrapContent, wrapContent) {
            alignVerticallyOn(monthHeaderView)
            endOfParent(dip(8))
        })
        add(openSpreadsheetButton, lParams(wrapContent, wrapContent) {
            alignVerticallyOn(monthHeaderView)
            endToStartOf(moreButton, dip(8))
        })
        add(nextMonthButton, lParams(wrapContent, wrapContent) {
            alignVerticallyOn(monthHeaderView)
            endToStartOf(openSpreadsheetButton, dip(8))
        })
        add(prevMonthButton, lParams(wrapContent, wrapContent) {
            alignVerticallyOn(monthHeaderView)
            endToStartOf(nextMonthButton, dip(8))
        })
        add(monthSummarySurface, lParams(matchConstraints, wrapContent) {
            margin = dip(16)
            centerHorizontally()
            topToBottomOf(monthHeaderView)
        })
        add(incomesHeaderView, lParams(wrapContent, wrapContent) {
            margin = dip(16)
            topToBottomOf(monthSummarySurface)
            startOfParent()
        })
        add(allIncomesCardView, lParams(matchConstraints, wrapContent) {
            bottomMargin = dip(8)
            topToBottomOf(incomesHeaderView, dip(8))
            centerHorizontally(dip(16))
        })
        add(expensesHeaderView, lParams(wrapContent, wrapContent) {
            margin = dip(16)
            topToBottomOf(allIncomesCardView)
            startOfParent()
        })
        add(allExpensesLayout, lParams(matchConstraints, wrapContent) {
            centerHorizontally()
            topToBottomOf(expensesHeaderView)
        })
        add(monthlyBudgetProgressBar, lParams(wrapContent, wrapContent) {
            alignHorizontallyOn(allIncomesCardView)
            alignVerticallyOn(allIncomesCardView)
        })
    }

    val swipeRefreshLayout = view(::SwipeRefreshLayout) {
        addView(view(::NestedScrollView) {
            add(constraintLayout, lParams(width = matchParent, height = height) {
                bottomMargin = dip(16)
                layoutTransition = LayoutTransition()
            })
        })
    }

    override val root: View = frameLayout {
        add(swipeRefreshLayout, lParams(matchParent, matchParent))
        add(floatingActionButton, lParams {
            gravity = gravityBottomEnd
            margin = dip(16)
        })
    }
}

@Suppress("unused")
private class MainUiPreview : FrameLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        addView(MainUi(context).also { ui ->
            ui.month = "Kwiecień"
        }.root)
    }
}
