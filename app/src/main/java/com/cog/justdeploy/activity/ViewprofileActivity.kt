package com.cog.justdeploy.activity


import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.cog.justdeploy.R
import com.cog.justdeploy.utils.BaseActivity
import com.cog.justdeploy.utils.Constants
import com.cog.justdeploy.utils.FontUtility
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.plus.Plus
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonIOException
import kotlinx.android.synthetic.main.activity_build_log.*
import kotlinx.android.synthetic.main.activity_viewprofile.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

/**
 * Created by Rajesh on 17/04/2018
 */
open class ViewprofileActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    var image: String? = null
    var dialog: Dialog? = null
    var firstName: String? = null
    var lastName: String? = null
    var emailId: String? = null
    private var fontUtility: FontUtility? = null
    var sharedprf: SharedPreferences? = null
    var mongoId: String? = null
    private var mGoogleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewprofile)
        sharedprf = PreferenceManager.getDefaultSharedPreferences(this)
        val phptoUrl = sharedprf?.getString("imageUrl","")
        mongoId = sharedprf?.getString("mongoId","")
        println("image url in share preference view Profile "+phptoUrl)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build()
        fontUtility = FontUtility(this)



        setFontStyle()
        getProfileDetails(mongoId.toString())
        tvFirstNameLabel.isEnabled = false
        tvLastNameLabel.isEnabled = false
        tvEmailId.isEnabled = false
        peformClick()
    }

    companion object {
        internal val mPickImageRequest = 234
        val delayTime: Long = 1500
    }

    private fun setFontStyle() {
        tvView.typeface = fontUtility?.regular
        tvEdit.typeface = fontUtility?.regular
        tvUserName.typeface = fontUtility?.regular
        etFirstName.typeface = fontUtility?.regular
        etLastName.typeface = fontUtility?.regular
        etEmail.typeface = fontUtility?.regular
        btnSignout.typeface = fontUtility?.regular
        val font: Typeface = Typeface.createFromAsset(assets, "fonts/Roboto-Regular.ttf")
        tvFirstNameLabel.setTypeface(font)
        tvLastNameLabel.setTypeface(font)
        tvEmailId.setTypeface(font)

        dialog = ProgressDialog.show(this, "dialog title",
                "dialog message", false)
        dialog?.setContentView(R.layout.layout_loading_dot)
        dialog?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    public fun getProfileDetails(dpid:String) {

        dialog?.show()

        val gitUrld = Constants.JDMAINURL + "/users/profile/$dpid"
        val request = Request.Builder().url(gitUrld).build()
        val gitClientd = OkHttpClient()
        gitClientd.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.d("Info==>", "getProfileDetails failure ")
                val viewproFailure = Snackbar.make(etEmail, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                val view = viewproFailure.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view.layoutParams = params
                view.setBackgroundColor(Color.BLACK)
                val viewproFailmain = viewproFailure.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                viewproFailmain?.typeface = fontUtility?.regular
                dialog?.dismiss()
                viewproFailure.show()
            }

            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()
                println("url from view profile $response")
                Log.i("viewprofile==>", body.toString())
                try {


                val json = JSONObject(body)
                val status = json.getString("status")
                val data = json.getJSONArray("data")
                val dataObj = data.getJSONObject(0)
                firstName = dataObj.getString("firstName")
                if (status.equals("200")) {
                    lastName = dataObj.getString("lastName")
                    emailId = dataObj.getString("email")
                    image = dataObj.getString("profileImage")
                    println("image in view profile 123 $image")
                    runOnUiThread(Runnable {
                        etFirstName.setText(firstName)
                        etLastName.setText(lastName)
                        etEmail.setText(emailId)
                        tvUserName.text = firstName + " " + lastName
                        Glide.with(applicationContext).load(image).skipMemoryCache(true).centerCrop().error(R.drawable.whiteusershape).into(cimgUserprofile)
                        dialog?.dismiss()
                    })

                }
            }catch (jsonException:JsonIOException){
                    val viewError = Snackbar.make(tvView, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                    val view = viewError.view
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.TOP
                    view.layoutParams = params
                    view.setBackgroundColor(Color.BLACK)
                    val viewErrormain = viewError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                    viewErrormain?.typeface = fontUtility?.regular
                    viewError.show()
                    dialog?.dismiss()

                }
                catch (exception:Exception){
                    val viewError = Snackbar.make(tvView, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                    val view = viewError.view
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.TOP
                    view.layoutParams = params
                    view.setBackgroundColor(Color.BLACK)
                    val viewErrormain = viewError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                    viewErrormain?.typeface = fontUtility?.regular
                    viewError.show()
                    dialog?.dismiss()
                }
            }
        })
    }

    private fun peformClick() {
        ivEdit.setOnClickListener {
            intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
        btnSignout.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this, R.style.CustomDialog)
            val inflater = LayoutInflater.from(this)
            val updateDialog = inflater.inflate(R.layout.dialog_logout, null)
            dialogBuilder.setView(updateDialog)
            val updateDialogDb = dialogBuilder.create()
            updateDialogDb.setCancelable(false)
            updateDialogDb.show()
            val okDb = updateDialogDb.findViewById<TextView>(R.id.ok)
            val cancelDesc = updateDialogDb.findViewById<TextView>(R.id.cancel_desc)

            okDb?.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        object : ResultCallback<Status> {
                            override fun onResult(status: Status) {
                                val signInStatus:Boolean=false
                                sharedprf?.edit()?.putString("signInStatus",signInStatus.toString())?.apply()
                                sharedprf?.edit()?.putString("mongoId","")?.apply()
                                sharedprf?.edit()?.putString("imageUrl","")?.apply()
                                sharedprf?.edit()?.putString("firstName","")?.apply()
                                sharedprf?.edit()?.putString("googleId","")?.apply()
                                startActivity(Intent(applicationContext, WelcomeActivity::class.java))
                            }
                        })

                updateDialogDb.dismiss()
            }
            cancelDesc?.setOnClickListener { updateDialogDb.dismiss() }
        }

        ivBackArrow.setOnClickListener {

            startActivity(Intent((applicationContext), MainActivity::class.java))
            finish()
        }
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

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }

}
