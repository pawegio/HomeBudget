package com.pawegio.homebudget.picker

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class SpreadsheetIdParserTest : FreeSpec({
    "parse spreadsheet id" {
        parseSpreadsheetId("https://docs.google.com/spreadsheets/d/3kdks02emdsa/") shouldBe "3kdks02emdsa"
    }
    "parse spreadsheet id in edit mode" {
        parseSpreadsheetId("https://docs.google.com/spreadsheets/d/1df3a-fda23-fd13/edit#gid=1234") shouldBe "1df3a-fda23-fd13"
    }
})
