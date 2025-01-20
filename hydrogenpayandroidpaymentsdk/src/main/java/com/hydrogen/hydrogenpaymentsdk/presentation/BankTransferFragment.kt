package com.hydrogen.hydrogenpaymentsdk.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.FragmentBankTransferBinding
import com.hydrogen.hydrogenpaymentsdk.di.AppViewModelProviderFactory
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.customerNameInSentenceCase
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.setCustomerInitials
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.AppViewModel
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.boldSomeParts
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.copyToClipboard
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.expiresIn
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.observeLiveData
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BankTransferFragment : Fragment() {
    private lateinit var binding: FragmentBankTransferBinding
    private lateinit var changePaymentMethodButton: ImageView
    private lateinit var timer: TextView
    private lateinit var accountNumberTxtView: TextView
    private lateinit var infoTextView: TextView
    private lateinit var checkingPaymentProgress: ConstraintLayout
    private lateinit var makePaymentButton: Button
    private lateinit var customerInitials: TextView
    private lateinit var customerName: TextView
    private lateinit var merchantRefId: TextView
    private lateinit var transactionAmount: TextView

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
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.transaction_in_progress),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_bank_transfer, container, false)
        binding.apply {
            appViewModel = viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        changePaymentMethodButton.setOnClickListener {
            findNavController().popBackStack()
        }

        makePaymentButton.setOnClickListener {
            viewModel.confirmPayment()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.timeLeft.collectLatest {
                    timer.expiresIn(it)
                }
            }
        }

        // Start checking for transaction status
        observeLiveData(viewModel.paymentConfirmation, null, {
            makePaymentButton.isEnabled = false
            makePaymentButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.lighter_dark))
            infoTextView.visibility = View.GONE
            checkingPaymentProgress.visibility = View.VISIBLE
        },
            {
                Toast.makeText(requireContext(),  "$it ${getString(R.string.try_again)}", Toast.LENGTH_LONG).show()
                makePaymentButton.isEnabled = true
                makePaymentButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.hydrogen_yellow))
                infoTextView.visibility = View.VISIBLE
                checkingPaymentProgress.visibility = View.GONE
            }) {
            Toast.makeText(
                requireContext(),
                getString(R.string.payment_successful),
                Toast.LENGTH_LONG
            ).show()
            viewModel.pauseTimer()
            val action =
                BankTransferFragmentDirections.actionBankTransferFragmentToTransactionReceiptDetailsFragment()
            findNavController().navigate(action)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.paymentMethodsAndTransactionDetails.collect { data ->
                    data.content?.let {
                        it.second?.let { transactionDetails ->
                            val formattedMerchantRefId = getString(
                                R.string.merchant_ref_place_holder,
                                transactionDetails.merchantRef
                            )
                            customerName.customerNameInSentenceCase(transactionDetails.merchantInfo.merchantName)
                            customerInitials.setCustomerInitials(transactionDetails.merchantInfo.merchantName)
                            transactionAmount.text = getString(
                                R.string.amount_in_naira_place_holder,
                                transactionDetails.totalAmount
                            )
                            merchantRefId.boldSomeParts(
                                formattedMerchantRefId,
                                ((formattedMerchantRefId.length - transactionDetails.merchantRef.length)),
                                formattedMerchantRefId.length
                            )
                        }
                    }
                }
            }
        }

        // Copy Account number to clipboard
        accountNumberTxtView.setOnClickListener {
            copyToClipboard(
                requireContext(),
                getString(R.string.account_number_copied),
                accountNumberTxtView.text.toString()
            )
        }
    }

    private fun initViews() {
        with(binding) {
            changePaymentMethodButton = changePaymentMethodBtn
            accountNumberTxtView = textView13
            infoTextView = textView15
            checkingPaymentProgress = checkingPaymentL
            makePaymentButton = button
            timer = textView2
            customerInitials = textView3
            customerName = textView4
            merchantRefId = textView6
            transactionAmount = textView5
        }
    }
}