package com.hydrogen.hydrogenpaymentsdk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.HydrogenPayPaymentRequest
import com.hydrogen.hydrogenpaymentsdk.databinding.CallingAppMainActivityBinding
import com.hydrogen.hydrogenpaymentsdk.utils.HydrogenPay

class MainActivity : AppCompatActivity() {
    private lateinit var binding: CallingAppMainActivityBinding
    private lateinit var button: Button
    private val paymentLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    Toast.makeText(this, "Callback triggered", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.calling_app_main_activity)
        initViews()
        val payByTransferRequest = HydrogenPayPaymentRequest(
            10,
            "Dev Test",
            "devtest@randomuser.com",
            "https://hydrogenpay.com",
            meta = "test meta",
            description = "test desc",
            currency = "NGN",
            clientApiKey = AuthToken.LIVE.token
        )
        button.setOnClickListener {
            HydrogenPay.launch(paymentLauncher, this, payByTransferRequest)
        }
    }

    private fun initViews() {
        with(binding) {
            button = button4
        }
    }
}