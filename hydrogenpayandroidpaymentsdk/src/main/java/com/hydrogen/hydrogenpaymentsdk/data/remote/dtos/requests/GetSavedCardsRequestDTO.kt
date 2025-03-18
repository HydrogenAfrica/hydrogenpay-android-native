package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests

data class GetSavedCardsRequestDTO(
    val CustomerEmail: String,
    val merchantRef: String,
    val transactionId: String
)