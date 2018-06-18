package com.cog.justdeploy.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.Button
import com.cog.justdeploy.R
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder


/**
 * Created by GP on 31/5/18
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
    private var dialog: DialogPlus? = null
    var alertDialog: Dialog? =null
    private var fontUtility: FontUtility? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fontUtility = FontUtility(this)
        registerReceiver(ConnectivityReceiver(),
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }


    private fun showMessage(isConnected: Boolean) {
        dialog = DialogPlus.newDialog(this)
                .setContentHolder(ViewHolder(R.layout.snackbar))
                .setGravity(Gravity.TOP)
                .setCancelable(false)
                .setExpanded(false) // This will enable the expand feature, (similar to android L share dialog)
                .create()
        if (!isConnected) {

            val button = dialog?.findViewById(R.id.snretry) as Button
            button.setOnClickListener {
                val intent = intent
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                finish()
                startActivity(intent) }
            dialog?.show()

        } else {
            dialog?.dismiss()
            //alertDialog?.dismiss()
        }


    }

    override fun onResume() {
        super.onResume()

        ConnectivityReceiver.connectivityReceiverListener = this
    }

    /**
     * Callback will be called when there is change
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }
}
