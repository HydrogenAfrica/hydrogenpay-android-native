package com.hydrogen.hydrogenpaymentsdk.utils

import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.LONG_NETWORK_RETRY_TIME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retry
import okio.IOException
import java.net.SocketTimeoutException

object ExtensionFunctions {
    fun <T> Flow<ViewState<T?>>.retryAndCatchExceptions(networkUtil: NetworkUtil): Flow<ViewState<T?>> =
        this.retry(
            LONG_NETWORK_RETRY_TIME
        ) {
            return@retry it is SocketTimeoutException || it is IOException
        }.catch {
            val handledException = networkUtil.handleError<T>(it)
            emit(handledException)
        }
}