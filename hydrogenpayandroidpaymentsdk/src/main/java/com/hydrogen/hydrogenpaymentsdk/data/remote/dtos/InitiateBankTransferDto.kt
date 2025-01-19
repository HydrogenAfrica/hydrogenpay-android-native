package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos

data class InitiateBankTransferDto(
    val expiry_datetime: String,
    val request_id: String,
    val virtual_acct_name: String,
    val virtual_acct_no: String
)