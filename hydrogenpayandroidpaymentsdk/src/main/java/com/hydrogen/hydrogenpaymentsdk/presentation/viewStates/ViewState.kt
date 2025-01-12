package com.hydrogen.hydrogenpaymentsdk.presentation.viewStates

data class ViewState<out T>(
    val status: Status,
    val content: T?,
    val message: String,
) {
    companion object {
        fun <T> success(content: T): ViewState<T> = ViewState(
            Status.SUCCESS,
            content,
            "Success",
        )

        fun <T> error(content: T?, message: String = "Failed"): ViewState<T> = ViewState(
            Status.ERROR,
            content,
            message,
        )

        fun <T> loading(content: T?): ViewState<T> = ViewState(
            Status.LOADING,
            content,
            "Loading...",
        )

        fun <T> timeOut(content: T?): ViewState<T> = ViewState(
            Status.TIMEOUT,
            content,
            "Connection timeout! Please try again later",
        )

        fun <T> serverError(content: T?): ViewState<T> = ViewState(
            Status.INITIAL_DEFAULT,
            content,
            "Server error, kindly try again later",
        )

        fun <T> initialDefault(content: T?): ViewState<T> = ViewState(
            Status.INITIAL_DEFAULT,
            content,
            "App just stated up, no action required",
        )
    }
}
