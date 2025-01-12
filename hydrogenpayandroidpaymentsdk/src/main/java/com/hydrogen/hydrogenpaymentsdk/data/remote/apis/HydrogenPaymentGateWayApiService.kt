package com.hydrogen.hydrogenpaymentsdk.data.remote.apis

import com.hydrogen.hydrogenpaymentsdk.data.annotations.AuthorisedRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.HydrogenServerResponse
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferResponseDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PaymentConfirmationDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PaymentConfirmationRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface HydrogenPaymentGateWayApiService {
    @POST("Merchant/initiate-bank-transfer")
    @AuthorisedRequest
    suspend fun payByTransfer(
        @Body transferDetails: PayByTransferRequest
    ): Response<HydrogenServerResponse<PayByTransferResponseDto>>

    @POST("Merchant/confirm-payment")
    @AuthorisedRequest
    suspend fun paymentConfirmation(
        @Body paymentConfirmationRequestDTO: PaymentConfirmationRequestDTO
    ): Response<HydrogenServerResponse<PaymentConfirmationDto>>
}