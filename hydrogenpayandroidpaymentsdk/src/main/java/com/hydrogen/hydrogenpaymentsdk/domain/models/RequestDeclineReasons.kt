package com.hydrogen.hydrogenpaymentsdk.domain.models

enum class RequestDeclineReasons(val reason: String) {
    CANCELLED("CANCELLED"),
    INVALID_ARGUMENT_PROVIDED("INVALID ARGUMENT SUPPLIED")
}