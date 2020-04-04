package com.pawegio.homebudget.login

import android.content.Context
import android.graphics.Typeface
import android.text.Html
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.SignInButton.SIZE_WIDE
import com.pawegio.homebudget.R
import splitties.dimensions.dip
import splitties.resources.str
import splitties.views.dsl.constraintlayout.*
import splitties.views.dsl.core.*
import splitties.views.gravityCenter
import splitties.views.textAppearance
import splitties.views.textResource
import splitties.views.verticalPadding

class LoginUi(override val ctx: Context) : Ui {

    val signInButton: SignInButton = view(::SignInButton) {
        setSize(SIZE_WIDE)
    }

    private val appNameTextView = textView {
        textResource = R.string.app_name
        textAppearance = R.style.TextAppearance_MaterialComponents_Headline3
    }

    private val privacyPolicyTextView = textView {
        text = Html.fromHtml(ctx.str(R.string.privacy_policy), Html.FROM_HTML_MODE_LEGACY)
        textAppearance = R.style.TextAppearance_MaterialComponents_Caption
        typeface = Typeface.DEFAULT_BOLD
        gravity = gravityCenter
        verticalPadding = dip(36)
    }

    private val signInInfoTextView = textView {
        textResource = R.string.sign_in_info
        textAppearance = R.style.TextAppearance_MaterialComponents_Caption
        gravity = gravityCenter
        verticalPadding = dip(36)
    }

    override val root: View = constraintLayout {
        add(appNameTextView, lParams(wrapContent, wrapContent) {
            centerHorizontally()
            topOfParent()
            bottomToTopOf(signInButton)
        })
        add(signInButton, lParams(wrapContent, wrapContent) {
            centerHorizontally()
            topToBottomOf(appNameTextView)
            bottomOfParent()
        })
        add(privacyPolicyTextView, lParams(wrapContent, wrapContent) {
            centerHorizontally()
            topToBottomOf(signInButton)
            bottomToTopOf(signInInfoTextView)
        })
        add(signInInfoTextView, lParams(wrapContent, wrapContent) {
            centerHorizontally()
            topToBottomOf(privacyPolicyTextView)
            bottomOfParent()
        })
    }
}

@Suppress("unused")
private class LoginUiPreview : FrameLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        addView(LoginUi(context).root)
    }
}
