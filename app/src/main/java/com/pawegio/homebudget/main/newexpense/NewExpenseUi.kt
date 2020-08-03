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
import splitties.views.*
import splitties.views.dsl.appcompat.toolbar
import splitties.views.dsl.constraintlayout.*
import splitties.views.dsl.coordinatorlayout.appBarLParams
import splitties.views.dsl.coordinatorlayout.coordinatorLayout
import splitties.views.dsl.core.*
import splitties.views.dsl.material.appBarLayout
import splitties.views.dsl.material.contentScrollingWithAppBarLParams
import splitties.views.dsl.material.defaultLParams

class NewExpenseUi(override val ctx: Context) : Ui {

    var note: String?
        get() = noteEditText.editableText.toString()
        set(value) {
            noteEditText.setText(value)
        }

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

    private val noteImageView = imageView {
        imageResource = R.drawable.ic_note
        setColorFilter(colorAttr(R.attr.colorPrimary))
        verticalPadding = dip(16)
    }

    private val noteEditText = editText {
        textAppearance = R.style.TextAppearance_MaterialComponents_Headline5
        isSingleLine = true
        setHint(R.string.note_hint)
        verticalPadding = dip(8)
    }

    private val dateImageView = imageView {
        imageResource = R.drawable.ic_date
        setColorFilter(colorAttr(R.attr.colorPrimary))
        horizontalPadding = dip(2)
        verticalPadding = dip(16)
    }

    private val dateTextView = textView {
        textAppearance = R.style.TextAppearance_MaterialComponents_Body1
        verticalPadding = dip(8)
    }

    private val categoryImageView = imageView {
        imageResource = R.drawable.ic_category
        setColorFilter(colorAttr(R.attr.colorPrimary))
        verticalPadding = dip(16)
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
            add(noteImageView, lParams(wrapContent, wrapContent) {
                topOfParent(dip(8))
                startOfParent(dip(16))
            })
            add(noteEditText, lParams(matchConstraints, wrapContent) {
                alignVerticallyOn(noteImageView)
                startToEndOf(noteImageView, dip(16))
                endOfParent(dip(16))
            })
            add(dateImageView, lParams(wrapContent, wrapContent) {
                topToBottomOf(noteImageView)
                startOfParent(dip(16))
            })
            add(dateTextView, lParams(matchConstraints, wrapContent) {
                alignVerticallyOn(dateImageView)
                startToEndOf(dateImageView, dip(16))
                endOfParent(dip(16))
            })
            add(categoryImageView, lParams(wrapContent, wrapContent) {
                topToBottomOf(dateImageView)
                startOfParent(dip(16))
            })
            add(categorySpinner, lParams(matchConstraints, wrapContent) {
                alignVerticallyOn(categoryImageView)
                startToEndOf(categoryImageView, dip(16))
                endOfParent(dip(16))
            })
            add(addExpenseButton, lParams(wrapContent, wrapContent) {
                topToBottomOf(categoryImageView)
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
