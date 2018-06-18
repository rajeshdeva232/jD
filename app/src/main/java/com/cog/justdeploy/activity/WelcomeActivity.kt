package com.cog.justdeploy.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.cog.justdeploy.R
import com.cog.justdeploy.utils.BaseActivity
import com.cog.justdeploy.utils.Constants
import com.cog.justdeploy.utils.FontUtility
import kotlinx.android.synthetic.main.activity_welcome.*

/**
 * Created by GP on 16/04/18
 */
open class WelcomeActivity : BaseActivity() {

    private var fontUtility: FontUtility? = null

    /**
     *function
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        fontUtility = FontUtility(this)

        //Add Font Style
        setFontStyle()
        //Perform Action
        performClick()

    }

    companion object {
        val delayTime: Long = 1500
    }

    var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            val mDoubleIntent = Intent(Intent.ACTION_MAIN)
            mDoubleIntent.addCategory(Intent.CATEGORY_HOME)
            mDoubleIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(mDoubleIntent)

        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "click back again to Exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, delayTime)

    }

    private fun performClick() {
        ltGoogle.setOnClickListener({
            startActivity(Intent(applicationContext, GmailActivity::class.java))
        })


    }

    private fun setFontStyle() {
        tvGoogle.typeface = fontUtility?.medium
        tvWorkspace.typeface = fontUtility?.bold
    }
}

