package com.hydrogen.hydrogenpaymentsdk.data.repositoryImpl

import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SessionManagerContract
import com.hydrogen.hydrogenpaymentsdk.data.remote.apis.HydrogenPaymentGateWayApiService
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.GetBankTransferStatusRequestBody
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.InitiatePayByTransferRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.InitiatePayByTransferResponseDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.PaymentConfirmationRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.TransactionDetailsRequestDTO
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionStatus
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentMethod
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentTransactionCredentials
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
import com.hydrogen.hydrogenpaymentsdk.domain.repository.Repository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import com.hydrogen.hydrogenpaymentsdk.utils.ExtensionFunctions.retryAndCatchExceptions
import com.hydrogen.hydrogenpaymentsdk.utils.ModelMapper.paymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.utils.ModelMapper.toDomain
import com.hydrogen.hydrogenpaymentsdk.utils.NetworkUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class RepositoryImpl(
    private val apiService: HydrogenPaymentGateWayApiService,
    private val networkUtil: NetworkUtil,
    private val sessionManager: SessionManagerContract
) : Repository {
    override fun initiatePayment(initiatePaymentRequest: PayByTransferRequest): Flow<ViewState<PaymentTransactionCredentials?>> =
        flow {
            val response = apiService.initiatePayment(initiatePaymentRequest)
            emit(networkUtil.getServerResponse(response))
        }.map { data ->
            val result = data.content?.data?.paymentConfirmationResponse()
            sessionManager.saveSessionTransactionCredentials(result!!)
            ViewState(
                status = data.status,
                content = result,
                message = data.message
            ) as ViewState<PaymentTransactionCredentials?>
        }.retryAndCatchExceptions(networkUtil)

    override fun getPaymentMethod(transactionId: String): Flow<ViewState<List<PaymentMethod>?>> =
        flow {
            val response = apiService.getPaymentMethod(transactionId)
            emit(networkUtil.getServerResponse(response))
        }.map { data ->
            val result = data.content?.data?.map { it.paymentConfirmationResponse() }
            ViewState(
                status = data.status,
                content = result,
                message = data.message
            ) as ViewState<List<PaymentMethod>>
        }.retryAndCatchExceptions(networkUtil)

    override fun getTransactionDetails(transactionDetailsRequest: TransactionDetailsRequestDTO): Flow<ViewState<TransactionDetails?>> =
        flow {
            val response = apiService.getTransactionDetails(transactionDetailsRequest)
            emit(networkUtil.getServerResponse(response))
        }.map { data ->
            val result = data.content?.data?.paymentConfirmationResponse()
            ViewState(
                status = data.status,
                content = result,
                message = data.message
            ) as ViewState<TransactionDetails>
        }.retryAndCatchExceptions(networkUtil)
}