package com.pawegio.homebudget.main.newexpense

import android.content.Context
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.jakewharton.rxrelay2.PublishRelay
import com.pawegio.homebudget.R
import com.pawegio.homebudget.util.colorAttr
import com.pawegio.homebudget.util.resourceAttr
import io.reactivex.Observable
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
import java.math.BigDecimal

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

    var amount: BigDecimal?
        get() = BigDecimal(amountEditText.editableText.toString())
        set(value) {
            amountEditText.setText(value.toString())
        }

    private val backClicksRelay = PublishRelay.create<Unit>()
    private val dateClicksRelay = PublishRelay.create<LocalDate>()
    private val amountChangesRelay = PublishRelay.create<BigDecimal>()

    val backClicks: Observable<Unit> = backClicksRelay
    val dateClicks: Observable<LocalDate> = dateClicksRelay
    val amountChanges: Observable<BigDecimal> = amountChangesRelay

    private val appBar = appBarLayout(theme = R.style.AppTheme_AppBarOverlay) {
        add(toolbar {
            (ctx as? AppCompatActivity)?.setSupportActionBar(this)
            popupTheme = R.style.AppTheme_PopupOverlay
            setNavigationIcon(R.drawable.ic_close)
            setTitle(R.string.new_expense)
            setNavigationOnClickListener { backClicksRelay.accept(Unit) }
        }, defaultLParams(height = matchParent))
    }

    private val noteImageView = imageView {
        imageResource = R.drawable.ic_note
        setColorFilter(colorAttr(R.attr.colorPrimary))
        padding = dip(8)
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
        padding = dip(8)
    }

    private val dateTextView = textView {
        textAppearance = R.style.TextAppearance_MaterialComponents_Body1
        setBackgroundResource(resourceAttr(R.attr.selectableItemBackground))
        padding = dip(8)
        clicks()
            .map { date }
            .subscribe(dateClicksRelay)
    }

    private val categoryImageView = imageView {
        imageResource = R.drawable.ic_category
        setColorFilter(colorAttr(R.attr.colorPrimary))
        padding = dip(8)
    }

    val categorySpinner = spinner {
        verticalPadding = dip(8)
    }

    private val amountImageView = imageView {
        imageResource = R.drawable.ic_amount
        setColorFilter(colorAttr(R.attr.colorPrimary))
        padding = dip(8)
    }

    private val amountEditText = editText {
        isSingleLine = true
        gravity = gravityEnd
        setHint(R.string.amount_hint)
        textSize = 26f
        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        keyListener = DigitsKeyListener.getInstance("0123456789,")
        filters = arrayOf(AmountInputFilter(10, 2))
        textChanges()
            .skipInitialValue()
            .map { BigDecimal(it.toString().replace(',', '.')) }
            .subscribe(amountChangesRelay)
    }

    private val currencyTextView = textView {
        textAppearance = R.style.TextAppearance_MaterialComponents_Body1
        textSize = 26f
        textResource = R.string.currency
    }

    override val root: View = coordinatorLayout {
        add(appBar, appBarLParams())
        add(constraintLayout {
            backgroundColor = colorAttr(R.attr.colorSurface)
            add(noteImageView, lParams(dip(40), dip(40)) {
                topOfParent(dip(8))
                startOfParent(dip(16))
                verticalMargin = dip(16)
            })
            add(noteEditText, lParams(matchConstraints, wrapContent) {
                alignVerticallyOn(noteImageView)
                startToEndOf(noteImageView, dip(16))
                endOfParent(dip(16))
            })
            add(dateImageView, lParams(dip(40), dip(40)) {
                topToBottomOf(noteImageView)
                startOfParent(dip(16))
                verticalMargin = dip(16)
            })
            add(dateTextView, lParams(matchConstraints, wrapContent) {
                alignVerticallyOn(dateImageView)
                startToEndOf(dateImageView, dip(16))
                endOfParent(dip(16))
            })
            add(categoryImageView, lParams(dip(40), dip(40)) {
                topToBottomOf(dateImageView)
                startOfParent(dip(16))
                verticalMargin = dip(16)
            })
            add(categorySpinner, lParams(matchConstraints, wrapContent) {
                alignVerticallyOn(categoryImageView)
                startToEndOf(categoryImageView, dip(16))
                endOfParent(dip(16))
            })
            add(amountImageView, lParams(dip(40), dip(40)) {
                topToBottomOf(categoryImageView)
                startOfParent(dip(16))
                topMargin = dip(36)
            })
            add(amountEditText, lParams(matchConstraints, wrapContent) {
                alignVerticallyOn(amountImageView)
                startToEndOf(amountImageView, dip(16))
                endToStartOf(currencyTextView, dip(4))
            })
            add(currencyTextView, lParams(wrapContent, wrapContent) {
                alignVerticallyOn(amountImageView)
                endOfParent(dip(16))
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
