package com.hydrogen.hydrogenpaymentsdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dsofttech.dprefs.utils.DPrefs
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.HydrogenPayPaymentActivityBinding
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.HydrogenPayPaymentRequest
import com.hydrogen.hydrogenpaymentsdk.di.AppViewModelProviderFactory
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesGson
import com.hydrogen.hydrogenpaymentsdk.domain.HydrogenPaySdkCallBack
import com.hydrogen.hydrogenpaymentsdk.domain.enums.RequestDeclineReasons
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.AppViewModel
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.SetUpViewModel
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.LONG_TIME_15_MIN
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_EXTRA_TAG
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_TIMER_DONE_VALUE
import com.hydrogen.hydrogenpaymentsdk.utils.HydrogenPay
import com.hydrogen.hydrogenpaymentsdk.utils.ModelMapper.toPayByTransferRequestObject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HydrogenPayPaymentActivity : AppCompatActivity(), HydrogenPaySdkCallBack {
    private lateinit var binding: HydrogenPayPaymentActivityBinding
    private val appViewModel: AppViewModel by viewModels {
        AppViewModelProviderFactory(HydrogenPayDiModule)
    }
    private val setUpViewModel: SetUpViewModel by viewModels {
        AppViewModelProviderFactory(HydrogenPayDiModule)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DPrefs.initializeDPrefs(this)
        binding = DataBindingUtil.setContentView(this, R.layout.hydrogen_pay_payment_activity)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        val payByTransferRequest = try {
            intent.getStringExtra(STRING_EXTRA_TAG)
                ?.let { providesGson().fromJson(it, HydrogenPayPaymentRequest::class.java) }
        } catch (e: Exception) {
            null
        }
        payByTransferRequest?.let {
            setUpViewModel.setUp(it.clientApiKey)
            appViewModel.setBankTransferRequest(it.toPayByTransferRequestObject())
            appViewModel.startTimer(LONG_TIME_15_MIN)
            appViewModel.initiatePayment()
        } ?: cancelByGoingBackToMerchantApp(
            RequestDeclineReasons.INVALID_ARGUMENT_PROVIDED.reason
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appViewModel.timeLeft.collectLatest {
                    if (it == STRING_TIMER_DONE_VALUE) {
                        Toast.makeText(
                            this@HydrogenPayPaymentActivity,
                            getString(R.string.time_out),
                            Toast.LENGTH_SHORT
                        ).show()
                        cancelByGoingBackToMerchantApp(RequestDeclineReasons.TIME_OUT.reason)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appViewModel.timeLeftToRedirectToMerchantAppAfterSuccessfulPayment.collect {
                    if (it == -1L) {
                        val intent = Intent()
                        intent.putExtra(
                            HydrogenPay.HYDROGEN_PAY_RESULT_KEY,
                            providesGson().toJson(appViewModel.getReceiptPayload())
                        )
                        this.apply {
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    companion object {
        internal fun start(
            resultLauncher: ActivityResultLauncher<Intent>,
            context: Context,
            paymentRequest: HydrogenPayPaymentRequest?
        ) {
            val extra =
                paymentRequest?.let { providesGson().toJson(paymentRequest) } ?: ""
            val intent = Intent(context, HydrogenPayPaymentActivity::class.java)
            intent.putExtra(STRING_EXTRA_TAG, extra)
            resultLauncher.launch(intent)
        }

        @Deprecated(
            message = "This method has been deprecated and support for it would be dropped in future release",
            replaceWith = ReplaceWith("ActivityResultLauncher")
        )
        internal fun start(
            requestCode: Int,
            context: Activity
        ) {
            val intent = Intent(context, HydrogenPayPaymentActivity::class.java)
            context.startActivityForResult(intent, requestCode)
        }
    }

    override fun cancelByGoingBackToMerchantApp(optionalMessage: String) {
        val intent = Intent()
        intent.putExtra(HydrogenPay.HYDROGEN_PAY_RESULT_KEY, optionalMessage)
        this.apply {
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }
}