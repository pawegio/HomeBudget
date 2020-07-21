package com.pawegio.homebudget.main.newexpense

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.pawegio.homebudget.R
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import splitties.views.dsl.core.*
import splitties.views.textAppearance
import splitties.views.textResource

class NewExpenseUi(override val ctx: Context) : Ui {

    var date: LocalDate? = null
        set(value) {
            field = value
            dateTextView.text = value?.format(DateTimeFormatter.ofPattern("eeee, d MMMM yyyy"))
        }

    private val dateTextView = textView {
        textAppearance = R.style.TextAppearance_MaterialComponents_Body1
    }

    val categorySpinner = spinner()

    val addExpenseButton = button {
        textResource = R.string.add
    }

    override val root: View = verticalLayout {
        add(dateTextView, lParams())
        add(categorySpinner, lParams())
        add(addExpenseButton, lParams())
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
