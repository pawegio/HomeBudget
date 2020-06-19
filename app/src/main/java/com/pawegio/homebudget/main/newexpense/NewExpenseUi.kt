package com.pawegio.homebudget.main.newexpense

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.pawegio.homebudget.R
import splitties.views.dsl.core.*
import splitties.views.textResource

class NewExpenseUi(override val ctx: Context) : Ui {

    val dateTextView = textView()

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
        addView(NewExpenseUi(context).root)
    }
}
