package com.hydrogen.hydrogenpaymentsdk.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.FragmentCardPaymentBinding
import com.hydrogen.hydrogenpaymentsdk.di.AppViewModelProviderFactory
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.customerNameInSentenceCase
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.setCustomerInitials
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.AppViewModel
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.SetUpViewModel
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.INT_MASTER_VISA_CARD_LENGTH
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.INT_VERVE_CARD_LENGTH
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.boldSomeParts
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.expiresIn
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.observeLiveData
import com.hydrogen.hydrogenpaymentsdk.utils.ExtensionFunctions.getTransactionInfoBalloon
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CardPaymentFragment : Fragment() {
    private lateinit var binding: FragmentCardPaymentBinding
    private lateinit var cardNumber: TextInputEditText
    private lateinit var getCardProviderProgressBar: ProgressBar
    private lateinit var cardExp: TextInputEditText
    private lateinit var cvv: TextInputEditText
    private lateinit var userCardPinContainer: ConstraintLayout
    private lateinit var saveCardCheckBox: CheckBox
    private lateinit var savedCardsTextView: TextView
    private lateinit var payButton: Button
    private lateinit var returnToMerchantAppButton: ImageView
    private lateinit var changePaymentMethodButton: ImageView
    private lateinit var timer: TextView
    private lateinit var customerInitials: TextView
    private lateinit var customerName: TextView
    private lateinit var merchantRefId: TextView
    private lateinit var transactionAmount: TextView
    private lateinit var infoIcon: ImageView

    private val viewModel: AppViewModel by activityViewModels {
        AppViewModelProviderFactory(HydrogenPayDiModule)
    }
    private val setUpViewModel: SetUpViewModel by activityViewModels {
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
                    val action =
                        CardPaymentFragmentDirections.actionCardPaymentFragmentToChangePaymentMethodConfirmationFragment()
                    findNavController().navigate(action)
                }
            }
        )
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_card_payment, container, false)
        binding.apply {
            appViewModel = viewModel
            this.setUpViewModel = this@CardPaymentFragment.setUpViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        changePaymentMethodButton.setOnClickListener {
            val action =
                CardPaymentFragmentDirections.actionCardPaymentFragmentToChangePaymentMethodConfirmationFragment()
            findNavController().navigate(action)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.timeLeft.collectLatest {
                    timer.expiresIn(it)
                }
            }
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

        infoIcon.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.paymentMethodsAndTransactionDetails.collect { it1 ->
                        it1.content?.let { data ->
                            it.getTransactionInfoBalloon(data.second!!)
                        }
                    }
                }
            }
        }

        cardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val text = p0.toString().replace(" ", "")
                if (text.length == INT_MASTER_VISA_CARD_LENGTH || text.length > INT_VERVE_CARD_LENGTH) {
                    viewModel.getCardProvider(text)
                }
            }
        })
        observeLiveData(viewModel.cardProvider, null, {toggleProgressBarVisibility(true)}, {toggleProgressBarVisibility(false)}) { result ->
            result?.getContentIfNotHandled()?.let {
                if (it.isPinRequired) {
                    userCardPinContainer.visibility = View.VISIBLE
                } else {
                    userCardPinContainer.visibility = View.GONE
                }
            }
        }
    }

    private fun toggleProgressBarVisibility(isVisible: Boolean) {
        if (isVisible) {
            getCardProviderProgressBar.visibility = View.VISIBLE
        } else {
            getCardProviderProgressBar.visibility = View.GONE
        }
    }

    private fun initViews() {
        with(binding) {
            changePaymentMethodButton = changePaymentMethodBtn
            timer = textView2
            customerInitials = textView3
            customerName = textView4
            merchantRefId = textView6
            transactionAmount = textView5
            infoIcon = imageView14
            cardNumber = cardNumberTiet
            cardExp = cardExpiryTiet
            cvv = cvvTiet
            userCardPinContainer = cardPinContainer
            saveCardCheckBox = checkBox
            savedCardsTextView = textView41
            payButton = button
            returnToMerchantAppButton = imageView12
            getCardProviderProgressBar = progressBar3
        }
    }
}