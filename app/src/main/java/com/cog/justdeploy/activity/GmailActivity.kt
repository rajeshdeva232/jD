package com.cog.justdeploy.activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Message
import android.os.StrictMode
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.cog.justdeploy.R
import com.cog.justdeploy.utils.BaseActivity
import com.cog.justdeploy.utils.Constants
import com.cog.justdeploy.utils.FontUtility
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.plus.Plus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.layout_loading_dot.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


/**
 * Created by GP on 24/04/18
 */
open class GmailActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var firstName: String
    lateinit var lastName: String
    lateinit var displayName: String
    lateinit var imageUrl: String
    lateinit var gmaiId: String
    lateinit var googleId: String
    internal var mGoogleSignInClient: GoogleSignInClient? = null
    private val tag = "gmail Using Firebase"
    internal var mAuth: FirebaseAuth? = null
    var sharedprf: SharedPreferences? = null
    private var mGoogleApiClient: GoogleApiClient? = null

    private var fontUtility: FontUtility? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_loading_dot)
        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        sharedprf = PreferenceManager.getDefaultSharedPreferences(this)
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        fontUtility = FontUtility(this)
        signIn()
    }

    companion object {
        internal var rcSignIn = 234
    }

    override fun onStart() {
        super.onStart()
        if (mAuth?.currentUser != null) {
            finish()
            println("last user method called")
            val user = mAuth?.currentUser
            displayName = user?.displayName.toString()
            gmaiId = user?.email.toString()
            imageUrl = user?.photoUrl.toString()
            googleId = user?.uid.toString()
            firstName = displayName.split(" ").get(0)
            lastName = displayName.split(" ").get(1)

            sharedprf?.edit()?.putString("googleId",googleId)?.apply()
            println("user details in already login " + displayName + " " + gmaiId + " " + imageUrl + " " + googleId + " " + firstName + " " + lastName)
            googleLogin(gmaiId, googleId, firstName, lastName, imageUrl)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcSignIn) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                startActivity(Intent(this, WelcomeActivity::class.java))

            }

        }
    }

    public fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {


        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val user = mAuth?.currentUser
                        displayName = user?.displayName.toString().trim()
                        gmaiId = user?.email.toString().trim()
                        imageUrl = user?.photoUrl.toString().trim()
                        googleId = user?.uid.toString().trim()
                        val signInStatus:Boolean = true
                        sharedprf?.edit()?.putString("googleId",googleId)?.apply()
                        sharedprf?.edit()?.putString("signInStatus", signInStatus.toString())?.apply()
                        firstName = displayName.split(" ").get(0).trim()
                        lastName = displayName.split(" ").get(1).trim()
                        println("user details in main activity " + displayName + " " + gmaiId + " " + imageUrl + " " + googleId + " " + firstName + " " + lastName)
                        googleLogin(gmaiId, googleId, firstName, lastName, imageUrl)

                        Log.d(tag, "signInWithCredential:success")


                    } else {
                        startActivity(Intent(this, WelcomeActivity::class.java))
                        // If sign in fails, display a message to the user.
                        Log.w(tag, "signInWithCredential:failure", task.exception)

                    }


                }
    }

    public fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, rcSignIn)
    }

    /**
     * PostURL Mainfunction
     */
    public fun googleLogin(fEmail: String, fUid: String, fFirstname: String, fLastname: String, fProfileImg: String) {

        val okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .build()

        println("URLGoogleLogin==> $fEmail $fFirstname $fLastname $fUid $fProfileImg")
        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                //Eg:ormbot-engine.herokuapp.com  https://justdeploy-engine.herokuapp.com/google-login
                .host(Constants.JDHOST)
                .addPathSegment("users")
                .addPathSegment("google_login")
                .build()

        val form = FormBody.Builder()
                .add("email", fEmail)
                .add("googleId", fUid)
                .add("firstName", fFirstname)
                .add("lastName", fLastname)
                .add("profileImage", fProfileImg)
                .build()


        try {
            updateResult(doSyncPost(okHttpClient, httpUrl, form))

        } catch (e: IOException) {
            e.printStackTrace()
            Log.i("Error==>", e.toString())
            val loginJson = Snackbar.make(avi, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = loginJson.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.setBackgroundColor(Color.BLACK)
            val loginJsonmain = loginJson.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            loginJsonmain?.typeface = fontUtility?.regular
            loginJson.show()
        }

    }

    public fun updateResult(myResponse: String) {
        Log.i("GoogleLogin==>", myResponse)
        try {

        val json = JSONObject(myResponse)
        println("status ==> ${json.getString("status")}")
        val status = json.getString("status")
        if (status.equals("200")) {
            //val message = json.getString("message")
            val data = json.getJSONObject("data")
            val mongoId = data.getString("_id")
            val imageUrl = data.getString("profileImage")
            val firstName = data.getString("firstName")
            Log.i("mongoid", mongoId)
            Log.i("photoUrl", imageUrl)
            sharedprf?.edit()?.putString("mongoId",mongoId)?.apply()
            sharedprf?.edit()?.putString("imageUrl",imageUrl)?.apply()
            sharedprf?.edit()?.putString("firstName",firstName)?.apply()
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {

         //   Toast.makeText(this, "Error in getting response ", Toast.LENGTH_SHORT).show()
            val loginJson = Snackbar.make(avi, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = loginJson.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.setBackgroundColor(Color.BLACK)
            val loginJsonmain = loginJson.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            loginJsonmain?.typeface = fontUtility?.regular
            loginJson.show()
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

        }}catch (jsonError: JSONException) {
            jsonError.printStackTrace()
            val loginJson = Snackbar.make(avi, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = loginJson.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.setBackgroundColor(Color.BLACK)
            val loginJsonmain = loginJson.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            loginJsonmain?.typeface = fontUtility?.regular
            loginJson.show()
        }

    }


    /**
     * PostURL
     */
    @Throws(IOException::class)
    fun doSyncPost(client: OkHttpClient, url: HttpUrl, body: RequestBody): String {
        return doSyncPost(client, url.toString(), body)
    }

    /**
     * PostURL
     */
    @Throws(IOException::class)
    fun doSyncPost(client: OkHttpClient, url: String, body: RequestBody): String {
        val request = Request.Builder()
                .url(url)
                .post(body)
                .build()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val response = client.newCall(request).execute()
        return response.body()?.string().toString()
    }
}
