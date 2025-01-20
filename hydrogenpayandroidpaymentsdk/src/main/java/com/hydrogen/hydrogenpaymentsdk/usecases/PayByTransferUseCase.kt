package com.hydrogen.hydrogenpaymentsdk.usecases

import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SessionManagerContract
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.InitiatePayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.InitiatePayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.domain.repository.Repository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

internal class PayByTransferUseCase(
    private val repository: Repository,
    private val sessionManager: SessionManagerContract
) {
    operator fun invoke(): Flow<ViewState<InitiatePayByTransferResponse?>> {
        val transactionId = sessionManager.getSessionTransactionCredentials()?.transactionId
        return repository.payByTransfer(InitiatePayByTransferRequest(transactionId ?: ""))
    }
}