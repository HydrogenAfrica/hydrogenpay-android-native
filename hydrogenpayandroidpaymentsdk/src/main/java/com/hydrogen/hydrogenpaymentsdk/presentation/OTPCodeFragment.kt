package com.hydrogen.hydrogenpaymentsdk.presentation

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
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
import androidx.navigation.fragment.navArgs
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.FragmentOTPCodeBinding
import com.hydrogen.hydrogenpaymentsdk.di.AppViewModelProviderFactory
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesGson
import com.hydrogen.hydrogenpaymentsdk.domain.HydrogenPaySdkCallBack
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByCardResponseDomain
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.customerNameInSentenceCase
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.setButtonEnabledState
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.setCustomerInitials
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.AppViewModel
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.SetUpViewModel
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.Status
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.INT_OTP_LENGTH
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.boldSomeParts
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.expiresIn
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.observeLiveData
import com.hydrogen.hydrogenpaymentsdk.utils.ExtensionFunctions.getTransactionInfoBalloon
import com.hydrogen.hydrogenpaymentsdk.utils.ExtensionFunctions.toggleProgressBarVisibility
import com.mukeshsolanki.OtpView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OTPCodeFragment : Fragment() {
    private lateinit var binding: FragmentOTPCodeBinding
    private lateinit var validateOtpProgressBar: ProgressBar
    private var loaderAlertDialog: Dialog? = null
    private lateinit var payButton: Button
    private lateinit var backToMerchantAppButton: ImageView
    private lateinit var changePaymentMethodButton: ImageView
    private lateinit var timer: TextView
    private lateinit var customerInitials: TextView
    private lateinit var customerName: TextView
    private lateinit var merchantRefId: TextView
    private lateinit var transactionAmount: TextView
    private lateinit var infoIcon: ImageView
    private lateinit var verifyOtpInfoText: TextView
    private lateinit var attemptsLeft: TextView
    private lateinit var resendOtpText: TextView
    private lateinit var otpCodeView: OtpView
    private lateinit var hydrogenPaySdkCallBack: HydrogenPaySdkCallBack

    private val applicationViewModel: AppViewModel by activityViewModels {
        AppViewModelProviderFactory(HydrogenPayDiModule)
    }

    private val setUpViewModel: SetUpViewModel by activityViewModels {
        AppViewModelProviderFactory(HydrogenPayDiModule)
    }
    private val args: OTPCodeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hydrogenPaySdkCallBack = requireActivity() as HydrogenPaySdkCallBack
        activity?.onBackPressedDispatcher?.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val action =
                        OTPCodeFragmentDirections.actionOTPCodeFragmentToChangePaymentMethodConfirmationFragment()
                    findNavController().navigate(action)
                }

            }
        )
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_o_t_p_code, container, false)
        binding.apply {
            appViewModel = applicationViewModel
            this.setUpViewModel = this@OTPCodeFragment.setUpViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        loaderAlertDialog = AppUtils.getLoadingAlertDialog(requireContext())
        val payByCardResponse = providesGson().fromJson(args.payByCardResponseAsString,  PayByCardResponseDomain::class.java)
        verifyOtpInfoText.text = payByCardResponse.payByCardResponseMessage
        changePaymentMethodButton.setOnClickListener {
            applicationViewModel.validateOtpCode.value?.let { otpValidation ->
                applicationViewModel.transactionStatus.value?.let { transStatus ->
                    if (otpValidation.status != Status.LOADING && transStatus.status != Status.LOADING) {
                        val action =
                            OTPCodeFragmentDirections.actionOTPCodeFragmentToChangePaymentMethodConfirmationFragment()
                        findNavController().navigate(action)
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.transaction_in_progress), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                applicationViewModel.timeLeft.collectLatest {
                    timer.expiresIn(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                applicationViewModel.paymentMethodsAndTransactionDetails.collect { data ->
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
                    applicationViewModel.paymentMethodsAndTransactionDetails.collect { it1 ->
                        it1.content?.let { data ->
                            it.getTransactionInfoBalloon(data.second!!)
                        }
                    }
                }
            }
        }

        backToMerchantAppButton.setOnClickListener {
            applicationViewModel.validateOtpCode.value?.let { otpValidation ->
                applicationViewModel.transactionStatus.value?.let { transStatus ->
                    if (otpValidation.status != Status.LOADING && transStatus.status != Status.LOADING) {
                        hydrogenPaySdkCallBack.cancelByGoingBackToMerchantApp()
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.transaction_in_progress), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        resendOtpText.setOnClickListener {
            applicationViewModel.resendOtp()
        }

        otpCodeView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val otp = p0.toString()
                if (otp.length < INT_OTP_LENGTH) {
                    otpCodeView.error = null
                }
                payButton.setButtonEnabledState(otp.length == INT_OTP_LENGTH)
            }
        })

        payButton.setOnClickListener {
            applicationViewModel.validateOtp(otpCodeView.text.toString())
        }

        observeLiveData(applicationViewModel.validateOtpCode, null, {
            payButton.setButtonEnabledState(false)
            validateOtpProgressBar.toggleProgressBarVisibility(true)
        }, {
            payButton.setButtonEnabledState(true)
            validateOtpProgressBar.toggleProgressBarVisibility(false)
            otpCodeView.error = it
        }) {
            applicationViewModel.getPayByCardTransactionStatus()
            validateOtpProgressBar.toggleProgressBarVisibility(false)
            payButton.setButtonEnabledState(true)
            Toast.makeText(requireContext(), getString(R.string.otp_successful_getting_trans_status), Toast.LENGTH_SHORT).show()
        }

        observeLiveData(applicationViewModel.transactionStatus, loaderAlertDialog!!, null, null) { transStatus ->
            transStatus?.let {
                val transactionStatusString = providesGson().toJson(it)
                val action = OTPCodeFragmentDirections.actionOTPCodeFragmentToPayByCardTransactionReceiptDetailsFragment(transactionStatusString)
                findNavController().navigate(action)
            } ?: run {
                Toast.makeText(requireContext(), getString(R.string.transaction_status_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initViews() {
        with(binding) {
            validateOtpProgressBar = paymentProcessingProgressBar
            payButton = button
            backToMerchantAppButton = imageView12
            changePaymentMethodButton = changePaymentMethodBtn
            timer = textView2
            customerInitials = textView3
            customerName = textView4
            merchantRefId = textView6
            transactionAmount = textView5
            infoIcon = imageView14
            verifyOtpInfoText = enterOtpHere
            attemptsLeft = textView45
            resendOtpText = resendOtp
            otpCodeView = otpView
        }
    }
}