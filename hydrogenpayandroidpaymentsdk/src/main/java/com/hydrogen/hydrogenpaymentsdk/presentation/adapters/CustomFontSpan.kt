package com.hydrogen.hydrogenpaymentsdk.presentation.adapters

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

class CustomFontSpan(private val font: Typeface?): MetricAffectingSpan() {
    companion object {
        const val WRONG_TYPEFACE = 0
    }
    override fun updateDrawState(textPaint: TextPaint?) = updateTypeface(textPaint)

    override fun updateMeasureState(textPaint: TextPaint) = updateTypeface(textPaint)

    private fun updateTypeface(textPaint: TextPaint?) {
        textPaint?.apply {
            val oldStyle = getOldStyle(typeface)
            if (oldStyle == WRONG_TYPEFACE) return
            typeface = Typeface.create(font, oldStyle)
        }
    }

    private fun getOldStyle(typeface: Typeface?): Int = typeface?.style ?: WRONG_TYPEFACE
}