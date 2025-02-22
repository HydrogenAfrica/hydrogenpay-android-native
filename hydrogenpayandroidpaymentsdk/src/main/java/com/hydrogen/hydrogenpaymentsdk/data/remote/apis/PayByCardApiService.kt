package com.hydrogen.hydrogenpaymentsdk.data.remote.apis

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.HydrogenServerResponseResource
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.CardPaymentRequestDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.GetBankTransferStatusRequestBody
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.GetCardProviderRequestDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.OtpValidationRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.BankTransferStatusResponseDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.CardProviderResponse
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.OtpValidationResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PayByCardApiService {
    @POST("bepay/api/v1/get-card-provider")
    suspend fun getCardProvider(
        @Body getCardProviderRequestBody: GetCardProviderRequestDto
    ): Response<HydrogenServerResponseResource<CardProviderResponse>>

    @POST("bepay/api/v1/Payment/purchase")
    suspend fun initiateCardPayment(@Body cardPaymentRequestDto: CardPaymentRequestDto): Response<HydrogenServerResponseResource<String>>

    @POST("")
    suspend fun validateOtpCode(@Body otpValidationRequestDTO: OtpValidationRequestDTO): Response<HydrogenServerResponseResource<OtpValidationResponseDTO>>

    @POST("bepay/api/v1/Payment/confirm-status")
    suspend fun confirmStatus(
        @Body bankTransferStatusRequestBody: GetBankTransferStatusRequestBody
    ): Response<HydrogenServerResponseResource<BankTransferStatusResponseDto>>
}