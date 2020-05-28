package com.pawegio.homebudget.about

import android.content.Context
import android.graphics.Typeface
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.pawegio.homebudget.BuildConfig
import com.pawegio.homebudget.R
import com.pawegio.homebudget.util.html
import splitties.dimensions.dip
import splitties.resources.txt
import splitties.views.dsl.core.*

class AboutUi(override val ctx: Context) : Ui {

    override val root: View = verticalLayout {
        addInfo(txt(R.string.version), BuildConfig.VERSION_NAME)
        addInfo(txt(R.string.author), txt(R.string.me))
        addInfo(txt(R.string.app_icon), html(R.string.app_icon_author))
    }

    private fun LinearLayout.addInfo(label: CharSequence, value: CharSequence) {
        add(textView {
            text = label
            typeface = Typeface.DEFAULT_BOLD
        }, lParams {
            topMargin = dip(16)
            startMargin = dip(48)
        })
        add(textView {
            text = value
            movementMethod = LinkMovementMethod.getInstance()
            linksClickable = true
        }, lParams {
            startMargin = dip(48)
        })
    }

}

@Suppress("unused")
private class AboutUiPreview : FrameLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        addView(AboutUi(context).root)
    }
}
