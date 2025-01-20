package com.hydrogen.hydrogenpaymentsdk.data.remote.apis

import com.hydrogen.hydrogenpaymentsdk.data.annotations.AuthorisedRequest
import com.hydrogen.hydrogenpaymentsdk.data.annotations.AuthorizedRequestModeHeader
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.HydrogenServerResponse
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.InitiatePaymentDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferResponseDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PaymentConfirmationDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PaymentConfirmationRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PaymentMethodDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.TransactionDetailsDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.TransactionDetailsRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface HydrogenPaymentGateWayApiService {
    @POST("Merchant/initiate-payment")
    @AuthorisedRequest
    suspend fun initiatePayment(
        @Body initiateTransferRequest: PayByTransferRequest
    ): Response<HydrogenServerResponse<InitiatePaymentDto>>

    @GET("Payment/get-payment-methods")
    @AuthorizedRequestModeHeader
    suspend fun getPaymentMethod(
        @Query("transactionId") transactionId: String
    ): Response<HydrogenServerResponse<List<PaymentMethodDto>>>

    @POST("payment/transaction-details")
    @AuthorizedRequestModeHeader
    suspend fun getTransactionDetails(
        @Body transactionDetailsRequest: TransactionDetailsRequest
    ): Response<HydrogenServerResponse<TransactionDetailsDto>>

    @POST("Merchant/initiate-bank-transfer")
    @AuthorisedRequest
    suspend fun payByTransfer(
        @Body transferDetails: PayByTransferRequest
    ): Response<HydrogenServerResponse<PayByTransferResponseDto>>

    @POST("Merchant/initiate-bank-transfer")
    @AuthorisedRequest
    suspend fun initiatePayByTransfer(
        @Body transferDetails: PayByTransferRequest
    ): Response<HydrogenServerResponse<InitiatePaymentDto>>

    @POST("Merchant/confirm-payment")
    @AuthorisedRequest
    suspend fun paymentConfirmation(
        @Body paymentConfirmationRequestDTO: PaymentConfirmationRequestDTO
    ): Response<HydrogenServerResponse<PaymentConfirmationDto>>
}