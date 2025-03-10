package com.hydrogen.hydrogenpaymentsdk.utils

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpaymentsdk.domain.enums.DrawablePosition
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.LONG_NETWORK_RETRY_TIME
import com.hydrogen.hydrogenpaymentsdk.utils.AppUtils.boldAmountsInTransactionDetailsBalloonTexts
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retry
import okio.IOException
import java.net.SocketTimeoutException

object ExtensionFunctions {
    fun <T> Flow<ViewState<T?>>.retryAndCatchExceptions(networkUtil: NetworkUtil): Flow<ViewState<T?>> =
        this.retry(
            LONG_NETWORK_RETRY_TIME
        ) {
            return@retry it is SocketTimeoutException || it is IOException
        }.catch {
            val handledException = networkUtil.handleError<T>(it)
            emit(handledException)
        }

    fun View.getBalloon(text: String, balloonAlignment: DrawablePosition) {
        val balloonBuilder = Balloon.Builder(context).apply {
            height = BalloonSizeSpec.WRAP
            width = BalloonSizeSpec.WRAP
            setIsVisibleArrow(true)
            setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            setText(text)
            setTextColorResource(R.color.black)
            setTextSize(15f)
            setArrowSize(17)
            setArrowPosition(0.5f)
            // iconDrawable = context.getDrawable(R.drawable.ic_edit)
            arrowPositionRules = ArrowPositionRules.ALIGN_ANCHOR
            setPadding(10)
            autoDismissDuration = 7000L
            backgroundColor = ContextCompat.getColor(context, R.color.white)
            balloonAnimation = BalloonAnimation.ELASTIC
            lifecycleOwner = findViewTreeLifecycleOwner()
        }

        when (balloonAlignment) {
            DrawablePosition.LEFT -> balloonBuilder.setArrowOrientation(ArrowOrientation.END)
                .build().showAlignStart(this)

            DrawablePosition.RIGHT -> balloonBuilder.setArrowOrientation(ArrowOrientation.START)
                .build().showAlignEnd(this)

            DrawablePosition.BOTTOM -> balloonBuilder.setArrowOrientation(ArrowOrientation.TOP)
                .build().showAlignBottom(this)

            DrawablePosition.TOP -> balloonBuilder.setArrowOrientation(ArrowOrientation.BOTTOM)
                .build().showAlignTop(this)
        }
    }

    fun View.getTransactionInfoBalloon(transactionDetails: TransactionDetails) {
        val balloonBuilder = Balloon.Builder(context).apply {
            height = BalloonSizeSpec.WRAP
            width = BalloonSizeSpec.WRAP
            setIsVisibleArrow(true)
            setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            setLayout(R.layout.balloon_tool_tip_layout)
            setTextColorResource(R.color.black)
            arrowPositionRules = ArrowPositionRules.ALIGN_ANCHOR
            setPadding(2)
            autoDismissDuration = 7000L
            backgroundColor = ContextCompat.getColor(context, R.color.white)
            balloonAnimation = BalloonAnimation.ELASTIC
            lifecycleOwner = findViewTreeLifecycleOwner()
        }

        val balloonBuild = balloonBuilder.setArrowOrientation(ArrowOrientation.END)
            .build()
        balloonBuild.getContentView().findViewById<TextView>(R.id.balloon_text)
            .boldAmountsInTransactionDetailsBalloonTexts(
                transactionDetails.amount,
                transactionDetails.serviceFees,
                transactionDetails.vatPercentage.toString(),
                transactionDetails.vatFee,
                transactionDetails.discountAmount
            )
        balloonBuild.showAlignStart(this)
    }

    fun ProgressBar.toggleProgressBarVisibility(isVisible: Boolean) {
        visibility = if (isVisible) {

            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun Number.toDecimalPlace(decimalPlace: Int = AppConstants.INT_2_DECIMAL_PLACE): String =
        String.format("%.${decimalPlace}f", this)
}