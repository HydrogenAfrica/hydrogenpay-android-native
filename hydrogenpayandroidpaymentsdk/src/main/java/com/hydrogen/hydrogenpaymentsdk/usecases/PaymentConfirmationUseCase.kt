package com.hydrogen.hydrogenpaymentsdk.usecases

import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SessionManagerContract
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.domain.repository.Repository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

internal class PaymentConfirmationUseCase(
    private val repository: Repository,
    private val sessionManager: SessionManagerContract
) {
    operator fun invoke(): Flow<ViewState<PaymentConfirmationResponse?>> {
        val transRef = sessionManager.getSessionTransactionCredentials()?.transactionRef
        return repository.confirmPayment(transRef ?: "")
    }
}