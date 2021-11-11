package com.pawegio.homebudget.main.transaction

import android.content.Context
import android.icu.text.DecimalFormatSymbols
import android.os.Build
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.editorActions
import com.jakewharton.rxbinding3.widget.itemSelections
import com.jakewharton.rxbinding3.widget.textChanges
import com.jakewharton.rxrelay2.PublishRelay
import com.pawegio.homebudget.Category
import com.pawegio.homebudget.R
import com.pawegio.homebudget.Subcategory
import com.pawegio.homebudget.util.Optional
import com.pawegio.homebudget.util.colorAttr
import com.pawegio.homebudget.util.optional
import com.pawegio.homebudget.util.resourceAttr
import io.reactivex.Observable
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import splitties.dimensions.dip
import splitties.dimensions.dp
import splitties.views.*
import splitties.views.dsl.appcompat.toolbar
import splitties.views.dsl.constraintlayout.*
import splitties.views.dsl.coordinatorlayout.appBarLParams
import splitties.views.dsl.coordinatorlayout.coordinatorLayout
import splitties.views.dsl.core.*
import splitties.views.dsl.material.MaterialComponentsStyles
import splitties.views.dsl.material.appBarLayout
import splitties.views.dsl.material.contentScrollingWithAppBarLParams
import splitties.views.dsl.material.defaultLParams
import java.math.BigDecimal

class TransactionUi(override val ctx: Context) : Ui {

    var isInProgress: Boolean = false
        set(value) {
            field = value
            progressLayout.isVisible = value
            formLayout.isVisible = !value
        }

    var categories: List<Category> = emptyList()
        set(value) {
            field = value
            categorySpinner.adapter = ArrayAdapter(
                ctx,
                android.R.layout.simple_list_item_1,
                value.map { it.name }
            )
        }

    var subcategories: List<Subcategory> = emptyList()
        set(value) {
            field = value
            subcategorySpinner.adapter = ArrayAdapter(
                ctx,
                android.R.layout.simple_list_item_1,
                value.map { it.name }
            )
        }

    var date: LocalDate? = null
        set(value) {
            field = value
            dateTextView.text = value?.format(DateTimeFormatter.ofPattern("eeee, d MMMM yyyy"))
        }

    var amount: BigDecimal? = null
        set(value) {
            field = value
            amountEditText.setText("%.2f".format((value ?: BigDecimal.ZERO).toFloat()).replace('.', ','))
            amountEditText.setSelection(amountEditText.text.length)
        }

    private val backClicksRelay = PublishRelay.create<Unit>()
    private val noteChangesRelay = PublishRelay.create<Optional<String>>()
    private val dateClicksRelay = PublishRelay.create<LocalDate>()
    private val categorySelectionsRelay = PublishRelay.create<Category>()
    private val subcategorySelectionsRelay = PublishRelay.create<Subcategory>()
    private val amountChangesRelay = PublishRelay.create<Optional<BigDecimal>>()
    private val amountClicksRelay = PublishRelay.create<Unit>()
    private val addClicksRelay = PublishRelay.create<Unit>()

    val backClicks: Observable<Unit> = backClicksRelay
    val noteChanges: Observable<Optional<String>> = noteChangesRelay
    val dateClicks: Observable<LocalDate> = dateClicksRelay
    val categorySelections: Observable<Category> = categorySelectionsRelay
    val subcategorySelections: Observable<Subcategory> = subcategorySelectionsRelay
    val amountChanges: Observable<Optional<BigDecimal>> = amountChangesRelay
    val amountClicks: Observable<Unit> = amountClicksRelay
    val addClicks: Observable<Unit> = addClicksRelay

    private val materialStyles = MaterialComponentsStyles(ctx)

    private val appBar = appBarLayout(theme = R.style.AppTheme_AppBarOverlay) {
        add(toolbar {
            (ctx as? AppCompatActivity)?.setSupportActionBar(this)
            popupTheme = R.style.AppTheme_PopupOverlay
            setNavigationIcon(R.drawable.ic_close)
            setTitle(R.string.new_transaction)
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
        textChanges()
            .skipInitialValue()
            .map { it.toString().takeIf(String::isNotBlank).optional }
            .subscribe(noteChangesRelay)
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

    private val categorySpinner = spinner {
        verticalPadding = dip(8)
        itemSelections().skipInitialValue()
            .map { categories[it] }
            .doOnNext { subcategories = it.subcategories }
            .subscribe(categorySelectionsRelay)
    }

    private val subcategoryImageView = imageView {
        imageResource = R.drawable.ic_subcategory
        setColorFilter(colorAttr(R.attr.colorPrimary))
        padding = dip(8)
    }

    private val subcategorySpinner = spinner {
        verticalPadding = dip(8)
        itemSelections().skipInitialValue()
            .map { subcategories[it] }
            .subscribe(subcategorySelectionsRelay)
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
        val separator = DecimalFormatSymbols.getInstance().decimalSeparator
        val digitsKeyListener = DigitsKeyListener.getInstance("0123456789$separator")
        val amountInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        if (Build.BRAND == "google") {
            inputType = amountInputType
            keyListener = digitsKeyListener
        } else {
            keyListener = digitsKeyListener
            inputType = amountInputType
        }
        filters = arrayOf(AmountInputFilter(10, 2))
        textChanges()
            .skipInitialValue()
            .map {
                it.toString()
                    .replace(',', '.')
                    .takeIf(String::isNotEmpty)
                    ?.let(::BigDecimal).optional
            }
            .subscribe(amountChangesRelay)
        editorActions { it == EditorInfo.IME_ACTION_DONE }
            .map { }
            .subscribe(addClicksRelay)
    }

    private val calculatorButton = materialStyles.button.text {
        textResource = R.string.calculator
        clicks()
            .subscribe(amountClicksRelay)
    }

    private val currencyTextView = textView {
        textAppearance = R.style.TextAppearance_MaterialComponents_Body1
        textSize = 26f
        textResource = R.string.currency
    }

    private val addButton = materialStyles.button.filled {
        textResource = R.string.add
        clicks()
            .subscribe(addClicksRelay)
    }

    private val progressBar = view(::ProgressBar) {
        elevation = dp(4f)
    }

    private val formLayout = constraintLayout {
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
        add(subcategoryImageView, lParams(dip(40), dip(40)) {
            topToBottomOf(categoryImageView)
            startOfParent(dip(16))
            verticalMargin = dip(16)
        })
        add(subcategorySpinner, lParams(matchConstraints, wrapContent) {
            alignVerticallyOn(subcategoryImageView)
            startToEndOf(categoryImageView, dip(16))
            endOfParent(dip(16))
        })
        add(amountImageView, lParams(dip(40), dip(40)) {
            topToBottomOf(subcategoryImageView)
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
        add(calculatorButton, lParams(matchConstraints, wrapContent) {
            topToBottomOf(amountEditText)
            endToEndOf(amountEditText)
        })
        add(addButton, lParams(matchConstraints, dip(52)) {
            centerHorizontally(dip(16))
            topToBottomOf(calculatorButton, dip(8))
        })
    }

    private val progressLayout = constraintLayout {
        add(progressBar, lParams(wrapContent, wrapContent) {
            centerInParent()
        })
        isVisible = false
    }

    override val root: View = coordinatorLayout {
        add(appBar, appBarLParams())
        add(formLayout, contentScrollingWithAppBarLParams())
        add(progressLayout, contentScrollingWithAppBarLParams())
    }
}

@Suppress("unused")
private class TransactionUiPreview : FrameLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        addView(TransactionUi(context).apply {
            date = LocalDate.parse("2020-07-21")
        }.root)
    }
}
