package com.pawegio.homebudget

import android.content.Context
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
import com.pawegio.homebudget.util.currentActivity
import com.pawegio.homebudget.util.polishDisplayName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.threeten.bp.Month
import java.io.IOException
import java.math.BigDecimal

interface HomeBudgetApi {
    val isSignedIn: Boolean
    suspend fun signIn()
    suspend fun signOut()
    suspend fun getMonthlyBudget(month: Month): MonthlyBudget
}

class HomeBudgetApiImpl(
    private val context: Context,
    private val repository: HomeBudgetRepository
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
            val result = currentActivity?.startForResult(signInClient.signInIntent)
            try {
                GoogleSignIn.getSignedInAccountFromIntent(result?.data).await()
            } catch (e: ApiException) {
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
        try {
            val response = sheetsService.spreadsheets().values()
                .batchGet(spreadsheetId)
                .setValueRenderOption("UNFORMATTED_VALUE")
                .setRanges(allRanges.map { "'$monthName'!${it.key}:${it.value}" })
                .execute()
            val ranges = response.valueRanges
            val planned = ranges[0]["values"] as List<List<BigDecimal>>
            val actual = ranges[1]["values"] as List<List<BigDecimal>>
            val incomes = ranges[2]["values"] as List<List<Any>>
            val expenses = List(12) { ranges[it + 3]["values"] as List<List<Any>> }
            val categories = listOf(createCategory(incomes, Category.Type.INCOMES)) +
                    expenses.map { createCategory(it, Category.Type.EXPENSES) }
            MonthlyBudget(
                month = monthName,
                plannedIncomes = planned[0][0],
                plannedExpenses = planned[1][0],
                actualIncomes = actual[0][0],
                actualExpenses = actual[1][0],
                categories = categories
            ).also(::println)
        } catch (e: IOException) {
            throw HomeBudgetApiException(e)
        }
    }

    private fun createCategory(data: List<List<Any>>, type: Category.Type) =
        Category(
            name = data[0][0] as String,
            type = type,
            subcategories = List(data.size - 1) { index ->
                Subcategory(
                    data[index + 1][0] as String,
                    data[index + 1][1] as BigDecimal,
                    data[index + 1][2] as BigDecimal,
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
