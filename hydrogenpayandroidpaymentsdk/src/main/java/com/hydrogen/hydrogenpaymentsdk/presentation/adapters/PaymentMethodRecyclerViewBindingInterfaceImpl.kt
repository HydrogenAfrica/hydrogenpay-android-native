package com.hydrogen.hydrogenpaymentsdk.presentation.adapters

import androidx.databinding.ViewDataBinding
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.SelectPaymentMethodRvItemLayoutBinding
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentMethod

class PaymentMethodRecyclerViewBindingInterfaceImpl(private val paymentMethod: PaymentMethod): AppRecyclerViewBindingInterface {
    override fun bindData(view: ViewDataBinding) {
        (view as SelectPaymentMethodRvItemLayoutBinding).apply {
            this.paymentMethod = this@PaymentMethodRecyclerViewBindingInterfaceImpl.paymentMethod
            executePendingBindings()
        }
    }
}