package com.hydrogen.hydrogenpaymentsdk.presentation

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.FragmentSelectPaymentMethodBinding
import com.hydrogen.hydrogenpaymentsdk.di.AppViewModelProviderFactory
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule
import com.hydrogen.hydrogenpaymentsdk.domain.enums.RequestDeclineReasons
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.PaymentMethodsAdapter
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.customerNameInSentenceCase
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.setCustomerInitials
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.AppViewModel
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.Status
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_CARD_PAYMENT
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_TRANSFER
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.boldSomeParts
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.expiresIn
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.getLoadingAlertDialog
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.observeLiveData
import com.hydrogen.hydrogenpaymentsdk.utils.HydrogenPay.HYDROGEN_PAY_RESULT_KEY
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SelectPaymentMethodFragment : Fragment() {
    private lateinit var paymentMethodsAdapter: PaymentMethodsAdapter
    private lateinit var binding: FragmentSelectPaymentMethodBinding
    private var loaderAlertDialog: Dialog? = null
    private lateinit var paymentMethodRv: RecyclerView
    private lateinit var backToMerchantAppBtn: ImageView
    private lateinit var timer: TextView
    private lateinit var merchantRefId: TextView
    private lateinit var shimmerMerchantRefId: ShimmerFrameLayout
    private lateinit var transactionAmount: TextView
    private lateinit var shimmerTransactionAmount: ShimmerFrameLayout
    private lateinit var merchantName: TextView
    private lateinit var shimmerMerchantName: ShimmerFrameLayout
    private lateinit var customerNameInitials: TextView
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
                    cancelByGoingBackToMerchantApp()
                }
            })
        paymentMethodsAdapter = PaymentMethodsAdapter {
            when (it.name) {
                STRING_TRANSFER -> {
                    if (viewModel.bankTransferResponseState.value!!.status == Status.SUCCESS) {
                        // Navigate to Bank Transfer Page
                        val action =
                            SelectPaymentMethodFragmentDirections.actionSelectPaymentMethodFragmentToBankTransferFragment()
                        findNavController().navigate(action)
                    } else {
                        // Initiate Bank Transfer and navigate to Bank Transfer page on success
                        viewModel.payByTransfer()
                        observeLiveData(
                            viewModel.bankTransferResponseState,
                            loaderAlertDialog,
                            null,
                            { errorMessage ->
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        ) {
                            // Navigate to Bank Transfer Page
                            val action =
                                SelectPaymentMethodFragmentDirections.actionSelectPaymentMethodFragmentToBankTransferFragment()
                            findNavController().navigate(action)
                        }
                    }
                }

                STRING_CARD_PAYMENT -> {
                    val action =
                        SelectPaymentMethodFragmentDirections.actionSelectPaymentMethodFragmentToCardPaymentFragment()
                    findNavController().navigate(action)
                }
            }
        }
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_select_payment_method,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        loaderAlertDialog = getLoadingAlertDialog(requireContext())

        // Cancel by clicking backToMerchantButton
        backToMerchantAppBtn.setOnClickListener {
            cancelByGoingBackToMerchantApp()
        }

        paymentMethodRv.apply {
            adapter = paymentMethodsAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.paymentMethodsAndTransactionDetails.collectLatest { data ->
                    data.content?.let {
                        it.first?.let { paymentMethods ->
                            it.second?.let { transactionDetails ->
                                paymentMethodsAdapter.updateData(paymentMethods)
                                val formattedMerchantRefId = getString(
                                    R.string.merchant_ref_place_holder,
                                    transactionDetails.merchantRef
                                )
                                transactionAmount.text = getString(
                                    R.string.amount_in_naira_place_holder,
                                    transactionDetails.totalAmount
                                )
                                merchantRefId.boldSomeParts(
                                    formattedMerchantRefId,
                                    ((formattedMerchantRefId.length - transactionDetails.merchantRef.length) + 1),
                                    formattedMerchantRefId.length
                                )
                                customerNameInitials.setCustomerInitials(transactionDetails.merchantInfo.merchantName)
                                merchantName.customerNameInSentenceCase(transactionDetails.merchantInfo.merchantName)
                                shouldShowAmountAndMerchantRef(true)
                            } ?: run { shouldShowAmountAndMerchantRef() }
                        } ?: run {
                            // Continue shimmer effects
                            paymentMethodsAdapter.updateData(emptyList())
                            shouldShowAmountAndMerchantRef()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.timeLeft.collectLatest {
                    timer.expiresIn(it)
                }
            }
        }
    }

    private fun shouldShowAmountAndMerchantRef(condition: Boolean = false) {
        if (condition) {
            shimmerMerchantRefId.visibility = View.INVISIBLE
            merchantRefId.visibility = View.VISIBLE
            shimmerTransactionAmount.visibility = View.INVISIBLE
            transactionAmount.visibility = View.VISIBLE
            shimmerMerchantName.visibility = View.INVISIBLE
            merchantName.visibility = View.VISIBLE
        } else {
            shimmerMerchantRefId.visibility = View.VISIBLE
            merchantRefId.visibility = View.INVISIBLE
            shimmerTransactionAmount.visibility = View.VISIBLE
            transactionAmount.visibility = View.INVISIBLE
            shimmerMerchantName.visibility = View.VISIBLE
            merchantName.visibility = View.INVISIBLE
        }
    }

    private fun initViews() {
        with(binding) {
            backToMerchantAppBtn = imageView4
            timer = textView2
            paymentMethodRv = recyclerView
            transactionAmount = textView5
            shimmerTransactionAmount = shimmerAmount
            merchantRefId = textView6
            shimmerMerchantRefId = shimmerMerchantRef
            customerNameInitials = textView3
            shimmerMerchantName = shimmerCustomerNameTextView
            merchantName = textView4
        }
    }

    private fun cancelByGoingBackToMerchantApp(optionalMessage: String = RequestDeclineReasons.CANCELLED.reason) {
        val intent = Intent()
        intent.putExtra(HYDROGEN_PAY_RESULT_KEY, optionalMessage)
        requireActivity().apply {
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }
}