package com.hydrogen.hydrogenpaymentsdk.domain.repository

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.CurrencyInfo
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.DeviceInformation
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.HydrogenServerResponseResource
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.DeleteSavedCardRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.CardProviderResponse
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.GetSavedCardsResponseDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.OtpValidationResponseDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.ResendOTPResponseDto
import com.hydrogen.hydrogenpaymentsdk.domain.models.SavedCard
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionStatus
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body

internal interface PayByCardRepository {
    fun getCardProvider(cardNumber: String): Flow<ViewState<CardProviderResponse?>>
    fun initiateCardPayment(
        customerEmail: String,
        providerId: String,
        cardNumber: String,
        expiryYear: String,
        expiryMonth: String,
        cvv: String,
        isCardSaved: Boolean,
        pin: String,
        deviceInformation: DeviceInformation,
        currencyInfo: CurrencyInfo
    ): Flow<ViewState<String?>>

    fun validateOtpCode(
        otpCode: String,
        providerId: String
    ): Flow<ViewState<OtpValidationResponseDTO?>>

    fun resendOtpCode(
        currency: String, providerId: String
    ): Flow<ViewState<ResendOTPResponseDto?>>

    fun getBankTransferStatus(
        transactionReference: String,
        transactionDetails: TransactionDetails,
        initiatePaymentRequest: PayByTransferRequest
    ): Flow<ViewState<TransactionStatus?>>

    fun getSavedCards(
        merchantRef: String,
        customerEmail: String,
        transactionId: String
    ): Flow<ViewState<List<SavedCard>?>>

    suspend fun deleteASavedCards(
        cardId: String
    ): Flow<ViewState<Boolean>>
}