package com.pawegio.homebudget.picker

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.pawegio.homebudget.R
import splitties.dimensions.dip
import splitties.views.*
import splitties.views.dsl.constraintlayout.*
import splitties.views.dsl.core.*
import splitties.views.dsl.material.MaterialComponentsStyles

class PickerUi(override val ctx: Context) : Ui {

    private val materialStyles = MaterialComponentsStyles(ctx)

    @Suppress("EXPERIMENTAL_API_USAGE")
    val spreadsheetUrlEditText = editText {
        setHint(R.string.spreadsheet_url_hint)
        inputType = InputType.uri.value
    }

    val connectSpreadsheetButton = materialStyles.button.filled {
        textResource = R.string.connect_spreadsheet
        isEnabled = false
    }

    val howToButton = materialStyles.button.text {
        textResource = R.string.create_spreadsheet
    }

    val templateSpinner = spinner()

    private val copyLinkImageView = imageView {
        imageResource = R.drawable.copy_link
    }

    private val enterSpreadsheetLabelView = textView {
        textResource = R.string.enter_spreadsheet_url
        textAppearance = R.style.TextAppearance_MaterialComponents_Headline6
        gravity = gravityCenter
    }

    private val templateLabel = textView {
        textResource = R.string.spreadsheet_template
        textAppearance = R.style.TextAppearance_MaterialComponents_Body1
        gravity = gravityCenter
    }

    override val root: View = constraintLayout {
        add(copyLinkImageView, lParams {
            horizontalMargin = dip(48)
            verticalMargin = dip(16)
            topOfParent()
            bottomToTopOf(enterSpreadsheetLabelView)
            centerHorizontally()
        })
        add(enterSpreadsheetLabelView, lParams(wrapContent, wrapContent) {
            horizontalMargin = dip(16)
            topToBottomOf(copyLinkImageView)
            centerHorizontally()
            bottomToTopOf(spreadsheetUrlEditText)
        })
        add(spreadsheetUrlEditText, lParams(matchConstraints, wrapContent) {
            horizontalMargin = dip(16)
            topToBottomOf(enterSpreadsheetLabelView, dip(8))
            centerHorizontally()
            bottomToTopOf(connectSpreadsheetButton, dip(48))
        })
        add(templateLabel, lParams(wrapContent, wrapContent) {
            verticalMargin = dip(8)
            topToBottomOf(spreadsheetUrlEditText)
            startOfParent()
            endToStartOf(templateSpinner, dip(16))
            bottomToTopOf(connectSpreadsheetButton)
        })
        add(templateSpinner, lParams(wrapContent, wrapContent) {
            alignVerticallyOn(templateLabel)
            startToEndOf(templateLabel)
            endOfParent()
        })
        add(connectSpreadsheetButton, lParams(wrapContent, wrapContent) {
            bottomMargin = dip(16)
            topToBottomOf(spreadsheetUrlEditText)
            centerHorizontally()
            bottomToTopOf(howToButton)
        })
        add(howToButton, lParams(wrapContent, wrapContent) {
            topToBottomOf(connectSpreadsheetButton)
            centerHorizontally()
            bottomOfParent(dip(36))
        })
    }
}

@Suppress("unused")
private class PickerUiPreview : FrameLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        addView(PickerUi(context).root)
    }
}
