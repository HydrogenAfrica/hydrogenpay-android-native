package com.hydrogen.hydrogenpaymentsdk.usecases.payByTransfer

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.domain.repository.Repository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

class PayByTransferUseCaseImpl(
    private val repository: Repository
) : PayByTransferUseCase {
    override fun payByTransfer(transferDetails: PayByTransferRequest): Flow<ViewState<PayByTransferResponse?>> =
        repository.payByTransfer(transferDetails)
}