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
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.AppViewModel
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
                    timer.text = it
                }
            }
        }

        // Start checking for transaction status
        observeLiveData(viewModel.paymentConfirmation, {
            infoTextView.visibility = View.GONE
            checkingPaymentProgress.visibility = View.VISIBLE
        },
            { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }) {
            Toast.makeText(
                requireContext(),
                getString(R.string.payment_successful),
                Toast.LENGTH_SHORT
            ).show()
            val action =
                BankTransferFragmentDirections.actionBankTransferFragmentToTransactionReceiptDetailsFragment()
            findNavController().navigate(action)
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
        }
    }
}