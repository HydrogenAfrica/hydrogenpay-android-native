package com.hydrogen.hydrogenpaymentsdk.data.remote.apis

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.HydrogenServerResponse
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PaymentConfirmationDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PaymentConfirmationRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface HydrogenPaymentGateWayApiService {
    @POST("confirm-payment")
    fun paymentConfirmation(
        @Body paymentConfirmationRequestDTO: PaymentConfirmationRequestDTO
    ): Response<HydrogenServerResponse<PaymentConfirmationDto>>
}