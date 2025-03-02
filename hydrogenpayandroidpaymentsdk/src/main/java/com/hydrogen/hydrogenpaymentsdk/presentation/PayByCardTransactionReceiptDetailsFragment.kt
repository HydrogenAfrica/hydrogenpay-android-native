package com.hydrogen.hydrogenpaymentsdk.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.FragmentPayByCardTransactionReceiptDetailsBinding
import com.hydrogen.hydrogenpaymentsdk.di.AppViewModelProviderFactory
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.AppViewModel
import kotlinx.coroutines.launch

class PayByCardTransactionReceiptDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPayByCardTransactionReceiptDetailsBinding
    private val args: PayByCardTransactionReceiptDetailsFragmentArgs by navArgs()
    private val applicationViewModel: AppViewModel by activityViewModels {
        AppViewModelProviderFactory(HydrogenPayDiModule)
    }
    private lateinit var serviceChargeAmount: TextView
    private lateinit var vatAmount: TextView
    private lateinit var totalAmount: TextView
    private lateinit var merchantProvidedDescription: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_pay_by_card_transaction_receipt_details,
            container,
            false
        )
        binding.apply {
            appViewModel = applicationViewModel
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        initViews()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                applicationViewModel.paymentMethodsAndTransactionDetails.collect {

                }
            }
        }
    }

    private fun initViews() {
        with(binding) {
            serviceChargeAmount = serviceCharge
            vatAmount = vat
            totalAmount = textView25
            merchantProvidedDescription = textView27
        }
    }
}