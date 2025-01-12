package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos

data class HydrogenServerResponse<T>(
    val `data`: T,
    val message: String,
    val statusCode: String
)