package com.hydrogen.hydrogenpaymentsdk.data.remote.apis

import com.hydrogen.hydrogenpaymentsdk.data.annotations.AuthorizedRequestModeHeader
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.HydrogenServerResponseResource
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.CardPaymentRequestDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.GetBankTransferStatusRequestBody
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.GetCardProviderRequestDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.OtpValidationRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.ResendOTPRequestDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.TransactionStatusResponseDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.CardProviderResponse
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.OtpValidationResponseDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.ResendOTPResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PayByCardApiService {
    @POST("get-card-provider")
    @AuthorizedRequestModeHeader
    suspend fun getCardProvider(
        @Body getCardProviderRequestBody: GetCardProviderRequestDto
    ): Response<HydrogenServerResponseResource<CardProviderResponse>>

    @POST("Payment/purchase")
    @AuthorizedRequestModeHeader
    suspend fun initiateCardPayment(@Body cardPaymentRequestDto: CardPaymentRequestDto): Response<HydrogenServerResponseResource<String>>

    @POST("Payment/validate-otp")
    suspend fun validateOtpCode(@Body otpValidationRequestDTO: OtpValidationRequestDTO): Response<HydrogenServerResponseResource<OtpValidationResponseDTO>>

    @POST("Payment/resend-otp")
    suspend fun resendOtpCode(
        @Body resendOTPRequestDto: ResendOTPRequestDto
    ): Response<HydrogenServerResponseResource<ResendOTPResponseDto>>

    @POST("Payment/confirm-status")
    suspend fun confirmStatus(
        @Body bankTransferStatusRequestBody: GetBankTransferStatusRequestBody
    ): Response<HydrogenServerResponseResource<TransactionStatusResponseDto>>
}