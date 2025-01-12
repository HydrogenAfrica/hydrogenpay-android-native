package com.hydrogen.hydrogenpaymentsdk.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
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
                else -> doOnError.invoke(it.message)
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
}