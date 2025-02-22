package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models

data class CurrencyInfo(
    val alpha2Code: String,
    val currencySymbol: String,
    val id: String,
    val name: String,
    val numericCode: String
)