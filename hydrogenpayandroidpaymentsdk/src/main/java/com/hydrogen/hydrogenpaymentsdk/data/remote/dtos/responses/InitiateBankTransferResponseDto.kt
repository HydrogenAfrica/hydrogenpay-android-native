package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.ResponseData

data class InitiateBankTransferResponseDto(
    val response_code: String,
    val response_data: ResponseData,
    val response_message: String
)