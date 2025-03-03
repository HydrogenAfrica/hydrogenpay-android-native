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
import androidx.navigation.fragment.navArgs
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.FragmentPayByCardTransactionReceiptDetailsBinding
import com.hydrogen.hydrogenpaymentsdk.di.AppViewModelProviderFactory
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule
import com.hydrogen.hydrogenpaymentsdk.domain.enums.PaymentType
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.AppViewModel
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.Status
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.createAlertModal
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PayByCardTransactionReceiptDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPayByCardTransactionReceiptDetailsBinding
    private val args: PayByCardTransactionReceiptDetailsFragmentArgs by navArgs()
    private val applicationViewModel: AppViewModel by activityViewModels {
        AppViewModelProviderFactory(HydrogenPayDiModule)
    }
    private lateinit var serviceChargeAmount: TextView
    private lateinit var vatLabelTv: TextView
    private lateinit var vatAmount: TextView
    private lateinit var totalAmount: TextView
    private lateinit var merchantProvidedDescription: TextView
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
                            applicationViewModel.timeLeftToRedirectToMerchantAppAfterSuccessfulPayment.collectLatest {
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
            })
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                applicationViewModel.paymentMethodsAndTransactionDetails.collect {
                    if (it.status == Status.SUCCESS && it.content != null) {
                        it.content.let { data ->
                            serviceChargeAmount.text = getString(
                                R.string.amount_in_naira_place_holder,
                                data.second?.serviceFees ?: ""
                            )
                            vatLabelTv.text = getString(
                                R.string.vat_place_holder,
                                data.second?.vatPercentage ?: ""
                            )
                            vatAmount.text = getString(
                                R.string.amount_in_naira_place_holder,
                                data.second?.vatFee ?: ""
                            )
                            totalAmount.text = getString(
                                R.string.amount_in_naira_place_holder,
                                data.second?.totalAmount ?: ""
                            )
                        }
                    }
                }
            }
        }

        applicationViewModel.startRedirectTimer(PaymentType.PAY_BY_CARD)
        val infoModal =
            createAlertModal(null, applicationViewModel.timeLeftToRedirectToMerchantAppAfterSuccessfulPayment)
        infoModal?.show()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                applicationViewModel.timeLeftToRedirectToMerchantAppAfterSuccessfulPayment.collect {
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
            serviceChargeAmount = serviceCharge
            vatAmount = vat
            totalAmount = textView25
            merchantProvidedDescription = textView27
            vatLabelTv = vatLabel
        }
    }
}