package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models

data class PaymentMethodDto(
    val alias: String,
    val description: String,
    val displayOrder: Int,
    val id: String,
    val image: String,
    val isActive: Boolean,
    val isPageReroute: Boolean,
    val name: String,
    val providerId: String,
    val transactionLimit: Int,
    val type: Int
)