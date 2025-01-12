package com.hydrogen.hydrogenpaymentsdk.presentation.adapters

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.formatNumberWithCommas
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.getCustomerNameInitials
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.toSentenceCase

@BindingAdapter("android:transferTo")
fun TextView.transferTo(inputString: String?) {
    inputString?.let {
        val spannableString = SpannableString(it)
        spannableString.apply {
            setSpan(
                ForegroundColorSpan(Color.BLACK),
                13,
                it.length,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            setSpan(
                StyleSpan(Typeface.BOLD),
                13,
                it.length,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            setSpan(
                ForegroundColorSpan(
                    ResourcesCompat.getColor(
                        this@transferTo.resources,
                        R.color.light_black,
                        this@transferTo.context.theme
                    )
                ),
                1,
                13,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            setSpan(
                CustomFontSpan(ResourcesCompat.getFont(this@transferTo.context, R.font.exo_medium)),
                1,
                13,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        }
    }
}

@BindingAdapter("android:setCustomerInitials")
fun TextView.setCustomerInitials(customerName: String?) {
    customerName?.let {
        text = it.getCustomerNameInitials()
    }
}

@BindingAdapter("android:customerNameInSentenceCase")
fun TextView.customerNameInSentenceCase(customerName: String?) {
    customerName?.let {
        text = it.toSentenceCase()
    }
}

@BindingAdapter("android:formatAmountWithCommas")
fun TextView.formatAmountWithCommas(num: Int?) {
    text = context.getString(
        R.string.amount_in_naira_place_holder,
        formatNumberWithCommas(num?.toDouble())
    )
}

@BindingAdapter("android:toggleTextColorWithEnabledState")
fun Button.toggleTextColorWithEnabledState(string: String) {
    if (this.isEnabled == true) {
        setTextColor(ContextCompat.getColor(context, R.color.hydrogen_yellow))
    } else {
        setTextColor(ContextCompat.getColor(context, R.color.hydrogen_yellow))
    }
}