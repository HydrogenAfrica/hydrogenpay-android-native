package com.hydrogen.hydrogenpaymentsdk.domain.repository

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.InitiatePayByTransferRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.TransactionDetailsRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.InitiatePayByTransferResponseDTO
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentMethod
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentTransactionCredentials
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionStatus
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

internal interface Repository {
    fun initiatePayment(
        initiatePaymentRequest: PayByTransferRequest
    ): Flow<ViewState<PaymentTransactionCredentials?>>

    fun getPaymentMethod(
        transactionId: String
    ): Flow<ViewState<List<PaymentMethod>?>>

    fun getTransactionDetails(
        transactionDetailsRequest: TransactionDetailsRequestDTO
    ): Flow<ViewState<TransactionDetails?>>
}