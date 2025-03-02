package com.hydrogen.hydrogenpaymentsdk.data.repositoryImpl

import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SessionManagerContract
import com.hydrogen.hydrogenpaymentsdk.data.remote.apis.BankTransferApiService
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.GetBankTransferStatusRequestBody
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.InitiatePayByTransferRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.InitiatePayByTransferResponseDTO
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionStatus
import com.hydrogen.hydrogenpaymentsdk.domain.repository.BankTransferRepository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import com.hydrogen.hydrogenpaymentsdk.utils.ExtensionFunctions.retryAndCatchExceptions
import com.hydrogen.hydrogenpaymentsdk.utils.ModelMapper.paymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.utils.ModelMapper.toDomain
import com.hydrogen.hydrogenpaymentsdk.utils.NetworkUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class BankTransferRepositoryImpl(
    private val apiService: BankTransferApiService,
    private val networkUtil: NetworkUtil
) : BankTransferRepository {
    override fun payByTransfer(transferDetails: InitiatePayByTransferRequestDTO): Flow<ViewState<InitiatePayByTransferResponseDTO?>> =
        flow {
            val response = apiService.payByTransfer(transferDetails)
            emit(networkUtil.getServerResponse(response))
        }.map { data ->
            val result = data.content?.data?.paymentConfirmationResponse()
            ViewState(
                status = data.status,
                content = result,
                message = data.message
            ) as ViewState<InitiatePayByTransferResponseDTO?>
        }.retryAndCatchExceptions(networkUtil)

    override fun getBankTransferStatus(
        transactionReference: String,
        transactionDetails: TransactionDetails,
        initiatePaymentRequest: PayByTransferRequest
    ): Flow<ViewState<TransactionStatus?>> =
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
            ) as ViewState<TransactionStatus?>
        }
}