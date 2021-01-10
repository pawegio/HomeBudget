package com.pawegio.homebudget

import android.accounts.Account
import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

interface HomeBudgetRepository {
    var account: Account?
    var spreadsheetId: String?
    var spreadsheetTemplate: Int
}

class HomeBudgetRepositoryImpl(context: Context) : HomeBudgetRepository {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override var account: Account?
        get() {
            val name = sharedPreferences.getString(ACCOUNT_NAME, null)
            val type = sharedPreferences.getString(ACCOUNT_TYPE, null)
            return if (name != null && type != null) Account(name, type) else null
        }
        set(value) {
            sharedPreferences.edit(commit = true) {
                if (value == null) {
                    remove(ACCOUNT_NAME)
                    remove(ACCOUNT_TYPE)
                } else {
                    putString(ACCOUNT_NAME, value.name)
                    putString(ACCOUNT_TYPE, value.name)
                }
            }
        }

    override var spreadsheetId: String?
        get() = sharedPreferences.getString(SPREADSHEET_ID, null)
        set(value) = sharedPreferences.edit(commit = true) { putString(SPREADSHEET_ID, value) }

    override var spreadsheetTemplate: Int
        get() = sharedPreferences.getInt(SPREADSHEET_TEMPLATE, 2021)
        set(value) = sharedPreferences.edit(commit = true) { putInt(SPREADSHEET_TEMPLATE, value) }

    companion object {
        private const val ACCOUNT_NAME = "accountName"
        private const val ACCOUNT_TYPE = "accountType"
        private const val SPREADSHEET_ID = "spreadsheetId"
        private const val SPREADSHEET_TEMPLATE = "spreadsheetTemplate"
    }
}
