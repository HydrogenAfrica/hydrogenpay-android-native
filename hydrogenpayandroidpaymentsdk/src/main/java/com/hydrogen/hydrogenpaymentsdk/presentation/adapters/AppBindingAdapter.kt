package com.hydrogen.hydrogenpaymentsdk.presentation.adapters

import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.android.material.textfield.TextInputEditText
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.formatNumberWithCommas
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.getCustomerNameInitials
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.toSentenceCase

@BindingAdapter("android:setIsTestTransaction")
fun TextView.setIsTestTransaction(isTestTransaction: Boolean?) {
    visibility = isTestTransaction?.let {
        if (it) View.VISIBLE else View.GONE
    } ?: View.GONE
}

@BindingAdapter("android:transferTo")
fun TextView.transferTo(inputString: String?) {
    inputString?.let {
        val totalString = context.getString(R.string.transfer_to_1s, it)
        val spannableString = SpannableString(totalString)
        spannableString.apply {
            setSpan(
                ForegroundColorSpan(Color.BLACK),
                13,
                totalString.length,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            setSpan(
                StyleSpan(Typeface.BOLD),
                13,
                totalString.length,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            setSpan(
                CustomFontSpan(ResourcesCompat.getFont(this@transferTo.context, R.font.exo)),
                0,
                13,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        }
        text = spannableString
    }
}

@BindingAdapter("android:loadImageFromPaymentMethodsImageUrl")
fun ImageView.loadImageFromPaymentMethodsImageUrl(imageUrl: String?) {
    imageUrl?.let {
        val imageLoader = ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()

        val request = ImageRequest.Builder(context)
            .data(it)
            .placeholder(R.drawable.image_pre_load_place_holder)
            .error(R.drawable.image_pre_load_place_holder)
            .target(this)
            .listener(
                onStart = {
                    Log.d("IMAGE_LOADING_TAG", "Image loading started")
                },
                onError = { _, errorResult ->
                    Log.e(
                        "IMAGE_LOADING_TAG",
                        "Image loading failed: ${errorResult.throwable.localizedMessage}",
                        errorResult.throwable
                    )
                },
                onSuccess = { _, _ ->
                    Log.d("IMAGE_LOADING_TAG", "Image loaded successfully")
                }
            )
            .build()

        imageLoader.enqueue(request)
    } ?: run {
        load(R.drawable.image_pre_load_place_holder) {
            crossfade(true)
            error(R.drawable.image_pre_load_place_holder)
            transformations(CircleCropTransformation())
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
    if (this.isEnabled) {
        setTextColor(ContextCompat.getColor(context, R.color.hydrogen_yellow))
    } else {
        setTextColor(ContextCompat.getColor(context, R.color.lighter_dark))
    }
}

@BindingAdapter("android:formatCardNumber")
fun TextInputEditText.formatCardNumber(optionalString: String? = null) {
    addTextChangedListener(object : TextWatcher {
        private var isFormatting: Boolean = false
        private var deletingHyphen: Boolean = false
        private var hyphenPosition: Int = 0
        private val CARD_NUMBER_FORMAT_LENGTH = 4

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            deletingHyphen = count > after && s?.get(start) == ' '
            hyphenPosition = start
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(editable: Editable?) {
            if (isFormatting || editable == null) return
            isFormatting = true

            val text = editable.toString().replace(" ", "")
            val formattedText = StringBuilder()

            for (i in text.indices) {
                if (i > 0 && i % CARD_NUMBER_FORMAT_LENGTH == 0) {
                    formattedText.append(" ")
                }
                formattedText.append(text[i])
            }

            removeTextChangedListener(this)
            setText(formattedText.toString())
            setSelection(formattedText.length)
            addTextChangedListener(this)

            isFormatting = false
        }
    })
}

@BindingAdapter("android:formatExpiryDate")
fun TextInputEditText.formatExpiryDate(optionalString: String? = null) {
    addTextChangedListener(object : TextWatcher {
        private var isFormatting: Boolean = false
        private var deletingSlash: Boolean = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            deletingSlash = count > after && s?.get(start) == '/'
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(editable: Editable?) {
            if (isFormatting || editable == null) return
            isFormatting = true

            val text = editable.toString().replace(" / ", "")
            val formattedText = StringBuilder()

            for (i in text.indices) {
                if (i == 2) formattedText.append(" / ")
                formattedText.append(text[i])
            }

            removeTextChangedListener(this)
            setText(formattedText.toString())
            setSelection(formattedText.length)
            addTextChangedListener(this)

            isFormatting = false
        }
    })
}

