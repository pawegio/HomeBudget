package com.pawegio.homebudget.util

import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

internal class ColumnResolverImplTest : FreeSpec({
    val resolver = ColumnResolverImpl
    "get column name for 1st day of month" {
        resolver.getColumnName(1) shouldBe "I"
    }
    "get column name for 18th day of month" {
        resolver.getColumnName(18) shouldBe "Z"
    }
    "get column name for 19th day of month" {
        resolver.getColumnName(19) shouldBe "AA"
    }
    "get column name for 31th day of month" {
        resolver.getColumnName(31) shouldBe "AM"
    }
})