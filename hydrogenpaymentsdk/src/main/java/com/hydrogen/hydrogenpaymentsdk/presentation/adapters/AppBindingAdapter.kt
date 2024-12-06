package com.hydrogen.hydrogenpaymentsdk.presentation.adapters

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.hydrogen.hydrogenpaymentsdk.R

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