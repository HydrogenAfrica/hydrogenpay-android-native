package com.hydrogen.hydrogenpaymentsdk

import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("customerName", "email", "amount", "description", "env")
fun Button.enablePayButton(
    customerName: TextInputEditText,
    email: TextInputEditText,
    amount: TextInputEditText,
    description: TextInputEditText,
    env: AutoCompleteTextView
) {
    this.isEnabled = customerName.text.toString().isNotBlank()
            && email.text.toString().isNotBlank()
            && amount.text.toString().isNotBlank()
            && description.text.toString().isNotBlank()
}