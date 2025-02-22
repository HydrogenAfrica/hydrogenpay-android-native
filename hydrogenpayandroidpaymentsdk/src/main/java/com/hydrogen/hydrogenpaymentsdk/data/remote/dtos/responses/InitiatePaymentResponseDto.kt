package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses

data class InitiatePaymentResponseDto(
    val transactionRef: String,
    val url: String
) {
    fun getTransactionId(): String {
        val regex = Regex("transactionId=([a-zA-Z0-9\\-]+)")
        val matchResult = regex.find(url)
        return matchResult?.groupValues?.get(1) ?: ""
    }

    fun getTransactionMode(): String {
        val regex = Regex("Mode=([a-zA-Z0-9]+)")
        val matchResult = regex.find(url)
        return matchResult?.groupValues?.get(1) ?: ""
    }
}