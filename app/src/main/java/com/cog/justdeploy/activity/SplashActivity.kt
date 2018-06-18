package com.cog.justdeploy.activity

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.cog.justdeploy.R
import com.cog.justdeploy.utils.Constants
import com.cog.justdeploy.utils.FontUtility
import com.cog.justdeploy.utils.NetworkUtils
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by GP on 16/04/18
 */
class SplashActivity : AppCompatActivity() {

    private val requestCodeForMultiplePermmision = 5
    private var fontUtility: FontUtility? = null
    var sharedprf: SharedPreferences? = null
    /**
     *function
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Fabric.with(this, Crashlytics())
        fontUtility = FontUtility(this)
        sharedprf = PreferenceManager.getDefaultSharedPreferences(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkMultiplePermissions()
        } else {
            val handler = Handler()
            val signinstatus :Boolean?=sharedprf?.getString("signInStatus","")?.toBoolean()
            if(NetworkUtils.isNetworkAvailable(this)) {
                handler.postDelayed({
                    // Actions to do after 3 seconds
                    when (signinstatus) {
                        true -> startActivity(Intent((applicationContext), MainActivity::class.java))
                        false -> startActivity(Intent((applicationContext), WelcomeActivity::class.java))
                    }

                    finish()
                }, splashDelay.toLong())
            }
            else
            {
                showDialog(true, "Close")
            }
        }

    }

    private fun checkMultiplePermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            val permissionsNeeded = ArrayList<String>()
            val permissionsList = ArrayList<String>()
            if (!addPermission(permissionsList, android.Manifest.permission.CAMERA)) {
                permissionsNeeded.add("Camera")
            }

            if (!addPermission(permissionsList, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissionsNeeded.add("Read Storage")
            }

            if (!addPermission(permissionsList, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionsNeeded.add("Write Storage")
            }

            if (permissionsList.size > 0) {
                requestPermissions(permissionsList.toTypedArray(),
                        requestCodeForMultiplePermmision)
            } else {
                val handler = Handler()
                val signinstatus :Boolean?=sharedprf?.getString("signInStatus","")?.toBoolean()
                if(NetworkUtils.isNetworkAvailable(this)) {
                    handler.postDelayed({
                        // Actions to do after 3 seconds
                        when (signinstatus) {
                            true -> startActivity(Intent((applicationContext), MainActivity::class.java))
                            false -> startActivity(Intent((applicationContext), WelcomeActivity::class.java))
                        }

                        finish()
                    }, splashDelay.toLong())
                }
                else
                {
                    showDialog(true, "Close")
                }
            }
        }

    }

    private fun addPermission(permissionsList: MutableList<String>, permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= 23)

            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission)

                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false
            }
        return true
    }

    companion object {
    val splashDelay = 3000
}



internal fun showDialog(close: Boolean, text: String) {
    val dialogBuilder = AlertDialog.Builder(this,R.style.CustomDialog)
    val inflater = LayoutInflater.from(this)
    val updateDialog = inflater.inflate(R.layout.dialog_internet, null)
    dialogBuilder.setView(updateDialog)
    val updateDialogDb = dialogBuilder.create()
    updateDialogDb.setCancelable(false)
    updateDialogDb.show()
    val okDb = updateDialog.findViewById<TextView>(R.id.close)

    okDb.setOnClickListener {
        finish()
    }
}

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            requestCodeForMultiplePermmision -> {

                val perms = HashMap<String, Int>()
                // Initial
                perms[android.Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[android.Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                perms[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                perms[android.Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED

                // Fill with results
                for (i in permissions.indices)
                    perms[permissions[i]] = grantResults[i]
                if (perms[android.Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED
                        && perms[android.Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                        && perms[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED && perms[android.Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    startActivity(Intent(this, SplashActivity::class.java))
                    finish()
                    return
                } else {
                    // Permission Denied
                    if (Build.VERSION.SDK_INT >= 23) {
                        Toast.makeText(applicationContext, "Please permit all the permissions", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

}

