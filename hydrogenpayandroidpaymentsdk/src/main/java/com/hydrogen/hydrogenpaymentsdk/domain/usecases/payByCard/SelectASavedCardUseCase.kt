package com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard

import com.hydrogen.hydrogenpaymentsdk.domain.models.SavedCard
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SelectASavedCardUseCase {
    operator fun invoke(
        selectedCard: SavedCard,
        savedCards: List<SavedCard>
    ): Flow<ViewState<List<SavedCard>>> {
        val updatedCards = savedCards.map { it.copy(isSelected = false) } // Reset all cards
        val updatedSelectedCard =
            updatedCards.map { if (it.id == selectedCard.id) it.copy(isSelected = true) else it }
        return flowOf(ViewState.success(updatedSelectedCard))
    }
}