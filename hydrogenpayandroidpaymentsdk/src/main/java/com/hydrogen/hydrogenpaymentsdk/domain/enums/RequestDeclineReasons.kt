package com.hydrogen.hydrogenpaymentsdk.domain.enums

enum class RequestDeclineReasons(val reason: String) {
    CANCELLED("CANCELLED"),
    INVALID_ARGUMENT_PROVIDED("INVALID ARGUMENT SUPPLIED"),
    TIME_OUT("REQUEST WAS NOT COMPLETED WITHIN THE SPECIFIED TIME FRAME, 15 MINS")
}