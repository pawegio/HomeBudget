package com.pawegio.homebudget.util

interface ColumnResolver {
    fun getColumnName(dayOfMonth: Int): String
    fun getColumnIndex(dayOfMonth: Int): Int
}

object ColumnResolverImpl : ColumnResolver {

    override fun getColumnName(dayOfMonth: Int): String =
        when (dayOfMonth) {
            in 1..18 -> ('H' + dayOfMonth).toString()
            in 19..31 -> ("A" + ('A' + dayOfMonth - 19))
            else -> throw IllegalStateException()
        }

    override fun getColumnIndex(dayOfMonth: Int): Int = dayOfMonth + 7
}