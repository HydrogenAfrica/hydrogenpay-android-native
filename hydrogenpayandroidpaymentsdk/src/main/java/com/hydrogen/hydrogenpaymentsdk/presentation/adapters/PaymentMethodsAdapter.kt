package com.hydrogen.hydrogenpaymentsdk.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.SelectPaymentMethodRvItemLayoutBinding
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentMethod

typealias PaymentMethodOnSelectListener = (clickedData: PaymentMethod) -> Unit

class PaymentMethodsAdapter(
    private val paymentMethodClickListener: PaymentMethodOnSelectListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isLoading = true
    private var data: MutableList<PaymentMethod> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: ArrayList<PaymentMethod>) {
        val diff = GenericRecyclerViewDiffUtil(newData, data)
        val diffResult = DiffUtil.calculateDiff(diff)
        isLoading = false
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.select_payment_method_place_holder_layout, parent, false)
            ShimmerViewHolder(view)
        } else {
            val itemBinding = DataBindingUtil.inflate<SelectPaymentMethodRvItemLayoutBinding>(
                LayoutInflater.from(parent.context),
                R.layout.select_payment_method_rv_item_layout,
                parent,
                false,
            )
            DataViewHolder(itemBinding)
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading) 4 else data.size // Show 5 shimmer items while loading
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DataViewHolder) {
            val bindingData: PaymentMethodRecyclerViewBindingInterfaceImpl? =
                PaymentMethodRecyclerViewBindingInterfaceImpl(data[position])
            bindingData?.let {
                holder.apply {
                    bind(bindingData)
                    itemView.setOnClickListener {
                        data[position].let { it1 -> paymentMethodClickListener.invoke(it1) }
                    }
                }
            }
        }
    }

    inner class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class DataViewHolder(val view: ViewDataBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(
            bindingInterface: AppRecyclerViewBindingInterface,
        ) {
            bindingInterface.bindData(view)
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<PaymentMethod>() {
            override fun areItemsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean =
                oldItem.alias == newItem.alias

            override fun areContentsTheSame(
                oldItem: PaymentMethod,
                newItem: PaymentMethod
            ): Boolean =
                oldItem == newItem
        }
    }
}
