package com.hydrogen.hydrogenpaymentsdk.utils

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferResponseDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PaymentConfirmationDto
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse

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
            vat
        )
}