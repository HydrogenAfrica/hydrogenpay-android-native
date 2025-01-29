package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos

data class GetBankTransferStatusRequestBody(
    val transactionId: String,
    val IsSendEmailCustomer: Boolean = true,
    val IsSendEmailMerchant: Boolean = true,
)