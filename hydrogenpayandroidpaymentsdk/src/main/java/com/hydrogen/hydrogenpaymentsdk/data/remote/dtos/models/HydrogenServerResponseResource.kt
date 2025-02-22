package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models

data class HydrogenServerResponseResource<T>(
    val `data`: T,
    val message: String,
    val statusCode: String
)