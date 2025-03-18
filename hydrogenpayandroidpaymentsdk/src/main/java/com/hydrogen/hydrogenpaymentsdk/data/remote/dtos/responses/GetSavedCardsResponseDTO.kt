package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses

data class GetSavedCardsResponseDTO(
    val cardScheme: String,
    val cardToken: String,
    val cvv: String,
    val discountPercentage: Int,
    val expiryMonth: String,
    val expiryYear: String,
    val id: String,
    val isCardExpired: Boolean,
    val isCardSpecificDiscount: Boolean,
    val isLastUsed: Boolean,
    val isPinRequired: Boolean,
    val maskedPan: String,
    val providerId: String
)