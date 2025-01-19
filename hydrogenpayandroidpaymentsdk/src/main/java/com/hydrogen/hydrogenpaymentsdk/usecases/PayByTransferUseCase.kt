package com.hydrogen.hydrogenpaymentsdk.usecases

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.domain.repository.Repository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

class PayByTransferUseCase(
    private val repository: Repository
) {
    operator fun invoke(transferDetails: PayByTransferRequest): Flow<ViewState<PayByTransferResponse?>> =
        repository.payByTransfer(transferDetails)
}