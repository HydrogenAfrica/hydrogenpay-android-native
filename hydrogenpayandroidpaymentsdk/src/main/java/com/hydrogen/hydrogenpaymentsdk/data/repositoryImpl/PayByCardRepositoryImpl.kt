package com.hydrogen.hydrogenpaymentsdk.data.repositoryImpl

import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SessionManagerContract
import com.hydrogen.hydrogenpaymentsdk.data.remote.apis.PayByCardApiService
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.CardDetails
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.CardNumberDetails
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.CurrencyInfo
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.DeviceInformation
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.CardPaymentRequestDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.DeleteSavedCardRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.GetBankTransferStatusRequestBody
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.GetCardProviderRequestDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.GetSavedCardsRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.OtpValidationRequestDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.ResendOTPRequestDto
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.CardProviderResponse
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.OtpValidationResponseDTO
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.ResendOTPResponseDto
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesGson
import com.hydrogen.hydrogenpaymentsdk.domain.models.SavedCard
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionStatus
import com.hydrogen.hydrogenpaymentsdk.domain.repository.DataEncryptionContract
import com.hydrogen.hydrogenpaymentsdk.domain.repository.PayByCardRepository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_RELAUNCH_SDK_MESSAGE
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_SERVER_RESPONSE_SUCCESS
import com.hydrogen.hydrogenpaymentsdk.utils.CardPaymentUtil
import com.hydrogen.hydrogenpaymentsdk.utils.ExtensionFunctions.retryAndCatchExceptions
import com.hydrogen.hydrogenpaymentsdk.utils.ModelMapper.toDomain
import com.hydrogen.hydrogenpaymentsdk.utils.NetworkUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class PayByCardRepositoryImpl(
    private val payByCardApiService: PayByCardApiService,
    private val networkUtil: NetworkUtil,
    private val sessionManager: SessionManagerContract,
    private val dataEncryption: DataEncryptionContract
) : PayByCardRepository {
    override fun getCardProvider(cardNumber: String): Flow<ViewState<CardProviderResponse?>> =
        flow {
            val transactionId = sessionManager.getSessionTransactionCredentials()?.transactionId
            transactionId?.let { transId ->
                val cardDetailsAsString =
                    providesGson().toJson(CardNumberDetails(cardNumber, transId))
                val encryptedCardDetails = dataEncryption.encrypt(cardDetailsAsString)
                val cardProviderRequest = GetCardProviderRequestDto(encryptedCardDetails, transId)
                val response = payByCardApiService.getCardProvider(cardProviderRequest)
                emit(networkUtil.getServerResponse(response))
            } ?: run {
                emit(ViewState.error(null, STRING_RELAUNCH_SDK_MESSAGE))
            }
        }.map { data ->
            val result = data.content?.data
            ViewState(
                status = data.status,
                content = result,
                message = data.message
            ) as ViewState<CardProviderResponse?>
        }.retryAndCatchExceptions(networkUtil)

    override fun initiateCardPayment(
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
    ): Flow<ViewState<String?>> =
        flow {
            val transactionId = sessionManager.getSessionTransactionCredentials()?.transactionId
            transactionId?.let {
                val cardDetails = CardDetails(
                    cardNumber,
                    cvv,
                    expiryMonth,
                    expiryYear,
                    it,
                    pin,
                    CardPaymentUtil.getCardType(cardNumber).code
                )
                val cardDetailsAsString = providesGson().toJson(cardDetails)
                val encryptedCardDetails =
                    dataEncryption.encrypt(cardDetailsAsString)
                val cardPaymentRequest = CardPaymentRequestDto(
                    encryptedCardDetails,
                    "",
                    currencyInfo.alpha2Code,
                    customerEmail,
                    deviceInformation,
                    CardPaymentUtil.getLocalIpAddress() ?: "",
                    isCardSaved,
                    providerId,
                    transactionId
                )
                val response = payByCardApiService.initiateCardPayment(cardPaymentRequest)
                val serverResponse = networkUtil.getServerResponse(response)
                val result = ViewState(
                    message = response.body()?.message ?: serverResponse.message,
                    status = serverResponse.status,
                    content = serverResponse.content
                )
                emit(result)
            }
        }.map {
            val result = it.content?.data
            ViewState(
                status = it.status,
                content = result,
                message = it.message
            ) as ViewState<String?>
        }.retryAndCatchExceptions(networkUtil)

    override fun validateOtpCode(
        otpCode: String,
        providerId: String
    ): Flow<ViewState<OtpValidationResponseDTO?>> =
        flow {
            val transactionId = sessionManager.getSessionTransactionCredentials()?.transactionId
            transactionId?.let {
                val validateOtpRequest = OtpValidationRequestDTO(otpCode, providerId, it)
                val response = payByCardApiService.validateOtpCode(validateOtpRequest)
                val serverResponse = networkUtil.getServerResponse(response)
                val result = ViewState(
                    message = serverResponse.content?.message ?: serverResponse.message,
                    status = serverResponse.status,
                    content = serverResponse.content
                )
                emit(result)
            } ?: run { ViewState.error(null, STRING_RELAUNCH_SDK_MESSAGE) }
        }.map {
            val result = it.content?.data
            ViewState(it.status, result, it.message) as ViewState<OtpValidationResponseDTO?>
        }.retryAndCatchExceptions(networkUtil)

    override fun resendOtpCode(
        currency: String,
        providerId: String
    ): Flow<ViewState<ResendOTPResponseDto?>> =
        flow {
            val transactionId = sessionManager.getSessionTransactionCredentials()?.transactionId
            transactionId?.let {
                val resendOTPRequestDto = ResendOTPRequestDto(currency, providerId, it)
                val result = payByCardApiService.resendOtpCode(resendOTPRequestDto)
                emit(networkUtil.getServerResponse(result))
            } ?: run {
                emit(ViewState.error(null, STRING_RELAUNCH_SDK_MESSAGE))
            }
        }.map {
            val result = it.content?.data
            ViewState(it.status, result, it.message) as ViewState<ResendOTPResponseDto?>
        }.retryAndCatchExceptions(networkUtil)

    override fun getBankTransferStatus(
        transactionReference: String,
        transactionDetails: TransactionDetails,
        initiatePaymentRequest: PayByTransferRequest
    ): Flow<ViewState<TransactionStatus?>> =
        flow {
            val request = GetBankTransferStatusRequestBody(transactionReference)
            val response = payByCardApiService.confirmStatus(request)
            emit(networkUtil.getServerResponse(response))
        }.map { data ->
            val result = data.content?.data?.toDomain(transactionDetails, true)
            ViewState(
                status = data.status,
                content = result,
                message = data.message
            ) as ViewState<TransactionStatus?>
        }

    override fun getSavedCards(
        merchantRef: String,
        customerEmail: String,
        transactionId: String
    ): Flow<ViewState<List<SavedCard>?>> =
        flow {
            val getSavedCardRequest =
                GetSavedCardsRequestDTO(customerEmail, merchantRef, transactionId)
            emit(
                networkUtil.getServerResponse(
                    payByCardApiService.getSavedCards(
                        getSavedCardRequest
                    )
                )
            )
        }.map { data ->
            val result = data.content?.data?.map { it.toDomain() }
            ViewState(
                status = data.status,
                content = result,
                message = data.message
            ) as ViewState<List<SavedCard>?>
        }

    override suspend fun deleteASavedCards(cardId: String): Flow<ViewState<Boolean>> =
        flow {
            val deleteCardRequest = DeleteSavedCardRequestDTO(cardId)
            val response = payByCardApiService.deleteASavedCards(deleteCardRequest)

            val viewState = if (response.isSuccessful && response.body() != null) {
                if (response.body()!!.message.contains(STRING_SERVER_RESPONSE_SUCCESS, true)) {
                    ViewState.success(true)
                } else ViewState.error(false, response.message())
            } else ViewState.error(false, response.message())

            emit(viewState)
        }
}