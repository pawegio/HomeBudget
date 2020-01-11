package com.pawegio.homebudget

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

interface HomeBudgetRepository {
    var spreadsheetId: String?
    var spreadsheetTemplate: Int
}

class HomeBudgetRepositoryImpl(context: Context) : HomeBudgetRepository {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override var spreadsheetId: String?
        get() = sharedPreferences.getString(SPREADSHEET_ID, null)
        set(value) = sharedPreferences.edit(commit = true) { putString(SPREADSHEET_ID, value) }

    override var spreadsheetTemplate: Int
        get() = sharedPreferences.getInt(SPREADSHEET_TEMPLATE, 2020)
        set(value) = sharedPreferences.edit(commit = true) { putInt(SPREADSHEET_TEMPLATE, value) }

    companion object {
        private const val SPREADSHEET_ID = "spreadsheetId"
        private const val SPREADSHEET_TEMPLATE = "spreadsheetTemplate"
    }
}
