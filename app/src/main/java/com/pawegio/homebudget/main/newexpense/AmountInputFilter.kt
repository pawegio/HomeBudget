package com.pawegio.homebudget.main.newexpense

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

class AmountInputFilter(
    digitsBeforeSeparator: Int,
    digitsAfterSeparator: Int
) : InputFilter {

    private val pattern: Pattern

    init {
        val b = "(-?\\d{1,$digitsBeforeSeparator})"
        val a = "(\\d{1,$digitsAfterSeparator})"
        val s = separators
        val numberRegex = buildString {
            append("(-)")
            append("|")
            append("($b$s$a)")
            append("|")
            append("($b$s)")
            append("|")
            append("($b)")
        }
        pattern = Pattern.compile(numberRegex)
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val input =
            dest.toString().substring(0, dstart) + source.subSequence(start, end) + dest.toString()
                .substring(dend)
        val matcher = pattern.matcher(input)
        return if (!matcher.matches()) "" else null
    }

    companion object {
        private const val separators = "[\\.\\,]"

    }
}
