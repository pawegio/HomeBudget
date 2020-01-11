package com.pawegio.homebudget

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

interface HomeBudgetRepository {
    var spreadsheetId: String?
    var spreadsheetVersion: Int
}

class HomeBudgetRepositoryImpl(context: Context) : HomeBudgetRepository {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override var spreadsheetId: String?
        get() = sharedPreferences.getString(SPREADSHEET_ID, null)
        set(value) = sharedPreferences.edit(commit = true) { putString(SPREADSHEET_ID, value) }

    override var spreadsheetVersion: Int
        get() = sharedPreferences.getInt(SPREADSHEET_VERSION, 2020)
        set(value) = sharedPreferences.edit(commit = true) { putInt(SPREADSHEET_VERSION, value) }

    companion object {
        private const val SPREADSHEET_ID = "spreadsheetId"
        private const val SPREADSHEET_VERSION = "spreadsheetVersion"
    }
}
