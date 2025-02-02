package com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs

import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentTransactionCredentials

internal interface SessionManagerContract {
    fun saveSessionTransactionCredentials(transactionCredentials: PaymentTransactionCredentials)
    fun getSessionTransactionCredentials(): PaymentTransactionCredentials?
    fun clearSessionTransactionCredentials()
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
}