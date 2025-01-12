package com.hydrogen.hydrogenpaymentsdk.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpaymentsdk.domain.enums.DrawablePosition
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.Status
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import java.text.NumberFormat
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
        doWhenLoading: () -> Unit,
        doOnError: (errorMessage: String) -> Unit,
        doOnSuccess: (result: T?) -> Unit
    ) {
        liveData.observe(this) {
            when (it.status) {
                Status.LOADING -> doWhenLoading.invoke()
                Status.ERROR -> doOnError.invoke(it.message)
                Status.SUCCESS -> doOnSuccess.invoke(it.content)
                else -> {
                    if (it.status != Status.INITIAL_DEFAULT) {
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
    fun TextView.handleDrawableRightClick(
        drawablePosition: DrawablePosition = DrawablePosition.LEFT,
        actionToRun: () -> Unit
    ) {
        setOnTouchListener { _, event ->
            val position = if (drawablePosition == DrawablePosition.LEFT) 0 else 2
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = compoundDrawablesRelative[position] // Get the right drawable
                if (drawable != null) {
                    val drawableWidth = drawable.bounds.width()
                    // Check if the touch is within the bounds of the right drawable
                    if (event.rawX >= (right - drawableWidth - paddingEnd)) {
                        // Handle the click on the drawable
                        actionToRun.invoke()
                        return@setOnTouchListener true
                    }
                }
            }
            return@setOnTouchListener false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun TextView.handleEditTextDrawableClick(
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
}