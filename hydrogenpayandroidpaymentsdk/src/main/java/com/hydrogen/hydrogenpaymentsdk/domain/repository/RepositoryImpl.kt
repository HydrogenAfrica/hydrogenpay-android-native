package com.hydrogen.hydrogenpaymentsdk.domain.repository

import com.hydrogen.hydrogenpaymentsdk.data.remote.apis.HydrogenPaymentGateWayApiService
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PaymentConfirmationRequestDTO
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.LONG_NETWORK_RETRY_TIME
import com.hydrogen.hydrogenpaymentsdk.utils.ModelMapper.toDomain
import com.hydrogen.hydrogenpaymentsdk.utils.NetworkUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import okio.IOException
import java.net.SocketTimeoutException

class RepositoryImpl(
    private val apiService: HydrogenPaymentGateWayApiService,
    private val networkUtil: NetworkUtil
) : Repository {
    override fun payByTransfer(transferDetails: PayByTransferRequest): Flow<ViewState<PayByTransferResponse?>> =
        flow {
            val response = apiService.payByTransfer(transferDetails)
            emit(networkUtil.getServerResponse(response))
        }.map { data ->
            val result = data.content?.data?.toDomain()
            ViewState(
                status = data.status,
                content = result,
                message = data.message
            ) as ViewState<PayByTransferResponse?>
        }.retry(LONG_NETWORK_RETRY_TIME) {
            return@retry it is SocketTimeoutException || it is IOException
        }.catch {
            val handledException = networkUtil.handleError<PayByTransferResponse>(it)
            emit(handledException)
        }

    override fun confirmPayment(transactionReference: String): Flow<ViewState<PaymentConfirmationResponse?>> =
        flow {
            val request = PaymentConfirmationRequestDTO(transactionReference)
            val response = apiService.paymentConfirmation(request)
            emit(networkUtil.getServerResponse(response))
        }.map { data ->
            val result = data.content?.data?.toDomain()
            ViewState(
                status = data.status,
                content = result,
                message = data.message
            ) as ViewState<PaymentConfirmationResponse?>
        }.retry(LONG_NETWORK_RETRY_TIME) {
            return@retry it is SocketTimeoutException || it is IOException
        }.catch {
            val handledException = networkUtil.handleError<PaymentConfirmationResponse>(it)
            emit(handledException)
        }
}