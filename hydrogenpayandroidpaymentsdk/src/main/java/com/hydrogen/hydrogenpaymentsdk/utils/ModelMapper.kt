package com.hydrogen.hydrogenpaymentsdk.utils

import android.util.Log
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.PaymentMethodDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.HydrogenPayPaymentRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.GetSavedCardsResponseDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.InitiateBankTransferResponseDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.InitiatePayByTransferResponseDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.InitiatePaymentResponseDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.OtpValidationResponseDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.PayByTransferResponseDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.PaymentConfirmationResponseDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.ResendOTPResponseData
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.TransactionDetailsDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.TransactionStatusResponseDto
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesGson
import com.hydrogen.hydrogenpaymentsdk.domain.enums.PaymentType
import com.hydrogen.hydrogenpaymentsdk.domain.models.HydrogenPayPaymentTransactionReceipt
import com.hydrogen.hydrogenpaymentsdk.domain.models.OTPValidationProcessorResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.OtpValidationResponseDomain
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentMethod
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentTransactionCredentials
import com.hydrogen.hydrogenpaymentsdk.domain.models.ResendOTPProcessorResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.ResendOTPResponseDataDomain
import com.hydrogen.hydrogenpaymentsdk.domain.models.SavedCard
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionStatus
import com.hydrogen.hydrogenpaymentsdk.domain.repository.DataEncryptionContract
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.formatNumberWithCommas
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.formatTransactionDateTime
import com.hydrogen.hydrogenpaymentsdk.utils.ExtensionFunctions.toDecimalPlace

internal object ModelMapper {
    fun HydrogenPayPaymentRequest.toPayByTransferRequestObject(): PayByTransferRequest =
        PayByTransferRequest(
            amount, customerName, email, callback, transactionRef, currency, description, meta
        )

    fun PayByTransferResponseDto.paymentConfirmationResponse(): PayByTransferResponse =
        PayByTransferResponse(
            amountPaid,
            bankName,
            expiryDateTime,
            transactionRef,
            transactionStatus,
            virtualAccountName,
            virtualAccountNo
        )

    fun PaymentConfirmationResponseDto.paymentConfirmationResponse(): PaymentConfirmationResponse =
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

    fun TransactionStatusResponseDto.toDomain(
        transactionDetails: TransactionDetails,
        isPayByCard: Boolean = false
    ): TransactionStatus =
        TransactionStatus(
            accountName ?: "",
            accountNo ?: "",
            if (isPayByCard) formatNumberWithCommas(
                (amount / 100).toDecimalPlace().toDouble()
            ) else amount.toString(),
            bank,
            callBackUrl,
            canRetry,
            cardExpiry,
            channelTransactionReference,
            clientReferenceInformation ?: "",
            completedTimeUtc,
            customerName ?: "",
            email,
            errors,
            expMonth,
            expYear,
            maskedPan,
            processorResponse,
            processorTransactionId,
            reconciliationId ?: "",
            recurringCardToken,
            remittanceAmount.toString(),
            responseCode,
            responseDescription,
            status ?: "",
            submitTimeUtc,
            transactionId,
            transactionReference,
            transactionStatus ?: "",
            PaymentType.BANK_TRANSFER.typeName,
            transactionDetails.currencyInfo,
            transactionDetails.merchantInfo,
            transactionDetails.description
        )

    fun TransactionStatus.getReceiptPayload(
        paymentType: String
    ): HydrogenPayPaymentTransactionReceipt =
        HydrogenPayPaymentTransactionReceipt(
            amount,
            submitTimeUtc,
            completedTimeUtc,
            transactionReference,
            paymentType,
            responseDescription,
            responseCode,
            bank,
            processorResponse,
            recurringCardToken,
            accountName,
            accountNo
        )

    fun InitiatePaymentResponseDto.paymentConfirmationResponse(): PaymentTransactionCredentials =
        PaymentTransactionCredentials(transactionRef, url, getTransactionId(), getTransactionMode())

    fun TransactionDetailsDto.paymentConfirmationResponse(): TransactionDetails =
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

    fun PaymentMethodDto.paymentConfirmationResponse(): PaymentMethod = PaymentMethod(
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

    fun InitiateBankTransferResponseDto.paymentConfirmationResponse(): InitiatePayByTransferResponseDTO =
        InitiatePayByTransferResponseDTO(
            response_data.expiry_datetime,
            response_data.request_id,
            response_data.virtual_acct_name,
            response_data.virtual_acct_no
        )

    fun OtpValidationResponseDTO.toDomain(): OtpValidationResponseDomain =
        OtpValidationResponseDomain(
            amount,
            errors,
            message,
            otpRetryCount,
            panLast4Digits,
            providesGson().fromJson(processorResponse, OTPValidationProcessorResponse::class.java),
            responseCode,
            token,
            tokenExpiryDate,
            transactionId,
            transactionIdentifier,
            transactionRef
        )

    fun ResendOTPResponseData.toDomain(): ResendOTPResponseDataDomain =
        ResendOTPResponseDataDomain(
            amount,
            approvalCode,
            cardType,
            clientReferenceInformationCode,
            errors,
            message,
            paymentId,
            providesGson().fromJson(processorResponse, ResendOTPProcessorResponse::class.java),
            reConciliationId,
            resendOtpRetryCount,
            responseCode,
            status,
            supportMessage,
            terminalId,
            transactionId,
            transactionRef
        )

    fun GetSavedCardsResponseDTO.toDomain(dataEncryption: DataEncryptionContract): SavedCard {
        Log.d("INCOMING_CVV", cvv)
        val newCvv = dataEncryption.decrypt(cvv)
        Log.d("INCOMING_CVV_2", newCvv)
        return SavedCard(
            cardScheme,
            cardToken,
            dataEncryption.decrypt(cvv),
            discountPercentage,
            dataEncryption.decrypt(expiryMonth),
            dataEncryption.decrypt(expiryYear),
            id,
            isCardExpired,
            isCardSpecificDiscount,
            isLastUsed,
            isPinRequired,
            maskedPan,
            providerId
        )
    }
}