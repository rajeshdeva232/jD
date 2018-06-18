package com.cog.justdeploy.utils


import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by GP
 */
object NetworkUtils {
    /**
     * Created by GP
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
