package com.hydrogen.hydrogenpaymentsdk.presentation.interactors

import com.hydrogen.hydrogenpaymentsdk.domain.models.SavedCard

interface SavedCardsSelectListener {
    fun onCardSelected(card: SavedCard)
    fun onDeleteCard(card: SavedCard)
}