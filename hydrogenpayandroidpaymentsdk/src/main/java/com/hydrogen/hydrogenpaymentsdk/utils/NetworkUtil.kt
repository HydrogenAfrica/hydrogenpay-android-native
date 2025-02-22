package com.hydrogen.hydrogenpaymentsdk.utils

import android.util.Log
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.HydrogenServerResponseResource
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_SUCCESSFUL_SERVER_OPERATION_STATUS_CODE
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class NetworkUtil {
    fun <T> getServerResponse(serverResponse: Response<T>): ViewState<T?> {
        return when {
            serverResponse.isSuccessful -> {
                if (serverResponse.body() != null && (serverResponse.body() as HydrogenServerResponseResource<*>).statusCode == STRING_SUCCESSFUL_SERVER_OPERATION_STATUS_CODE) {
                    ViewState.success(serverResponse.body())
                } else {
                    ViewState.error(
                        null,
                        (serverResponse.body()!! as HydrogenServerResponseResource<*>).message
                    )
                }
            }

            serverResponse.code() >= 500 -> ViewState.serverError(null)
            serverResponse.code() in 400..499 -> ViewState.error(null, "Client error")
            else -> ViewState.error(null)
        }
    }

    fun <T> handleError(e: Throwable): ViewState<T?> =
        when (e) {
            is SocketTimeoutException -> {
                Log.d("ERROR_COMING", e.localizedMessage ?: "SocketTimeout Exception")
                ViewState.timeOut(null)
            }

            is HttpException -> {
                Log.d("ERROR_COMING", e.localizedMessage ?: "IO Exception")
                ViewState.serverError(null)
            }

            is IOException -> {
                Log.d("ERROR_COMING", e.localizedMessage ?: "Network timeout")
                ViewState.error(null, "Network timeout")
            }

            else -> {
                Log.d("ERROR_COMING", e.localizedMessage ?: "Null was here")
                ViewState.error(null)
            }
        }
}