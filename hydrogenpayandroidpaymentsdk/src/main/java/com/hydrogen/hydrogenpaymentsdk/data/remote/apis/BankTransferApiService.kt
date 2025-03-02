package com.hydrogen.hydrogenpaymentsdk.data.remote.apis

import com.hydrogen.hydrogenpaymentsdk.data.annotations.AuthorisedRequest
import com.hydrogen.hydrogenpaymentsdk.data.annotations.AuthorizedRequestModeHeader
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.HydrogenServerResponseResource
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.GetBankTransferStatusRequestBody
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.InitiatePayByTransferRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.PaymentConfirmationRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.InitiateBankTransferResponseDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.InitiatePaymentResponseDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.PaymentConfirmationResponseDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.TransactionStatusResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

internal interface BankTransferApiService {
    @POST("Payment/initiate-bank-transfer")
    @AuthorizedRequestModeHeader
    suspend fun payByTransfer(
        @Body transferDetails: InitiatePayByTransferRequestDTO
    ): Response<HydrogenServerResponseResource<InitiateBankTransferResponseDto>>

    @POST("Merchant/initiate-bank-transfer")
    @AuthorisedRequest
    suspend fun initiatePayByTransfer(
        @Body transferDetails: PayByTransferRequest
    ): Response<HydrogenServerResponseResource<InitiatePaymentResponseDto>>

    @POST("Payment/bank-transfer-status")
    @AuthorizedRequestModeHeader
    suspend fun checkBankTransferStatus(
        @Body bankTransferStatusRequestBody: GetBankTransferStatusRequestBody
    ): Response<HydrogenServerResponseResource<TransactionStatusResponseDto>>
}