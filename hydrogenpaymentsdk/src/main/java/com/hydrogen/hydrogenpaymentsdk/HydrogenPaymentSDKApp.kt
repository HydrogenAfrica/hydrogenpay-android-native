package com.hydrogen.hydrogenpaymentsdk

import android.app.Application
import com.dsofttech.dprefs.utils.DPrefs

class HydrogenPaymentSDKApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DPrefs.initializeDPrefs(this)
    }
}