package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses

data class OTPError(
    val errorCode: String,
    val message: String
)