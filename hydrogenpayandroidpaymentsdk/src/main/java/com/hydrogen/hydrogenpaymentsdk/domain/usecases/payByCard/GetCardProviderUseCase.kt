package com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.CardProviderResponse
import com.hydrogen.hydrogenpaymentsdk.domain.repository.PayByCardRepository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

internal class GetCardProviderUseCase(
    private val payByCardRepository: PayByCardRepository
) {
    operator fun invoke(cardNumber: String): Flow<ViewState<CardProviderResponse?>> = payByCardRepository.getCardProvider(cardNumber)
}