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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

interface HomeBudgetApi {
    val isSignedIn: Boolean
    suspend fun signIn()
    suspend fun getMonthlyBudget(): MonthlyBudget
}

class HomeBudgetApiImpl(private val context: Context) : HomeBudgetApi {

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

    override suspend fun getMonthlyBudget() = withContext(Dispatchers.IO) {
        val response = sheetsService.spreadsheets().values()
            .batchGet(BuildConfig.SPREADSHEET_ID)
            .setRanges(listOf(
                "'Sierpień'!D9:D10",
                "'Sierpień'!D16:D17"
            ))
            .execute()
        val ranges = response.valueRanges
        val planned = ranges[0]["values"] as ArrayList<ArrayList<String>>
        val actual = ranges[1]["values"] as ArrayList<ArrayList<String>>
        MonthlyBudget(
            plannedIncomes = planned[0][0],
            plannedExpenses = planned[1][0],
            actualIncomes = actual[0][0],
            actualExpenses = actual[1][0]
        )
    }

    companion object {
        private const val USER_AGENT = "HomeBudget"
    }
}

class HomeBudgetApiException(override val cause: Throwable?) : RuntimeException(cause)
