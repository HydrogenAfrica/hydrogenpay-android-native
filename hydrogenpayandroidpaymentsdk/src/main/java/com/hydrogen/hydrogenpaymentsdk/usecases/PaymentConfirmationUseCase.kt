package com.hydrogen.hydrogenpaymentsdk.usecases

import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.domain.repository.Repository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

class PaymentConfirmationUseCase(
    private val repository: Repository
) {
    operator fun invoke(transactionReference: String): Flow<ViewState<PaymentConfirmationResponse?>> =
        repository.confirmPayment(transactionReference)
}