package com.hydrogen.hydrogenpaymentsdk

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.textfield.TextInputEditText
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.HydrogenPayPaymentRequest
import com.hydrogen.hydrogenpaymentsdk.databinding.CallingAppMainActivityBinding
import com.hydrogen.hydrogenpaymentsdk.utils.HydrogenPay

class MainActivity : AppCompatActivity() {
    private lateinit var binding: CallingAppMainActivityBinding
    private lateinit var customerName: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var amount: TextInputEditText
    private lateinit var environmentDropDown: AutoCompleteTextView
    private lateinit var description: TextInputEditText
    private lateinit var button: Button
    private val paymentLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    Toast.makeText(this, "Callback triggered", Toast.LENGTH_SHORT).show()
                }
            }
        }
    private lateinit var environments: Array<String>
    private lateinit var dropDownAdapter: ArrayAdapter<String>
    private var selectedEnvironment: String = AuthToken.DEV.token

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.calling_app_main_activity)
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        initViews()
        environments = resources.getStringArray(R.array.environments)
        dropDownAdapter = ArrayAdapter(this, R.layout.env_drop_down_menu_item_layout, environments)
        environmentDropDown.setOnItemClickListener { p0, p1, p2, p3 ->
            selectedEnvironment = environments[p2]
        }
        environmentDropDown.setAdapter(dropDownAdapter)
        button.setOnClickListener {
            val payByTransferRequest = HydrogenPayPaymentRequest(
                amount = amount.text.toString().toInt(),
                customerName = customerName.text.toString(),
                email = email.text.toString(),
                callback = "https://hydrogenpay.com",
                meta = "test meta",
                description = description.text.toString(),
                currency = "NGN",
                clientApiKey = if (environmentDropDown.text.isNotEmpty() && environmentDropDown.text.toString() == "Dev") AuthToken.DEV.token else AuthToken.LIVE.token,
            )
            HydrogenPay.launch(paymentLauncher, this, payByTransferRequest)
        }
    }

    private fun initViews() {
        with(binding) {
            button = button4
            customerName = customerNameTv
            email = emailTv
            amount = amountTv
            environmentDropDown = environmentAutoCompleteTv
            description = descriptionTv
        }
    }
}