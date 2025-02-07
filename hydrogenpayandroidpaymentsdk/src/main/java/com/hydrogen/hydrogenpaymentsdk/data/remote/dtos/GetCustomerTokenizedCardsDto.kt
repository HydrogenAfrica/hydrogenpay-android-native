package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos

data class GetCustomerTokenizedCardsDto(
    val CustomerEmail: String,
    val merchantRef: String,
    val transactionId: String
)