package com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard

import com.hydrogen.hydrogenpaymentsdk.domain.models.SavedCard
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DeleteSavedCardUseCase {
    operator fun invoke(savedCard: SavedCard, savedCards: List<SavedCard>): Flow<ViewState<List<SavedCard>>> {
        val resetCards = savedCards.map { it.copy(isSelected = false) }
        val updatedSavedCard = resetCards.filter { it.id != savedCard.id }
        return flowOf(ViewState.success(updatedSavedCard))
    }
}