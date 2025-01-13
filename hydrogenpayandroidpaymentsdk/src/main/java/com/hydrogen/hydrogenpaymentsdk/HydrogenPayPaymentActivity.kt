package com.hydrogen.hydrogenpaymentsdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dsofttech.dprefs.utils.DPrefs
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.HydrogenPayPaymentActivityBinding
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.di.AppViewModelProviderFactory
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesGson
import com.hydrogen.hydrogenpaymentsdk.domain.enums.RequestDeclineReasons
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.AppViewModel
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.INT_TIME_15_MIN
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_EXTRA_TAG
import com.hydrogen.hydrogenpaymentsdk.utils.HydrogenPay

class HydrogenPayPaymentActivity : AppCompatActivity() {
    private lateinit var binding: HydrogenPayPaymentActivityBinding
    private val viewModel: AppViewModel by viewModels {
        AppViewModelProviderFactory()
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
                ?.let { providesGson().fromJson(it, PayByTransferRequest::class.java) }
        } catch (e: Exception) {
            null
        }
        payByTransferRequest?.let {
            viewModel.setBankTransferRequest(it)
            viewModel.startTimer(INT_TIME_15_MIN)
        } ?: cancelByGoingBackToMerchantApp(
            RequestDeclineReasons.INVALID_ARGUMENT_PROVIDED.reason
        )
    }

    companion object {
        internal fun start(
            resultLauncher: ActivityResultLauncher<Intent>,
            context: Context,
            payByTransferRequest: PayByTransferRequest?
        ) {
            val extra =
                payByTransferRequest?.let { providesGson().toJson(payByTransferRequest) } ?: ""
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

    private fun cancelByGoingBackToMerchantApp(optionalMessage: String = RequestDeclineReasons.CANCELLED.reason) {
        val intent = Intent()
        intent.putExtra(HydrogenPay.HYDROGEN_PAY_RESULT_KEY, optionalMessage)
        this.apply {
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }
}