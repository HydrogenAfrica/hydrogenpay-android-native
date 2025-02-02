package com.hydrogen.hydrogenpaymentsdk.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.hydrogen.hydrogenpaymentsdk.HydrogenPayPaymentActivity
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.HydrogenPayPaymentRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequest

object HydrogenPay {
    const val HYDROGEN_PAY_RESULT_KEY = "hydrogen_pay_result_key"

    fun launch(resultLauncher: ActivityResultLauncher<Intent>, context: Context, payByTransferRequest: HydrogenPayPaymentRequest?) {
        HydrogenPayPaymentActivity.start(resultLauncher, context, payByTransferRequest)
    }

    @Deprecated(
        message = "This method has been deprecated and support for it would be dropped in future release",
        replaceWith = ReplaceWith("ActivityResultLauncher")
    )
    fun launch(requestCode: Int, context: Activity) {
        HydrogenPayPaymentActivity.start(requestCode, context)
    }
}