package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models

data class ResponseData(
    val expiry_datetime: String,
    val request_id: String,
    val virtual_acct_name: String,
    val virtual_acct_no: String
)