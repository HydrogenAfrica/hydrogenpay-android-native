package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.DeviceInformation

data class CardPaymentRequestDto(
    val CardDetails: String,
    val callbackURL: String,
    val currency: String,
    val customerId: String,
    val deviceInformation: DeviceInformation,
    val ipAddress: String,
    val isCardSave: Boolean,
    val providerId: String,
    val transactionId: String
)