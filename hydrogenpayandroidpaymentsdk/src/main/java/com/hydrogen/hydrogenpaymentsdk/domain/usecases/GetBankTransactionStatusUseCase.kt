package com.hydrogen.hydrogenpaymentsdk.domain.usecases

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionStatus
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
import com.hydrogen.hydrogenpaymentsdk.domain.repository.BankTransferRepository
import com.hydrogen.hydrogenpaymentsdk.domain.repository.Repository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

internal class GetBankTransactionStatusUseCase(
    private val repository: BankTransferRepository
) {
    operator fun invoke(
        transactionReference: String,
        transactionDetails: TransactionDetails,
        initiatePaymentRequest: PayByTransferRequest
    ): Flow<ViewState<TransactionStatus?>> =
        repository.getBankTransferStatus(
            transactionReference,
            transactionDetails,
            initiatePaymentRequest
        )
}