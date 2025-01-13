package com.hydrogen.hydrogenpaymentsdk.utils

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferResponseDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PaymentConfirmationDto
import com.hydrogen.hydrogenpaymentsdk.domain.models.HydrogenPayPaymentTransactionReceipt
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.formatTransactionDateTime

object ModelMapper {
    fun PayByTransferResponseDto.toDomain(): PayByTransferResponse =
        PayByTransferResponse(
            amountPaid,
            bankName,
            expiryDateTime,
            transactionRef,
            transactionStatus,
            virtualAccountName,
            virtualAccountNo
        )

    fun PaymentConfirmationDto.toDomain(): PaymentConfirmationResponse =
        PaymentConfirmationResponse(
            amount.toString(),
            chargedAmount.toString(),
            formatTransactionDateTime(createdAt),
            currency,
            customerEmail,
            description,
            fees.toString(),
            id,
            narration,
            formatTransactionDateTime(paidAt),
            paymentType,
            recurringCardToken,
            status,
            transactionRef,
            transactionStatus,
            vat.toString()
        )

    fun PaymentConfirmationResponse.getReceiptPayload(
        payByTransferResponse: PayByTransferResponse,
        merchantName: String
    ): HydrogenPayPaymentTransactionReceipt =
        HydrogenPayPaymentTransactionReceipt(
            amount,
            chargedAmount,
            createdAt,
            currency,
            customerEmail,
            description,
            fees,
            id,
            narration,
            paidAt,
            paymentType,
            recurringCardToken,
            status,
            transactionRef,
            transactionStatus,
            vat,
            payByTransferResponse.bankName,
            payByTransferResponse.virtualAccountName,
            payByTransferResponse.virtualAccountNo,
            merchantName
        )
}