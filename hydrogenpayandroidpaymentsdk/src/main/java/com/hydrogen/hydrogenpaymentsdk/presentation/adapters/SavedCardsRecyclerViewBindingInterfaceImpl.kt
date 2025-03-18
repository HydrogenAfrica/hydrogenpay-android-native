package com.hydrogen.hydrogenpaymentsdk.presentation.adapters

import androidx.databinding.ViewDataBinding
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.FragmentSavedCardsBottomSheetDailogListDialogItemBinding
import com.hydrogen.hydrogenpaymentsdk.domain.models.SavedCard
import com.hydrogen.hydrogenpaymentsdk.presentation.interactors.SavedCardsSelectListener

class SavedCardsRecyclerViewBindingInterfaceImpl(private val savedCard: SavedCard, private val savedCardsSelectListener: SavedCardsSelectListener): AppRecyclerViewBindingInterface {
    override fun bindData(view: ViewDataBinding) {
        (view as FragmentSavedCardsBottomSheetDailogListDialogItemBinding).apply {
            savedCard = this@SavedCardsRecyclerViewBindingInterfaceImpl.savedCard
            this.radioButton.setOnClickListener {
                savedCardsSelectListener.onCardSelected(savedCard)
            }
            this.deleteIcon.setOnClickListener {
                savedCardsSelectListener.onDeleteCard(savedCard)
            }
            executePendingBindings()
        }
    }
}