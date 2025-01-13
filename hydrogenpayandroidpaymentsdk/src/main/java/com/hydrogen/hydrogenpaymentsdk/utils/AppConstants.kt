package com.hydrogen.hydrogenpaymentsdk.utils

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.AuthToken

internal object AppConstants {
    const val BASE_URL = "https://api.hydrogenpay.com/bepay/api/v1/"
    val AUTH_TOKEN = AuthToken.DEV.token
    const val LONG_NETWORK_RETRY_TIME = 2L

    const val STRING_EXTRA_TAG = "string_extra_tag"
    const val LONG_TIME_15_MIN = 15000L
    const val LONG_TIME_15_SECS = 15L
    const val STRING_SUCCESSFUL_SERVER_OPERATION_STATUS_CODE = "90000"
    const val STRING_TIMER_DONE_VALUE = "0:00"
    const val STRING_APP_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"
    const val STRING_TIME_FORMAT_NO_AM_PM = "yyyy-MM-dd HH:mm:ss"
    const val STRING_TIME_FORMAT_AM_PM = "yyyy-MM-dd HH:mm:ss a"
}