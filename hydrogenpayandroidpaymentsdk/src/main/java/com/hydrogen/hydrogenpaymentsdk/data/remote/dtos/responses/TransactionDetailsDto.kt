package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.CurrencyInfo
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.MerchantInfo

data class TransactionDetailsDto(
    val amount: String,
    val bankDiscountValue: String?,
    val bankDiscountedAmount: String,
    val billingMessage: String?,
    val callBackUrl: String,
    val canRetry: Boolean,
    val currency: String?,
    val currencyInfo: CurrencyInfo,
    val customerEmail: String,
    val customerFeePercentage: Int,
    val description: String,
    val discountAmount: String,
    val discountPercentage: Int,
    val frequency: String?,
    val isBankDiscountEnabled: Boolean,
    val isCardSpecificDiscount: Boolean,
    val isRecurring: Boolean,
    val isRecurringActive: String?,
    val merchantInfo: MerchantInfo,
    val merchantRef: String,
    val merchantServiceFee: String,
    val orderId: String,
    val otpOrBankTransferTimeoutLeft: String,
    val paymentId: String?,
    val serviceFees: String,
    val timeoutLeft: String,
    val totalAmount: String,
    val transactionId: String,
    val transactionMode: Int,
    val transactionType: Int,
    val vatFee: String,
    val vatPercentage: Double
)