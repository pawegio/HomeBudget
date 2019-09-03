package com.pawegio.homebudget.util

import org.threeten.bp.Month

val Month.polishDisplayName: String
    get() = when (value) {
        1 -> "Styczeń"
        2 -> "Luty"
        3 -> "Marzec"
        4 -> "Kwiecień"
        5 -> "Maj"
        6 -> "Czerwiec"
        7 -> "Lipiec"
        8 -> "Sierpień"
        9 -> "Wrzesień"
        10 -> "Październik"
        11 -> "Listopad"
        12 -> "Grudzień"
        else -> throw IllegalStateException()
    }
