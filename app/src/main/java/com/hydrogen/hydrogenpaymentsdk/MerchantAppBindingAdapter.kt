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
    this.isEnabled = customerName.text.toString().isNotEmpty()
            && email.text.toString().isNotEmpty()
            && amount.text.toString().isNotEmpty()
            && description.text.toString().isNotEmpty()
}