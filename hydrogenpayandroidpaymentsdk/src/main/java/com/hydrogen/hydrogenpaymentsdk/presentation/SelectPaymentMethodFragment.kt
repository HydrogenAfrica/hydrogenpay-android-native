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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.FragmentSelectPaymentMethodBinding
import com.hydrogen.hydrogenpaymentsdk.di.AppViewModelProviderFactory
import com.hydrogen.hydrogenpaymentsdk.domain.models.RequestDeclineReasons
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.AppViewModel
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.getLoadingAlertDialog
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.observeLiveData
import com.hydrogen.hydrogenpaymentsdk.utils.HydrogenPay.HYDROGEN_PAY_RESULT_KEY
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SelectPaymentMethodFragment : Fragment() {
    private lateinit var binding: FragmentSelectPaymentMethodBinding
    private var loaderAlertDialog: Dialog? = null
    private lateinit var bankTransfer: ConstraintLayout
    private lateinit var payByCard: ConstraintLayout
    private lateinit var backToMerchantAppBtn: ImageView
    private lateinit var timer: TextView
    private val viewModel: AppViewModel by activityViewModels {
        AppViewModelProviderFactory()
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
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_select_payment_method,
            container,
            false
        )
        binding.apply {
            this.appViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }
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

        // Select pay by bank transfer
        bankTransfer.setOnClickListener {
            viewModel.payByTransfer()
            observeLiveData(viewModel.bankTransferResponseState, {
                loaderAlertDialog?.show()
            }, {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }) {
                loaderAlertDialog?.cancel()
                loaderAlertDialog?.dismiss()
                val action =
                    SelectPaymentMethodFragmentDirections.actionSelectPaymentMethodFragmentToBankTransferFragment()
                findNavController().navigate(action)
            }
        }

        // Select pay by card
        payByCard.setOnClickListener {
            val action =
                SelectPaymentMethodFragmentDirections.actionSelectPaymentMethodFragmentToCardPaymentFragment()
            findNavController().navigate(action)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.timeLeft.collectLatest {
                    timer.text = it
                }
            }
        }
    }

    private fun initViews() {
        with(binding) {
            bankTransfer = bankTransferContentL
            payByCard = payByCardConstraintL
            backToMerchantAppBtn = imageView4
            timer = textView2
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