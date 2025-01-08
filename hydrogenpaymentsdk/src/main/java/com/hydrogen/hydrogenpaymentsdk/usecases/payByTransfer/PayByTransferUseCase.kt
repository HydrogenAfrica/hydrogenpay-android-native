package com.hydrogen.hydrogenpaymentsdk.usecases.payByTransfer

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequestBody
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

interface PayByTransferUseCase {
    fun payByTransfer(
        transferDetails: PayByTransferRequestBody
    ): Flow<ViewState<PayByTransferResponse?>>
}