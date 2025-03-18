package com.hydrogen.hydrogenpaymentsdk.presentation.adapters

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class SavedCardsRecyclerViewViewHolder(private val itemViewDataBinding: ViewDataBinding) :
    RecyclerView.ViewHolder(itemViewDataBinding.root) {
    fun bind(bindingInterface: AppRecyclerViewBindingInterface) {
        bindingInterface.bindData(itemViewDataBinding)
    }
}