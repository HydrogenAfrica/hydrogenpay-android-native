package com.hydrogen.hydrogenpaymentsdk.domain.repository

import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SessionManagerContract
import com.hydrogen.hydrogenpaymentsdk.data.remote.apis.HydrogenPaymentGateWayApiService
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.GetBankTransferStatusRequestBody
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.InitiatePayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.InitiatePayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PaymentConfirmationRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.TransactionDetailsRequest
import com.hydrogen.hydrogenpaymentsdk.domain.models.BankTransferStatus
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentMethod
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentTransactionCredentials
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
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

    override fun payByTransfer(transferDetails: InitiatePayByTransferRequest): Flow<ViewState<InitiatePayByTransferResponse?>> =
        flow {
            val response = apiService.payByTransfer(transferDetails)
            emit(networkUtil.getServerResponse(response))
        }.map { data ->
            val result = data.content?.data?.paymentConfirmationResponse()
            ViewState(
                status = data.status,
                content = result,
                message = data.message
            ) as ViewState<InitiatePayByTransferResponse?>
        }.retryAndCatchExceptions(networkUtil)

    override fun confirmPayment(transactionReference: String): Flow<ViewState<PaymentConfirmationResponse?>> =
        flow {
            val request = PaymentConfirmationRequestDTO(transactionReference)
            val response = apiService.paymentConfirmation(request)
            emit(networkUtil.getServerResponse(response))
        }.map { data ->
            val result = data.content?.data?.paymentConfirmationResponse()
            ViewState(
                status = data.status,
                content = result,
                message = data.message
            ) as ViewState<PaymentConfirmationResponse?>
        }.retryAndCatchExceptions(networkUtil)

    override fun getBankTransferStatus(
        transactionReference: String,
        transactionDetails: TransactionDetails,
        initiatePaymentRequest: PayByTransferRequest
    ): Flow<ViewState<BankTransferStatus?>> =
        flow {
            val request = GetBankTransferStatusRequestBody(transactionReference)
            val response = apiService.checkBankTransferStatus(request)
            emit(networkUtil.getServerResponse(response))
        }.map { data ->
            val result = data.content?.data?.toDomain(transactionDetails)
            ViewState(
                status = data.status,
                content = result,
                message = data.message
            ) as ViewState<BankTransferStatus?>
        }

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

    override fun getTransactionDetails(transactionDetailsRequest: TransactionDetailsRequest): Flow<ViewState<TransactionDetails?>> =
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