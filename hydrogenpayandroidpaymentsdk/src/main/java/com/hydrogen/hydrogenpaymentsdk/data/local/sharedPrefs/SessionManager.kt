package com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs

import com.google.gson.Gson
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentTransactionCredentials
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_PREFS_MODE_TAG
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_PREFS_TOKEN_TAG

internal class SessionManager(
    private val sharedPrefsManagerContract: SharedPrefsManagerContract,
    private val gson: Gson
) : SessionManagerContract {
    override fun saveSessionTransactionCredentials(transactionCredentials: PaymentTransactionCredentials) {
        val transactionCredentialsAsString = gson.toJson(transactionCredentials)
        sharedPrefsManagerContract.saveStringToSharedPrefs(STRING_PREFS_MODE_TAG, transactionCredentialsAsString)
    }

    override fun getSessionTransactionCredentials(): PaymentTransactionCredentials? {
        val transactionCredentials = sharedPrefsManagerContract.retrieveStringFromSharedPrefs(STRING_PREFS_MODE_TAG)
        return gson.fromJson(transactionCredentials, PaymentTransactionCredentials::class.java)
    }

    override fun clearSessionTransactionCredentials() {
        sharedPrefsManagerContract.deletePrefs(STRING_PREFS_MODE_TAG)
    }

    override fun saveToken(token: String) {
        sharedPrefsManagerContract.saveStringToSharedPrefs(STRING_PREFS_TOKEN_TAG, token)
    }

    override fun getToken(): String? = sharedPrefsManagerContract.retrieveStringFromSharedPrefs(STRING_PREFS_TOKEN_TAG)

    override fun clearToken() {
        sharedPrefsManagerContract.deletePrefs(STRING_PREFS_TOKEN_TAG)
    }
}