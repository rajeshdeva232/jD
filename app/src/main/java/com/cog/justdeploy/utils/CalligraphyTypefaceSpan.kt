package com.cog.justdeploy.utils

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

/**
 * Created by test on 26/4/18.
 */
class CalligraphyTypefaceSpan(private val typeface: Typeface) : MetricAffectingSpan() {

    init {
        if (typeface == null) {
            throw IllegalArgumentException("typeface is null")
        }
    }

    override fun updateDrawState(drawState: TextPaint) {
        apply(drawState)
    }

    override fun updateMeasureState(paint: TextPaint) {
        apply(paint)
    }

    private fun apply(paint: Paint) {
        val oldTypeface = paint.typeface
        val oldStyle = oldTypeface?.style ?: 0
        val fakeStyle = oldStyle and typeface.getStyle().inv()

        if (fakeStyle and Typeface.BOLD != 0) {
            paint.isFakeBoldText = true
        }

        if (fakeStyle and Typeface.ITALIC != 0) {
            paint.textSkewX = floatValue
        }

        paint.typeface = typeface
    }

    companion object {
        val floatValue = -0.25f
    }
}
