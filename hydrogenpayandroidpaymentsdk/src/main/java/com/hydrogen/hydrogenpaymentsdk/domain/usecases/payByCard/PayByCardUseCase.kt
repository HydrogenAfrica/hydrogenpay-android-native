package com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard

import com.google.gson.Gson
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.CurrencyInfo
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.DeviceInformation
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.CardPaymentResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByCardResponseDomain
import com.hydrogen.hydrogenpaymentsdk.domain.repository.DataEncryptionContract
import com.hydrogen.hydrogenpaymentsdk.domain.repository.PayByCardRepository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_CARD_EXPIRY_DATE_SPACER
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_CARD_NUMBER_SPACER
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class PayByCardUseCase(
    private val payByCardRepository: PayByCardRepository,
    private val dataEncryptionContract: DataEncryptionContract,
    private val gson: Gson
) {
    operator fun invoke(
        customerEmail: String,
        providerId: String,
        cardNumber: String,
        cardExpiration: String,
        cvv: String,
        isCardSaved: Boolean,
        pin: String,
        deviceInformation: DeviceInformation,
        currencyInfo: CurrencyInfo
    ): Flow<ViewState<PayByCardResponseDomain?>> = payByCardRepository.initiateCardPayment(
        customerEmail,
        providerId,
        cardNumber.replace(STRING_CARD_NUMBER_SPACER, ""),
        cardExpiration.split(STRING_CARD_EXPIRY_DATE_SPACER).last(),
        cardExpiration.split(STRING_CARD_EXPIRY_DATE_SPACER).first(),
        cvv,
        isCardSaved,
        pin,
        deviceInformation,
        currencyInfo
    ).map { data ->
        val status = data.status
        val message = data.message

        data.content?.let {
            val decryptedResultAsString = dataEncryptionContract.decrypt(it)
            val result =
                gson.fromJson(decryptedResultAsString, CardPaymentResponse::class.java)
            ViewState.success(PayByCardResponseDomain(message, result)) as ViewState<PayByCardResponseDomain?>
        } ?: ViewState(status, null, message)
    }
}