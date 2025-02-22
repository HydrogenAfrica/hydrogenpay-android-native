package com.hydrogen.hydrogenpaymentsdk.domain.enums

enum class CardType(val code: Int, val typeName: String) {
    VISA(0, "Visa"),
    MASTERCARD(1, "Mastercard"),
    VERVE(2, "Verve"),
    UNKNOWN(4, "Unknown");
}