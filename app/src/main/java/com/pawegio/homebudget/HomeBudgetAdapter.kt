package com.pawegio.homebudget

interface HomeBudgetAdapter {
    val plannedBudgetRange: Map<String, String>
    val actualBudgetRange: Map<String, String>
    val incomesRange: Map<String, String>
    val expensesRanges: Map<String, String>
    val incomesFirstRow: Int
    val expensesFirstRow: Int
    val expensesCategoriesCount: Int
}

object HomeBudget2020Adapter : HomeBudgetAdapter {

    override val plannedBudgetRange = mapOf("D9" to "D10")

    override val actualBudgetRange = mapOf("D16" to "D17")

    override val incomesRange = mapOf("B57" to "D72")

    override val expensesRanges = mapOf(
        "B79" to "D89",
        "B91" to "D101",
        "B103" to "D113",
        "B115" to "D125",
        "B127" to "D137",
        "B139" to "D149",
        "B151" to "D161",
        "B163" to "D173",
        "B175" to "D185",
        "B187" to "D197",
        "B199" to "D209",
        "B211" to "D221",
        "B223" to "D233",
        "B235" to "D245",
        "B247" to "D257"
    )

    override val incomesFirstRow = 57

    override val expensesFirstRow = 79

    override val expensesCategoriesCount = expensesRanges.count()
}

object HomeBudget2019Adapter : HomeBudgetAdapter {

    override val plannedBudgetRange = mapOf("D9" to "D10")

    override val actualBudgetRange = mapOf("D16" to "D17")

    override val incomesRange = mapOf("B51" to "D66")

    override val expensesRanges = mapOf(
        "B73" to "D83",
        "B85" to "D95",
        "B97" to "D107",
        "B109" to "D119",
        "B121" to "D131",
        "B133" to "D143",
        "B145" to "D155",
        "B157" to "D167",
        "B169" to "D179",
        "B181" to "D191",
        "B193" to "D203",
        "B205" to "D215"
    )

    override val incomesFirstRow = 51

    override val expensesFirstRow = 73

    override val expensesCategoriesCount = expensesRanges.count()
}
