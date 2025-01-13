package com.hydrogen.hydrogenpaymentsdk.utils

import android.icu.util.TimeUnit
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.AuthToken

internal object AppConstants {
    const val BASE_URL = "https://api.hydrogenpay.com/bepay/api/v1/"
    val AUTH_TOKEN = AuthToken.DEV.token
    const val LONG_NETWORK_RETRY_TIME = 2L

    const val STRING_EXTRA_TAG = "string_extra_tag"
    const val INT_TIME_15_MIN = 2
}