package com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard

import com.hydrogen.hydrogenpaymentsdk.domain.models.SavedCard
import com.hydrogen.hydrogenpaymentsdk.domain.repository.PayByCardRepository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

internal class GetSavedCardsUseCase(
    private val cardRepository: PayByCardRepository
) {
    operator fun invoke(
        merchantRef: String,
        customerEmail: String,
        transactionId: String
    ): Flow<ViewState<List<SavedCard>?>> = cardRepository.getSavedCards(merchantRef, customerEmail, transactionId)
}