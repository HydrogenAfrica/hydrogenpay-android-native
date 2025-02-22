package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses

data class CardProviderResponse(
    val discountPercentage: Int,
    val isCardSpecificDiscount: Boolean,
    val isPinRequired: Boolean,
    val providerId: String,
    val providerName: String
)