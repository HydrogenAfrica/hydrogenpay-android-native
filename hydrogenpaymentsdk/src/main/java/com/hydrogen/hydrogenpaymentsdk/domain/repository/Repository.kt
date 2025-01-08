package com.hydrogen.hydrogenpaymentsdk.domain.repository

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequestBody
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface Repository {
    fun payByTransfer(
        transferDetails: PayByTransferRequestBody
    ): Flow<ViewState<PayByTransferResponse?>>

    fun confirmPayment(
        transactionReference: String
    ): Flow<ViewState<PaymentConfirmationResponse?>>
}