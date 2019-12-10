package com.pawegio.homebudget.picker

fun parseSpreadsheetId(url: String): String =
    url.substringAfter("spreadsheets/d/").substringBefore('/')
