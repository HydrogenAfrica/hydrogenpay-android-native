package com.hydrogen.hydrogenpaymentsdk.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.FragmentSavedCardsBottomSheetDailogListDialogItemBinding
import com.hydrogen.hydrogenpaymentsdk.domain.models.SavedCard
import com.hydrogen.hydrogenpaymentsdk.presentation.interactors.SavedCardsSelectListener

class SavedCardsRecyclerViewAdapter(
    private val savedCardSelectListener: SavedCardsSelectListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data: ArrayList<SavedCard> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<SavedCard>) {
        val diff = GenericRecyclerViewDiffUtil(newData, data)
        val diffResult = DiffUtil.calculateDiff(diff)
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemBinding =
            DataBindingUtil.inflate<FragmentSavedCardsBottomSheetDailogListDialogItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.fragment_saved_cards_bottom_sheet_dailog_list_dialog_item,
                parent,
                false
            )
        return SavedCardsRecyclerViewViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val bindingData: SavedCardsRecyclerViewBindingInterfaceImpl? =
            SavedCardsRecyclerViewBindingInterfaceImpl(data[position])
        bindingData?.let {
            (holder as SavedCardsRecyclerViewViewHolder).apply {
                bind(it)
                holder.itemView.findViewById<RadioButton>(R.id.radioButton).setOnClickListener {
                    savedCardSelectListener.onCardSelected(data[position])
                }

                holder.itemView.findViewById<ImageView>(R.id.deleteIcon).setOnClickListener {
                    savedCardSelectListener.onDeleteCard(data[position])
                }
            }
        }
    }
}