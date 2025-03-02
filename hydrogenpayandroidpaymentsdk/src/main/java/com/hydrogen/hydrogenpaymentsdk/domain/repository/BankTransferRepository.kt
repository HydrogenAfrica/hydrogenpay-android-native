package com.hydrogen.hydrogenpaymentsdk.domain.repository

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.InitiatePayByTransferRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.InitiatePayByTransferResponseDTO
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionStatus
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

internal interface BankTransferRepository {
    fun payByTransfer(
        transferDetails: InitiatePayByTransferRequestDTO
    ): Flow<ViewState<InitiatePayByTransferResponseDTO?>>

    fun getBankTransferStatus(
        transactionReference: String,
        transactionDetails: TransactionDetails,
        initiatePaymentRequest: PayByTransferRequest
    ): Flow<ViewState<TransactionStatus?>>
}