package com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.CurrencyInfo
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.DeviceInformation
import com.hydrogen.hydrogenpaymentsdk.domain.repository.PayByCardRepository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

internal class PayByCardUseCase(
    private val payByCardRepository: PayByCardRepository
) {
    operator fun invoke(
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
    ): Flow<ViewState<String?>> = payByCardRepository.initiateCardPayment(
        customerEmail,
        providerId,
        cardNumber,
        expiryYear,
        expiryMonth,
        cvv,
        isCardSaved,
        pin,
        deviceInformation,
        currencyInfo
    )
}