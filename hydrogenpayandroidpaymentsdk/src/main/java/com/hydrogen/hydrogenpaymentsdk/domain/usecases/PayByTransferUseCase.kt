package com.hydrogen.hydrogenpaymentsdk.domain.usecases

import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SessionManagerContract
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.InitiatePayByTransferRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.InitiatePayByTransferResponseDTO
import com.hydrogen.hydrogenpaymentsdk.domain.repository.BankTransferRepository
import com.hydrogen.hydrogenpaymentsdk.domain.repository.Repository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

internal class PayByTransferUseCase(
    private val repository: BankTransferRepository,
    private val sessionManager: SessionManagerContract
) {
    operator fun invoke(): Flow<ViewState<InitiatePayByTransferResponseDTO?>> {
        val transactionId = sessionManager.getSessionTransactionCredentials()?.transactionId
        return repository.payByTransfer(InitiatePayByTransferRequestDTO(transactionId ?: ""))
    }
}