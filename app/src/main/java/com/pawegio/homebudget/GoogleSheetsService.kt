package com.pawegio.homebudget

import android.content.Context
import com.github.florent37.inlineactivityresult.kotlin.coroutines.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.RuntimeException

interface GoogleSheetsService {
    val isSignedIn: Boolean
    suspend fun signIn()
}

class GoogleSheetsServiceImpl(private val context: Context) : GoogleSheetsService {

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

    override suspend fun signIn() {
        if (!isSignedIn) {
            val result = currentActivity?.startForResult(signInClient.signInIntent)
            try {
                GoogleSignIn.getSignedInAccountFromIntent(result?.data).await()
            } catch (e: ApiException) {
                throw GoogleSheetsException(e)
            }
        }
    }

    private fun accessSheets(account: GoogleSignInAccount) {
        credential.selectedAccount = account.account
        val sheetsService = Sheets.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("HomeBudget").build()

        GlobalScope.launch(Dispatchers.IO) {
            val response = sheetsService.spreadsheets().values()
                .get("id", "'Lipiec'!D9:D10")
                .execute()
            val values = response.getValues()
            println("values: $values")
        }
    }
}

class GoogleSheetsException(override val cause: Throwable?) : RuntimeException(cause)
