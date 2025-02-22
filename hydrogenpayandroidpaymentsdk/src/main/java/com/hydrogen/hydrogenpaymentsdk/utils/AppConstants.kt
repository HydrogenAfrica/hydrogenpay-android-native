package com.hydrogen.hydrogenpaymentsdk.utils

internal object AppConstants {
    const val BASE_URL = "https://api.hydrogenpay.com/bepay/api/v1/"
    const val LONG_NETWORK_RETRY_TIME = 2L

    const val STRING_EXTRA_TAG = "string_extra_tag"
    const val STRING_PREFS_MODE_TAG = "string_shared_prefs_mode_tag"
    const val STRING_PREFS_TOKEN_TAG = "string_shared_prefs_token_tag"
    const val STRING_TEST_TRANSACTION_KEY_IDENTIFIER_TAG = "TEST"
    const val LONG_TIME_15_MIN = 15000L
    const val LONG_TIME_15_SEC = 15L
    const val STRING_SUCCESSFUL_SERVER_OPERATION_STATUS_CODE = "90000"
    const val STRING_TIMER_DONE_VALUE = "00:00"
    const val STRING_HYDROGEN_CALL_BACK_URL = "https://hydrogenpay.com"
    const val STRING_APP_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"
    const val STRING_TIME_FORMAT_NO_AM_PM = "yyyy-MM-dd HH:mm:ss"
    const val STRING_TIME_FORMAT_AM_PM = "yyyy-MM-dd HH:mm:ss a"

    const val ENC_KEY = "NdaXdX3rihvlGY0rn2lm+k0ygB9XuKD6odv6hRl8FJo="
    const val ENC_IV = "wUO6nytYemaLq2Z6tOP+5Q=="

    const val STRING_TRANSFER = "Bank transfer"
    const val STRING_CARD_PAYMENT = "Pay by card"

    const val STRING_DEVICE_LANGUAGE = "en-GB"
    const val STRING_DEVICE_TYPE = "mobile"
}