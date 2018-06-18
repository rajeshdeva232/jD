package com.cog.justdeploy.utils

import android.view.Gravity
import android.R.attr.gravity
import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import java.lang.reflect.AccessibleObject.setAccessible
import android.support.design.widget.TextInputLayout
import android.util.AttributeSet


/**
 * Created by test on 19/5/18.
 */
class CustomTextInputLayout(context: Context?, attrs: AttributeSet?) : TextInputLayout(context, attrs)/*(*//*context: Context, attrs: AttributeSet*//*) : TextInputLayout(context, attrs) */ {

    override fun setErrorEnabled(enabled: Boolean) {
        super.setErrorEnabled(enabled)

        if (!enabled) {
            return
        }

        try {
            val errorViewField = TextInputLayout::class.java.getDeclaredField("mErrorView")
            errorViewField.isAccessible = true
            val errorView = errorViewField.get(this) as TextView
            if (errorView != null) {
                errorView.gravity = Gravity.RIGHT
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.gravity = Gravity.END
                errorView.layoutParams = params
            }
        } catch (e: Exception) {
            // At least log what went wrong
            e.printStackTrace()
        }

    }
}