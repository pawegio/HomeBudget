package com.pawegio.homebudget

import android.content.Context
import android.content.Intent
import com.github.florent37.inlineactivityresult.kotlin.InlineActivityResultException
import com.github.florent37.inlineactivityresult.kotlin.coroutines.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
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
    val note: String?,
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
        credential.selectedAccount = account?.account?.takeIf { it.name != null } ?: repository.account
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
                val signInAccount = GoogleSignIn.getSignedInAccountFromIntent(result?.data).await()
                repository.account = signInAccount.account
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
        } finally {
            repository.account = null
        }
    }

    override suspend fun getMonthlyBudget(month: Month) = withContext(Dispatchers.IO) {
        val adapter = when (repository.spreadsheetTemplate) {
            2019 -> HomeBudget2019Adapter
            else -> HomeBudget2020Adapter
        }
        getMonthlyBudget(adapter, month)
    }

    override suspend fun addTransaction(transaction: Transaction) {
        val (note, date, subcategory, value) = transaction
        val month = date.month.polishDisplayName
        val column = columnResolver.getColumnName(date.dayOfMonth)
        val row = subcategory.index
        val range = "'$month'!${column}$row"
        val spreadsheetId = checkNotNull(spreadsheetId)
        withContext(Dispatchers.IO) {
            try {
                addTransaction(spreadsheetId, range, value)
                if (note != null) {
                    addNote(month, date, subcategory, note)
                }
            } catch (e: Exception) {
                val apiException = HomeBudgetApiException(e)
                apiException.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(apiException)
                throw apiException
            }
        }
    }

    private fun addTransaction(spreadsheetId: String, range: String, value: BigDecimal) {
        val response = sheetsService.spreadsheets().values()
            .get(spreadsheetId, range)
            .setValueRenderOption("FORMULA")
            .execute()
        val oldValues = response.getValues() as List<List<Any>>?
        val oldTransactions = oldValues?.first()?.first()
        val newTransactions = when {
            oldTransactions is String && oldTransactions.startsWith("=") -> {
                "$oldTransactions+$value".replace('.', ',')
            }
            oldTransactions is BigDecimal -> "=$oldTransactions+$value".replace('.', ',')
            oldTransactions == null || oldTransactions is String && oldTransactions.isBlank() -> value
            else -> throw IllegalStateException("Invalid values: $oldValues")
        }
        val body = ValueRange().setValues(listOf(listOf(newTransactions)))
        sheetsService.spreadsheets().values()
            .update(spreadsheetId, range, body)
            .setValueInputOption("USER_ENTERED")
            .execute()
    }

    private fun addNote(month: String, date: LocalDate, subcategory: Subcategory, note: String) {
        val column = columnResolver.getColumnName(date.dayOfMonth)
        val row = subcategory.index
        val spreadsheet = sheetsService.spreadsheets().get(spreadsheetId)
            .setFields("sheets(properties(sheetId,title))")
            .execute()
        val sheets = spreadsheet.sheets
        val sheetId = sheets.first { it.properties["title"] == month }.properties["sheetId"] as? Int
        val columnIndex = columnResolver.getColumnIndex(date.dayOfMonth)
        val range = "'$month'!${column}$row"
        val cell = sheetsService.spreadsheets().get(spreadsheetId)
            .setRanges(listOf(range))
            .setFields("sheets/data/rowData/values/note")
            .execute()
        val oldNote = cell.sheets.firstOrNull()?.data?.get(0)?.rowData?.get(0)?.getValues()?.get(0)?.note
        val newNote = buildString {
            if (oldNote != null) {
                append(oldNote)
                append('\n')
            }
            append(note)
        }
        val addNoteRequest = Request().setRepeatCell(
            RepeatCellRequest()
                .setRange(
                    GridRange()
                        .setSheetId(sheetId)
                        .setStartRowIndex(row - 1)
                        .setEndRowIndex(row)
                        .setStartColumnIndex(columnIndex)
                        .setEndColumnIndex(columnIndex + 1)
                )
                .setCell(CellData().setNote(newNote))
                .setFields("note")
        )
        sheetsService.spreadsheets()
            .batchUpdate(spreadsheetId, BatchUpdateSpreadsheetRequest().setRequests(listOf(addNoteRequest)))
            .execute()
    }

    private suspend fun getMonthlyBudget(adapter: HomeBudgetAdapter, month: Month): MonthlyBudget {
        val monthName = month.polishDisplayName
        val allRanges = with(adapter) { plannedBudgetRange + actualBudgetRange + incomesRange + expensesRanges }
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
            val expenses = List(adapter.expensesCategoriesCount) { ranges[it + 3]["values"] as List<List<Any>> }
            val categories = listOf(createCategory(adapter.incomesFirstRow, incomes, Category.Type.INCOMES)) +
                    expenses.mapIndexed { index, expenses ->
                        createCategory(adapter.expensesFirstRow + 12 * index, expenses, Category.Type.EXPENSES)
                    }.filter { it.subcategories.isNotEmpty() }
            MonthlyBudget(
                month = monthName,
                plannedIncomes = planned[0][0],
                plannedExpenses = planned[1][0],
                actualIncomes = actual[0][0],
                actualExpenses = actual[1][0],
                categories = categories
            ).also(::println)
        } catch (e: UserRecoverableAuthIOException) {
            recoverAndRetry(e.intent) {
                getMonthlyBudget(adapter, month)
            }
        } catch (e: Exception) {
            val apiException = HomeBudgetApiException(e)
            apiException.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(apiException)
            throw apiException
        }
    }

    private suspend fun <T> recoverAndRetry(intent: Intent, block: suspend () -> T): T =
        try {
            withContext(Dispatchers.Main) {
                currentActivity?.startForResult(intent)
            }
            block()
        } catch (e: InlineActivityResultException) {
            throw HomeBudgetApiException(e)
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
                    data[subcategoryIndex + 1].getOrNull(1) as? BigDecimal ?: BigDecimal.ZERO,
                    data[subcategoryIndex + 1].getOrNull(2) as? BigDecimal ?: BigDecimal.ZERO,
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
