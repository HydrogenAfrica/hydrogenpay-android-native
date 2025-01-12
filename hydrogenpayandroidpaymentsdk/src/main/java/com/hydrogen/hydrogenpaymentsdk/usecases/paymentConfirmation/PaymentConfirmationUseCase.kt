package com.hydrogen.hydrogenpaymentsdk.usecases.paymentConfirmation

import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

interface PaymentConfirmationUseCase {
    fun confirmPayment(
        transactionReference: String
    ): Flow<ViewState<PaymentConfirmationResponse?>>
}