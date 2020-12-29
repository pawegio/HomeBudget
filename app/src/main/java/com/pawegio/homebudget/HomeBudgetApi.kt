package com.pawegio.homebudget

import android.content.Context
import com.github.florent37.inlineactivityresult.kotlin.InlineActivityResultException
import com.github.florent37.inlineactivityresult.kotlin.coroutines.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import com.pawegio.homebudget.util.ColumnResolver
import com.pawegio.homebudget.util.currentActivity
import com.pawegio.homebudget.util.polishDisplayName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import java.math.BigDecimal

interface HomeBudgetApi {
    val isSignedIn: Boolean
    suspend fun signIn()
    suspend fun signOut()
    suspend fun getMonthlyBudget(month: Month): MonthlyBudget
    suspend fun addTransaction(transaction: Transaction)
}

data class Transaction(
    val date: LocalDate,
    val subcategory: Subcategory,
    val value: BigDecimal
)

class HomeBudgetApiImpl(
    private val context: Context,
    private val repository: HomeBudgetRepository,
    private val columnResolver: ColumnResolver
) : HomeBudgetApi {

    override val isSignedIn: Boolean get() = account != null

    private val credential by lazy {
        GoogleAccountCredential.usingOAuth2(context, listOf(SheetsScopes.SPREADSHEETS_READONLY))
    }

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(Scope(SheetsScopes.SPREADSHEETS_READONLY))
        .build()

    private val signInClient by lazy { GoogleSignIn.getClient(context, gso) }

    private val account get() = GoogleSignIn.getLastSignedInAccount(context)

    private val sheetsService by lazy {
        credential.selectedAccount = account?.account
        Sheets.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        ).setApplicationName(USER_AGENT).build()
    }

    private val spreadsheetId get() = repository.spreadsheetId

    override suspend fun signIn() {
        if (!isSignedIn) {
            try {
                val result = currentActivity?.startForResult(signInClient.signInIntent)
                GoogleSignIn.getSignedInAccountFromIntent(result?.data).await()
            } catch (e: ApiException) {
                throw HomeBudgetApiException(e)
            } catch (e: InlineActivityResultException) {
                throw HomeBudgetApiException(e)
            }
        }
    }

    override suspend fun signOut() {
        try {
            signInClient.signOut().await()
        } catch (e: ApiException) {
            throw HomeBudgetApiException(e)
        }
    }

    override suspend fun getMonthlyBudget(month: Month) = withContext(Dispatchers.IO) {
        when (repository.spreadsheetTemplate) {
            2019 -> getMonthlyBudget2019(month)
            else -> getMonthlyBudget2020(month)
        }
    }

    override suspend fun addTransaction(transaction: Transaction) {
        val (date, subcategory, value) = transaction
        val month = date.month.polishDisplayName
        val column = columnResolver.getColumnName(date.dayOfMonth)
        val row = subcategory.index
        val range = "'$month'!${column}$row"
        val spreadsheetId = checkNotNull(spreadsheetId)
        withContext(Dispatchers.IO) {
            try {
                val response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .setValueRenderOption("FORMULA")
                    .execute()
                val oldValues = response.getValues() as List<List<Any>>?
                val oldTransactions = oldValues?.first()?.first()
                val newTransactions = when {
                    oldTransactions is String && oldTransactions.startsWith("=") -> "$oldTransactions+$value".replace('.', ',')
                    oldTransactions is BigDecimal -> "=$oldTransactions+$value".replace('.', ',')
                    oldTransactions == null || oldTransactions is String && oldTransactions.isBlank() -> value
                    else -> throw IllegalStateException("Invalid values: $oldValues")
                }
                val body = ValueRange().setValues(listOf(listOf(newTransactions)))
                sheetsService.spreadsheets().values()
                    .update(spreadsheetId, range, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute()
            } catch (e: Exception) {
                e.printStackTrace()
                throw HomeBudgetApiException(e)
            }
        }
    }

    private fun getMonthlyBudget2019(month: Month): MonthlyBudget {
        val monthName = month.polishDisplayName
        val plannedBudgetRange = mapOf("D9" to "D10")
        val actualBudgetRange = mapOf("D16" to "D17")
        val incomesRange = mapOf("B51" to "D66")
        val expensesRanges = mapOf(
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
        val allRanges = plannedBudgetRange + actualBudgetRange + incomesRange + expensesRanges
        return try {
            val response = sheetsService.spreadsheets().values()
                .batchGet(checkNotNull(spreadsheetId))
                .setValueRenderOption("UNFORMATTED_VALUE")
                .setRanges(allRanges.map { "'$monthName'!${it.key}:${it.value}" })
                .execute()
            val ranges = response.valueRanges
            val planned = ranges[0]["values"] as List<List<BigDecimal>>
            val actual = ranges[1]["values"] as List<List<BigDecimal>>
            val incomes = ranges[2]["values"] as List<List<Any>>
            val expenses = List(12) { ranges[it + 3]["values"] as List<List<Any>> }
            val categories = listOf(createCategory(51, incomes, Category.Type.INCOMES)) +
                    expenses.mapIndexed { index, expenses ->
                        createCategory(73 + 12 * index, expenses, Category.Type.EXPENSES)
                    }
            MonthlyBudget(
                month = monthName,
                plannedIncomes = planned[0][0],
                plannedExpenses = planned[1][0],
                actualIncomes = actual[0][0],
                actualExpenses = actual[1][0],
                categories = categories
            ).also(::println)
        } catch (e: Exception) {
            throw HomeBudgetApiException(e)
        }
    }

    private fun getMonthlyBudget2020(month: Month): MonthlyBudget {
        val monthName = month.polishDisplayName
        val plannedBudgetRange = mapOf("D9" to "D10")
        val actualBudgetRange = mapOf("D16" to "D17")
        val incomesRange = mapOf("B57" to "D72")
        val expensesRanges = mapOf(
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
        val allRanges = plannedBudgetRange + actualBudgetRange + incomesRange + expensesRanges
        return try {
            val response = sheetsService.spreadsheets().values()
                .batchGet(checkNotNull(spreadsheetId))
                .setValueRenderOption("UNFORMATTED_VALUE")
                .setRanges(allRanges.map { "'$monthName'!${it.key}:${it.value}" })
                .execute()
            val ranges = response.valueRanges
            val planned = ranges[0]["values"] as List<List<BigDecimal>>
            val actual = ranges[1]["values"] as List<List<BigDecimal>>
            val incomes = ranges[2]["values"] as List<List<Any>>
            val expenses = List(15) { ranges[it + 3]["values"] as List<List<Any>> }
            val categories = listOf(createCategory(57, incomes, Category.Type.INCOMES)) +
                    expenses.mapIndexed { index, expenses ->
                        createCategory(79 + 12 * index, expenses, Category.Type.EXPENSES)
                    }
            MonthlyBudget(
                month = monthName,
                plannedIncomes = planned[0][0],
                plannedExpenses = planned[1][0],
                actualIncomes = actual[0][0],
                actualExpenses = actual[1][0],
                categories = categories
            ).also(::println)
        } catch (e: Exception) {
            e.printStackTrace()
            throw HomeBudgetApiException(e)
        }
    }

    private fun createCategory(index: Int, data: List<List<Any>>, type: Category.Type) =
        Category(
            index = index,
            name = data[0][0] as String,
            type = type,
            subcategories = List(data.size - 1) { subcategoryIndex ->
                Subcategory(
                    index = index + 1 + subcategoryIndex,
                    data[subcategoryIndex + 1][0] as String,
                    data[subcategoryIndex + 1][1] as BigDecimal,
                    data[subcategoryIndex + 1][2] as BigDecimal,
                    type
                )
            }.filter { it.name != "." },
            planned = data[0][1] as BigDecimal,
            actual = data[0][2] as BigDecimal
        )

    companion object {
        private const val USER_AGENT = "HomeBudget"
    }
}

class HomeBudgetApiException(override val cause: Throwable? = null) : RuntimeException(cause)
