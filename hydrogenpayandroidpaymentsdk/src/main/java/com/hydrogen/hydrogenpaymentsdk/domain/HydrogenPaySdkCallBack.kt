package com.hydrogen.hydrogenpaymentsdk.domain

import com.hydrogen.hydrogenpaymentsdk.domain.enums.RequestDeclineReasons

interface HydrogenPaySdkCallBack {
    fun cancelByGoingBackToMerchantApp(optionalMessage: String = RequestDeclineReasons.CANCELLED.reason)
}