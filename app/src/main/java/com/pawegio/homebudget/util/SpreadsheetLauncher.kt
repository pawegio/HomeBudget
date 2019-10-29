package com.pawegio.homebudget.util

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import com.pawegio.homebudget.BuildConfig


interface SpreadsheetLauncher {
    fun launch()
}

class SpreadsheetLauncherImpl(private val context: Context) : SpreadsheetLauncher {

    override fun launch() {
        context.startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("$SPREADSHEETS_URL${BuildConfig.SPREADSHEET_ID}")
            addFlags(FLAG_ACTIVITY_NEW_TASK)
        })
    }
}

private const val SPREADSHEETS_URL = "https://docs.google.com/spreadsheets/d/"
