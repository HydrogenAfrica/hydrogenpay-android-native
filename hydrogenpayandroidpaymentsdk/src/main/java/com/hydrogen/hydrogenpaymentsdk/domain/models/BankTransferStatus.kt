package com.hydrogen.hydrogenpaymentsdk.domain.models

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.CurrencyInfo
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.MerchantInfo

data class BankTransferStatus(
    val accountName: String,
    val accountNo: String,
    val amount: String,
    val bank: String,
    val callBackUrl: String,
    val canRetry: Boolean,
    val cardExpiry: String?,
    val channelTransactionReference: String?,
    val clientReferenceInformation: String,
    val completedTimeUtc: String,
    val customerName: String,
    val email: String?,
    val errors: Any?,
    val expMonth: String?,
    val expYear: String?,
    val maskedPan: String?,
    val processorResponse: Any?,
    val processorTransactionId: String?,
    val reconciliationId: String,
    val recurringCardToken: String?,
    val remittanceAmount: String,
    val responseCode: String,
    val responseDescription: String,
    val status: String,
    val submitTimeUtc: String,
    val transactionId: String,
    val transactionReference: String,
    val transactionStatus: String,
    val transactionType: String,
    val currencyInfo: CurrencyInfo,
    val merchantInfo: MerchantInfo,
    val narration: String?
)
