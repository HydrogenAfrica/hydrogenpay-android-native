package com.hydrogen.hydrogenpaymentsdk.domain.enums

import androidx.annotation.DrawableRes
import com.hydrogen.hydrogenpayandroidpaymentsdk.R

enum class CardType(val code: Int, val typeName: String, @DrawableRes val icon: Int) {
    VISA(0, "Visa", R.drawable.visa),
    MASTERCARD(1, "Mastercard", R.drawable.mastercard),
    VERVE(2, "Verve", R.drawable.verve_icon),
    UNKNOWN(4, "Unknown", R.drawable.unknown_card_scheme)
}