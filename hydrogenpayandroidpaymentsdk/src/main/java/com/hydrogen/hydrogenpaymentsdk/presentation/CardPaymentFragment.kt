package com.hydrogen.hydrogenpaymentsdk.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.FragmentCardPaymentBinding
import com.hydrogen.hydrogenpaymentsdk.di.AppViewModelProviderFactory
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesGson
import com.hydrogen.hydrogenpaymentsdk.domain.HydrogenPaySdkCallBack
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.customerNameInSentenceCase
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.setButtonEnabledState
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.setCustomerInitials
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.AppViewModel
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.SetUpViewModel
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.Status
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.INT_CARD_EXPIRY_DATE_LENGTH
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.INT_MASTER_VISA_CARD_LENGTH
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.INT_VERVE_CARD_LENGTH
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_CARD_EXPIRY_DATE_SPACER
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_CARD_NUMBER_SPACER
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.boldSomeParts
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.expiresIn
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.observeLiveData
import com.hydrogen.hydrogenpaymentsdk.utils.CardPaymentUtil
import com.hydrogen.hydrogenpaymentsdk.utils.CardPaymentUtil.checkSumCardValidation
import com.hydrogen.hydrogenpaymentsdk.utils.ExtensionFunctions.getTransactionInfoBalloon
import com.hydrogen.hydrogenpaymentsdk.utils.ExtensionFunctions.toggleProgressBarVisibility
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CardPaymentFragment : Fragment() {
    private lateinit var binding: FragmentCardPaymentBinding
    private lateinit var cardNumber: TextInputEditText
    private lateinit var cardNumberTextInputLayout: TextInputLayout
    private lateinit var cardExpiryTextInputLayout: TextInputLayout
    private lateinit var cardPin: TextInputEditText
    private lateinit var getCardProviderProgressBar: ProgressBar
    private lateinit var payByCardProgressBar: ProgressBar
    private lateinit var cardExp: TextInputEditText
    private lateinit var cvv: TextInputEditText
    private lateinit var userCardPinContainer: ConstraintLayout
    private lateinit var saveCardCheckBox: CheckBox
    private lateinit var savedCardsTextView: TextView
    private lateinit var payButton: Button
    private lateinit var backToMerchantAppButton: ImageView
    private lateinit var changePaymentMethodButton: ImageView
    private lateinit var timer: TextView
    private lateinit var customerInitials: TextView
    private lateinit var customerName: TextView
    private lateinit var merchantRefId: TextView
    private lateinit var transactionAmount: TextView
    private lateinit var infoIcon: ImageView
    private lateinit var hydrogenPaySdkCallBack: HydrogenPaySdkCallBack

    private val viewModel: AppViewModel by activityViewModels {
        AppViewModelProviderFactory(HydrogenPayDiModule)
    }
    private val setUpViewModel: SetUpViewModel by activityViewModels {
        AppViewModelProviderFactory(HydrogenPayDiModule)
    }
    private var hasGottenCardProvider = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hydrogenPaySdkCallBack = requireActivity() as HydrogenPaySdkCallBack
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    goBack(true)
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
            goBack(true)
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
                            payButton.text = getString(
                                R.string.pay_amount_place_holder,
                                transactionDetails.currencyInfo.currencySymbol,
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

        cardExp.doOnTextChanged { text, _, _, _ ->
            text?.let {
                val cardExpiration = it.toString().replace(STRING_CARD_EXPIRY_DATE_SPACER, "")
                if (cardExpiration.length == INT_CARD_EXPIRY_DATE_LENGTH) {
                    val isCardExpiryDateValid = CardPaymentUtil.checkCardExpiryDate(it.toString())
                    if (isCardExpiryDateValid) {
                        cvv.requestFocus()
                        cardExpiryTextInputLayout.error = null
                    } else {
                        cardExpiryTextInputLayout.error =
                            getString(R.string.invalid_card_expiry_date)
                    }
                } else {
                    cardExpiryTextInputLayout.error = null
                }
            }
        }

        cardNumber.doOnTextChanged { text, _, _, _ ->
            text?.let {
                val inputtedCardNumber = text.toString().replace(STRING_CARD_NUMBER_SPACER, "")
                if ((inputtedCardNumber.length == INT_MASTER_VISA_CARD_LENGTH || inputtedCardNumber.length == INT_VERVE_CARD_LENGTH) && !hasGottenCardProvider) {
                    val localCardValidation = checkSumCardValidation(inputtedCardNumber)
                    if (localCardValidation) {
                        hasGottenCardProvider = true
                        viewModel.getCardProvider(inputtedCardNumber)
                    } else {
                        viewModel.resetCardProvider()
                        cardNumberTextInputLayout.error = getString(R.string.invalid_card_number)
                    }
                } else {
                    cardNumberTextInputLayout.error = null
                    userCardPinContainer.visibility = View.GONE
                    hasGottenCardProvider = false
                }
            }
        }

        observeLiveData(
            viewModel.cardProvider,
            null,
            { getCardProviderProgressBar.toggleProgressBarVisibility(true) },
            {
                cardNumberTextInputLayout.error = it
                getCardProviderProgressBar.toggleProgressBarVisibility(false)
            }) { result ->
            getCardProviderProgressBar.toggleProgressBarVisibility(false)
            result?.getContentIfNotHandled()?.let {
                cardExpiryTextInputLayout.requestFocus() // Automatically set focus on cardExpiry after getting card provider
                if (it.isPinRequired) {
                    userCardPinContainer.visibility = View.VISIBLE
                } else {
                    userCardPinContainer.visibility = View.GONE
                }
            }
        }

        observeLiveData(viewModel.cardPaymentResponse, null, {
            payByCardProgressBar.toggleProgressBarVisibility(true)
            payButton.setButtonEnabledState(false)
        }, {
            payByCardProgressBar.toggleProgressBarVisibility(false)
            payButton.setButtonEnabledState(true)
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }) { result ->
            payByCardProgressBar.toggleProgressBarVisibility(false)
            result?.let {
                Toast.makeText(requireContext(), it.data.message ?: "", Toast.LENGTH_SHORT).show()
                val responseAsString = providesGson().toJson(it)
                val action =
                    CardPaymentFragmentDirections.actionCardPaymentFragmentToOTPCodeFragment(
                        responseAsString
                    )
                findNavController().navigate(action)
            }
        }

        backToMerchantAppButton.setOnClickListener {
            goBack()
        }

        payButton.setOnClickListener {
            val userCardPin =
                if (userCardPinContainer.visibility == View.VISIBLE) cardPin.text.toString() else ""
            val deviceInformation = CardPaymentUtil.getDeviceInformation(requireContext())
            viewModel.payByCard(
                cardNumber.text.toString(),
                cardExp.text.toString(),
                cvv.text.toString(),
                saveCardCheckBox.isChecked,
                userCardPin,
                deviceInformation
            )
        }
    }

    private fun goBack(isChangePaymentMethodNotGoBackToMerchantApp: Boolean = false) {
        if (viewModel.canGoBackFromCardPayment()) {
            if (isChangePaymentMethodNotGoBackToMerchantApp) {
                val navAction =
                    CardPaymentFragmentDirections.actionCardPaymentFragmentToChangePaymentMethodConfirmationFragment()
                findNavController().navigate(navAction)
            } else {
                hydrogenPaySdkCallBack.cancelByGoingBackToMerchantApp(getString(R.string.transaction_cancelled))
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.transaction_in_progress),
                Toast.LENGTH_SHORT
            ).show()
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
            backToMerchantAppButton = imageView12
            getCardProviderProgressBar = progressBar3
            cardNumberTextInputLayout = selectACategoryTil
            cardExpiryTextInputLayout = cardExpiryTil
            cardPin = cardPinTiet
            payByCardProgressBar = paymentProcessingProgressBar
        }
    }
}