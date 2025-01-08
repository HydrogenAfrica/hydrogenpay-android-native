package com.hydrogen.hydrogenpaymentsdk.utils

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.HydrogenServerResponse
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class NetworkUtil {
    fun <T> getServerResponse(serverResponse: Response<T>): ViewState<T?> {
        return when {
            serverResponse.isSuccessful -> ViewState.success(serverResponse.body()!!)
            serverResponse.code() >= 500 -> ViewState.serverError(null)
            serverResponse.code() in 400..499 -> ViewState.error(null, "Client error")
            serverResponse.isSuccessful && (serverResponse.body()!! as HydrogenServerResponse<*>).message != "Operation Successful" -> ViewState.error(
                null,
                "Failed! Check the details provided"
            )

            else -> ViewState.error(null)
        }
    }

    fun <T> handleError(e: Throwable): ViewState<T?> =
        when (e) {
            is SocketTimeoutException -> {
                ViewState.timeOut(null)
            }

            is HttpException -> {
                ViewState.serverError(null)
            }

            is IOException -> {
                ViewState.error(null, "Network timeout")
            }

            else -> {
                ViewState.error(null)
            }
        }
}