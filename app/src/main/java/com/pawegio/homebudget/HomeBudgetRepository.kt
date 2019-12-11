package com.pawegio.homebudget

interface HomeBudgetRepository {
    var spreadsheetId: String?
}

object HomeBudgetRepositoryImpl : HomeBudgetRepository {

    override var spreadsheetId: String? = null
}
