package com.hydrogen.hydrogenpaymentsdk.domain.repository

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.InitiatePayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.InitiatePayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.TransactionDetailsRequest
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentTransactionCredentials
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentMethod
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.Query

interface Repository {
    fun initiatePayment(
        initiatePaymentRequest: PayByTransferRequest
    ): Flow<ViewState<PaymentTransactionCredentials?>>

    fun payByTransfer(
        transferDetails: InitiatePayByTransferRequest
    ): Flow<ViewState<InitiatePayByTransferResponse?>>

    fun confirmPayment(
        transactionReference: String
    ): Flow<ViewState<PaymentConfirmationResponse?>>

    fun getPaymentMethod(
        transactionId: String
    ): Flow<ViewState<List<PaymentMethod>?>>

    fun getTransactionDetails(
        transactionDetailsRequest: TransactionDetailsRequest
    ): Flow<ViewState<TransactionDetails?>>
}