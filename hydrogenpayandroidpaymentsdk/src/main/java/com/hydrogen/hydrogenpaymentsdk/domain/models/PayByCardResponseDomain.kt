package com.hydrogen.hydrogenpaymentsdk.domain.models

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.CardPaymentResponse

data class PayByCardResponseDomain(
    val payByCardResponseMessage: String,
    val data: CardPaymentResponse
)
