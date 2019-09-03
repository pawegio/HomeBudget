package com.pawegio.homebudget

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat

val BigDecimal.currencyValue: String
    get() = (NumberFormat.getCurrencyInstance(polishLocale) as (DecimalFormat))
        .apply {
            decimalFormatSymbols = DecimalFormatSymbols(polishLocale)
                .apply { currencySymbol = "zł" }
        }
        .format(this)
