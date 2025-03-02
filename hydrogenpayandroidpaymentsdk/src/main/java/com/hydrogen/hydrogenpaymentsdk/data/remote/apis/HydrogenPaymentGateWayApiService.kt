package com.hydrogen.hydrogenpaymentsdk.data.remote.apis

import com.hydrogen.hydrogenpaymentsdk.data.annotations.AuthorisedRequest
import com.hydrogen.hydrogenpaymentsdk.data.annotations.AuthorizedRequestModeHeader
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.HydrogenServerResponseResource
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.PaymentMethodDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.TransactionDetailsRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.InitiatePaymentResponseDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.TransactionDetailsDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface HydrogenPaymentGateWayApiService {
    @POST("Merchant/initiate-payment")
    @AuthorisedRequest
    suspend fun initiatePayment(
        @Body initiateTransferRequest: PayByTransferRequest
    ): Response<HydrogenServerResponseResource<InitiatePaymentResponseDto>>

    @GET("Payment/get-payment-methods")
    @AuthorizedRequestModeHeader
    suspend fun getPaymentMethod(
        @Query("transactionId") transactionId: String
    ): Response<HydrogenServerResponseResource<List<PaymentMethodDto>>>

    @POST("payment/transaction-details")
    @AuthorizedRequestModeHeader
    suspend fun getTransactionDetails(
        @Body transactionDetailsRequest: TransactionDetailsRequestDTO
    ): Response<HydrogenServerResponseResource<TransactionDetailsDto>>
}