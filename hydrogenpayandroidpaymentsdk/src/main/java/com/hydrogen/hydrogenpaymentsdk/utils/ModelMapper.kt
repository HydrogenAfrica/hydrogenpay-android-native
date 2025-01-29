package com.hydrogen.hydrogenpaymentsdk.utils

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.BankTransferStatusDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.InitiateBankTransferResponseDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.InitiatePayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.InitiatePaymentDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferResponseDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PaymentConfirmationDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PaymentMethodDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.TransactionDetailsDto
import com.hydrogen.hydrogenpaymentsdk.domain.enums.PaymentType
import com.hydrogen.hydrogenpaymentsdk.domain.models.HydrogenPayPaymentTransactionReceipt
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentMethod
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentTransactionCredentials
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
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
            description ?: "",
            fees.toString(),
            id,
            narration ?: "",
            formatTransactionDateTime(paidAt),
            paymentType,
            recurringCardToken,
            status,
            transactionRef,
            transactionStatus,
            vat.toString()
        )

    fun BankTransferStatusDto.toDomain(
        transactionDetails: TransactionDetails,
        initiatePaymentRequest: PayByTransferRequest
    ): PaymentConfirmationResponse =
        PaymentConfirmationResponse(
            transactionDetails.amount,
            transactionDetails.totalAmount,
            submitTimeUtc,
            transactionDetails.currencyInfo.currencySymbol,
            transactionDetails.customerEmail,
            transactionDetails.description,
            transactionDetails.serviceFees,
            transactionDetails.transactionId,
            initiatePaymentRequest.description ?: "",
            completedTimeUtc,
            PaymentType.BANK_TRANSFER.typeName,
            recurringCardToken ?: "",
            "$status<==>$responseDescription",
            transactionReference,
            transactionStatus,
            transactionDetails.vatFee
        )

    fun PaymentConfirmationResponse.getReceiptPayload(
        payByTransferResponse: InitiatePayByTransferResponse,
        merchantName: String
    ): HydrogenPayPaymentTransactionReceipt =
        HydrogenPayPaymentTransactionReceipt(
            amount,
            chargedAmount,
            createdAt,
            currency,
            customerEmail,
            description ?: "",
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
            "",
            payByTransferResponse.virtualAcctName,
            payByTransferResponse.virtualAcctName,
            merchantName
        )

    fun InitiatePaymentDto.toDomain(): PaymentTransactionCredentials =
        PaymentTransactionCredentials(transactionRef, url, getTransactionId(), getTransactionMode())

    fun TransactionDetailsDto.toDomain(): TransactionDetails =
        TransactionDetails(
            amount,
            bankDiscountValue,
            bankDiscountedAmount,
            billingMessage,
            callBackUrl,
            canRetry,
            currency,
            currencyInfo,
            customerEmail,
            customerFeePercentage,
            description,
            discountAmount,
            discountPercentage,
            frequency,
            isBankDiscountEnabled,
            isCardSpecificDiscount,
            isRecurring,
            isRecurringActive,
            merchantInfo,
            merchantRef,
            merchantServiceFee,
            orderId,
            otpOrBankTransferTimeoutLeft,
            paymentId,
            serviceFees,
            timeoutLeft,
            totalAmount,
            transactionId,
            transactionMode,
            transactionType,
            vatFee,
            vatPercentage
        )

    fun PaymentMethodDto.toDomain(): PaymentMethod = PaymentMethod(
        alias,
        description,
        displayOrder,
        id,
        image,
        isActive,
        isPageReroute,
        name,
        providerId,
        transactionLimit,
        type
    )

    fun InitiateBankTransferResponseDto.toDomain(): InitiatePayByTransferResponse =
        InitiatePayByTransferResponse(
            response_data.expiry_datetime,
            response_data.request_id,
            response_data.virtual_acct_name,
            response_data.virtual_acct_no
        )
}