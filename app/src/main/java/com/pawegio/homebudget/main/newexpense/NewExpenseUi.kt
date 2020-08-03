package com.pawegio.homebudget.main.newexpense

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.pawegio.homebudget.R
import com.pawegio.homebudget.util.colorAttr
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import splitties.dimensions.dip
import splitties.views.dsl.appcompat.toolbar
import splitties.views.dsl.constraintlayout.*
import splitties.views.dsl.coordinatorlayout.appBarLParams
import splitties.views.dsl.coordinatorlayout.coordinatorLayout
import splitties.views.dsl.core.*
import splitties.views.dsl.material.appBarLayout
import splitties.views.dsl.material.contentScrollingWithAppBarLParams
import splitties.views.dsl.material.defaultLParams
import splitties.views.imageResource
import splitties.views.textAppearance
import splitties.views.textResource
import splitties.views.verticalPadding

class NewExpenseUi(override val ctx: Context) : Ui {

    var date: LocalDate? = null
        set(value) {
            field = value
            dateTextView.text = value?.format(DateTimeFormatter.ofPattern("eeee, d MMMM yyyy"))
        }

    var onBackClick: (() -> Unit)? = null

    private val appBar = appBarLayout(theme = R.style.AppTheme_AppBarOverlay) {
        add(toolbar {
            (ctx as? AppCompatActivity)?.setSupportActionBar(this)
            popupTheme = R.style.AppTheme_PopupOverlay
            setNavigationIcon(R.drawable.ic_close)
            setTitle(R.string.new_expense)
            setNavigationOnClickListener { onBackClick?.invoke() }
        }, defaultLParams(height = matchParent))
    }

    private val dateImageView = imageView {
        imageResource = R.drawable.ic_date
        setColorFilter(colorAttr(R.attr.colorAccent))
    }

    private val dateTextView = textView {
        textAppearance = R.style.TextAppearance_MaterialComponents_Body1
        verticalPadding = dip(8)
    }

    val categorySpinner = spinner {
        verticalPadding = dip(8)
    }

    val addExpenseButton = button {
        textResource = R.string.add
    }

    override val root: View = coordinatorLayout {
        add(appBar, appBarLParams())
        add(constraintLayout {
            add(dateImageView, lParams(wrapContent, wrapContent) {
                topOfParent(dip(8))
                startOfParent(dip(16))
                verticalPadding = dip(16)
            })
            add(dateTextView, lParams(matchConstraints, wrapContent) {
                alignVerticallyOn(dateImageView)
                startToEndOf(dateImageView, dip(16))
                endOfParent(dip(16))
            })
            add(categorySpinner, lParams(matchConstraints, wrapContent) {
                topToBottomOf(dateTextView)
                centerHorizontally(dip(16))
            })
            add(addExpenseButton, lParams(wrapContent, wrapContent) {
                topToBottomOf(categorySpinner)
                startOfParent(dip(16))
            })
        }, contentScrollingWithAppBarLParams())
    }
}

@Suppress("unused")
private class NewExpenseUiPreview : FrameLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        addView(NewExpenseUi(context).apply {
            date = LocalDate.parse("2020-07-21")
        }.root)
    }
}
