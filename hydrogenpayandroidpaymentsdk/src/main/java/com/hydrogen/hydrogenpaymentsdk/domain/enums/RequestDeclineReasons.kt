package com.hydrogen.hydrogenpaymentsdk.domain.enums

enum class RequestDeclineReasons(val reason: String) {
    CANCELLED("CANCELLED"),
    INVALID_ARGUMENT_PROVIDED("INVALID ARGUMENT SUPPLIED")
}