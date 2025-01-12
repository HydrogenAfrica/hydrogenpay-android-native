package com.hydrogen.hydrogenpaymentsdk.usecases.paymentConfirmation

import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.domain.repository.Repository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

class PaymentConfirmationUseCaseImpl(
    private val repository: Repository
) : PaymentConfirmationUseCase {
    override fun confirmPayment(transactionReference: String): Flow<ViewState<PaymentConfirmationResponse?>> =
        repository.confirmPayment(transactionReference)
}