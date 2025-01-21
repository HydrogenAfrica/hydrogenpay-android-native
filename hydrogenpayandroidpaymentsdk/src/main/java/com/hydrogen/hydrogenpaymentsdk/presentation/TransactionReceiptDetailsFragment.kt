package com.hydrogen.hydrogenpaymentsdk.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.FragmentTransactionReceiptDetailsBinding
import com.hydrogen.hydrogenpaymentsdk.di.AppViewModelProviderFactory
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.customerNameInSentenceCase
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.AppViewModel
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.createAlertModal
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TransactionReceiptDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTransactionReceiptDetailsBinding
    private lateinit var merchantName: TextView
    private val viewModel: AppViewModel by activityViewModels {
        AppViewModelProviderFactory(HydrogenPayDiModule)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.timeLeftToRedirectToMerchantAppAfterSuccessfulPayment.collectLatest {
                                val time = if (it > 9) it.toString() else "0$it"
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.you_will_be_redirected, time),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        )

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_transaction_receipt_details,
            container,
            false
        )
        binding.apply {
            appViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.paymentMethodsAndTransactionDetails.collect { data ->
                    data.content?.second?.let {
                        merchantName.customerNameInSentenceCase(it.merchantInfo.merchantName)
                    }
                }
            }
        }

        viewModel.startRedirectTimer()
        val infoModal =
            createAlertModal(null, viewModel.timeLeftToRedirectToMerchantAppAfterSuccessfulPayment)
        infoModal?.show()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.timeLeftToRedirectToMerchantAppAfterSuccessfulPayment.collect {
                    if (it == 0L) {
                        infoModal?.dismiss()
                        infoModal?.cancel()
                    }
                }
            }
        }
    }

    private fun initViews() {
        with(binding) {
            merchantName = textView22
        }
    }
}