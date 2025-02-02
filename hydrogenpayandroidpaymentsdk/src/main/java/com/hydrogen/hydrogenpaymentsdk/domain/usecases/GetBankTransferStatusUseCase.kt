package com.hydrogen.hydrogenpaymentsdk.domain.usecases

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.domain.models.BankTransferStatus
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
import com.hydrogen.hydrogenpaymentsdk.domain.repository.Repository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

internal class GetBankTransferStatusUseCase(
    private val repository: Repository
) {
    operator fun invoke(
        transactionReference: String,
        transactionDetails: TransactionDetails,
        initiatePaymentRequest: PayByTransferRequest
    ): Flow<ViewState<BankTransferStatus?>> =
        repository.getBankTransferStatus(
            transactionReference,
            transactionDetails,
            initiatePaymentRequest
        )
}