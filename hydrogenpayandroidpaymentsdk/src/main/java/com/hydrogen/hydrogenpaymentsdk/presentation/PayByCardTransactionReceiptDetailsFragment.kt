package com.hydrogen.hydrogenpaymentsdk.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hydrogen.hydrogenpayandroidpaymentsdk.R

class PayByCardTransactionReceiptDetailsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_pay_by_card_transaction_receipt_details,
            container,
            false
        )
    }
}