package com.hydrogen.hydrogenpaymentsdk.domain.repository

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun payByTransfer(
        transferDetails: PayByTransferRequest
    ): Flow<ViewState<PayByTransferResponse?>>

    fun confirmPayment(
        transactionReference: String
    ): Flow<ViewState<PaymentConfirmationResponse?>>
}