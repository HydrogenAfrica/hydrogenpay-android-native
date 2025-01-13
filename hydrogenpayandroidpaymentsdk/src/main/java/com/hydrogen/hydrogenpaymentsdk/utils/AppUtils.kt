package com.hydrogen.hydrogenpaymentsdk.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.google.android.material.textfield.TextInputEditText
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpaymentsdk.domain.enums.DrawablePosition
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.CustomFontSpan
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.transferTo
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.Status
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal object AppUtils {
    fun String.getCustomerNameInitials(): String {
        val first = getOrNull(0)?.uppercase() ?: ""
        val second = split(" ").getOrNull(1)?.getOrNull(0)?.uppercase() ?: ""

        return "$first $second".trim()
    }

    fun String.toSentenceCase(): String = if (isNotBlank()) {
        replaceFirstChar { it.uppercase() }
    } else this

    fun formatNumberWithCommas(number: Double?): String {
        return number?.let {
            val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
            return numberFormat.format(number).let {
                if (it.contains(".")) it else "$it.00"
            }
        } ?: ""
    }

    fun <T> LifecycleOwner.observeLiveData(
        liveData: LiveData<ViewState<T>>,
        loaderDialog: Dialog?,
        doWhenLoading: (() -> Unit)?,
        doOnError: (errorMessage: String) -> Unit,
        doOnSuccess: (result: T?) -> Unit
    ) {
        liveData.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    loaderDialog?.show()
                    doWhenLoading?.invoke()
                }

                Status.ERROR -> {
                    loaderDialog?.cancel()
                    loaderDialog?.dismiss()
                    doOnError.invoke(it.message)
                }

                Status.SUCCESS -> {
                    loaderDialog?.cancel()
                    loaderDialog?.dismiss()
                    doOnSuccess.invoke(it.content)
                }

                else -> {
                    if (it.status != Status.INITIAL_DEFAULT) {
                        loaderDialog?.cancel()
                        loaderDialog?.dismiss()
                        doOnError.invoke(it.message)
                    }
                }
            }
        }
    }

    fun getLoadingAlertDialog(context: Context): Dialog = Dialog(context).apply {
        setContentView(R.layout.fragment_loading_dialog)
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        setCancelable(false)
        create()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun TextInputEditText.handleEditTextDrawableClick(
        drawablePosition: DrawablePosition = DrawablePosition.LEFT,
        actionToRun: () -> Unit
    ) {
        val position = if (drawablePosition == DrawablePosition.LEFT) 0 else 2
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (right - compoundDrawablesRelative[position].bounds.width())) {
                    actionToRun.invoke()
                }
            }
            false
        }
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipBoard: ClipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipBoard.setPrimaryClip(clip)
        Toast.makeText(context, label, Toast.LENGTH_SHORT)
            .show()
    }

    fun formatTransactionDateTime(inputTime: String): String {
        try {
            // Truncate the milliseconds to three digits if necessary
            val normalizedInputTime = inputTime.replace(Regex("\\.\\d{3}\\d*")) { matchResult ->
                ".${matchResult.value.substring(1, 4)}"
            }
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())

            // Parse the input time
            val date = inputFormat.parse(normalizedInputTime) ?: return "Invalid date"

            // Extract day, month, year, and time components
            val calendar = Calendar.getInstance().apply { time = date }
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Determine the ordinal suffix for the day
            val dayWithSuffix = when {
                day in 11..13 -> "${day}th"
                day % 10 == 1 -> "${day}st"
                day % 10 == 2 -> "${day}nd"
                day % 10 == 3 -> "${day}rd"
                else -> "${day}th"
            }

            // Format month, year, and time
            val monthYearFormat = SimpleDateFormat("MMM. yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

            val formattedMonthYear = monthYearFormat.format(date)
            val formattedTime = timeFormat.format(date)

            // Combine into the desired format
            return "$dayWithSuffix $formattedMonthYear | $formattedTime"
        } catch (e: Exception) {
            e.printStackTrace()
            return "Invalid date format"
        }
    }

    fun TextView.expiresIn(timeLeft: String?) {
        timeLeft?.let {
            val totalString = context.getString(R.string.expires_in_place_holder, it)
            val spannableString = SpannableString(totalString)
            spannableString.apply {
                setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    12,
                    totalString.length,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    12,
                    totalString.length,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                setSpan(
                    CustomFontSpan(ResourcesCompat.getFont(this@expiresIn.context, R.font.exo_bold)),
                    12,
                    totalString.length,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
            }
            text = spannableString
        }
    }
}