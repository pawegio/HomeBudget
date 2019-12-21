package com.pawegio.homebudget.util

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri

interface HowToLauncher {
    fun launch()
}

class HowToLauncherImpl(private val context: Context) : HowToLauncher {

    override fun launch() {
        context.startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(HOME_BUDGET_ARTICLE_URL)
            addFlags(FLAG_ACTIVITY_NEW_TASK)
        })
    }
}

private const val HOME_BUDGET_ARTICLE_URL =
    "https://jakoszczedzacpieniadze.pl/budzet-domowy-2019-szablon-arkusza"
