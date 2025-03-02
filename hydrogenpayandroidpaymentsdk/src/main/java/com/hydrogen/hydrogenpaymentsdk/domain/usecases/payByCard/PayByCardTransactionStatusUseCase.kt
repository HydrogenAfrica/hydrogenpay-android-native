package com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
import com.hydrogen.hydrogenpaymentsdk.domain.repository.PayByCardRepository

internal class PayByCardTransactionStatusUseCase(
    private val payByCardRepository: PayByCardRepository
) {
    operator fun invoke(
        transactionReference: String,
        transactionDetails: TransactionDetails,
        initiatePaymentRequest: PayByTransferRequest
    ) = payByCardRepository.getBankTransferStatus(
        transactionReference,
        transactionDetails,
        initiatePaymentRequest
    )
}