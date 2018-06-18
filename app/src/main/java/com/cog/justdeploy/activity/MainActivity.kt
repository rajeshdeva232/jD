package com.cog.justdeploy.activity

import android.annotation.TargetApi
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.cog.justdeploy.R
import com.cog.justdeploy.adapter.BitBucket
import com.cog.justdeploy.adapter.Github
import com.cog.justdeploy.model.BitBucketRepo
import com.cog.justdeploy.model.GithubRepo
import com.cog.justdeploy.utils.BaseActivity
import com.cog.justdeploy.utils.Constants
import com.cog.justdeploy.utils.FontUtility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Rajesh on 17/04/2018
 */
open class MainActivity : BaseActivity() {

    internal lateinit var storage: FirebaseStorage
    var dotPb: Dialog? = null
    var displayName: String? = null
    var imageUrl: String? = null
    var gmaiId: String? = null
    var googleId: String? = null
    var mongoId: String? = null
    var source: String? =  null
    val git = ArrayList<GithubRepo>()
    val bit = ArrayList<BitBucketRepo>()
   // private var slRepoReview: SwipeRefreshLayout? = null
    var bitstatus: Boolean? = false
    var gitstatus: Boolean? = false
    var phptoUrl: String? = null
    var sharedprf: SharedPreferences? = null
    private var fontUtility: FontUtility? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        storage = FirebaseStorage.getInstance()
       source = intent.getStringExtra("source")
        fontUtility = FontUtility(this)
       // slRepoReview = findViewById<SwipeRefreshLayout>(R.id.slRepoReview)
        slRepoReview?.isRefreshing = false
        //Add Font Style
        tvPlatform.typeface = fontUtility?.bold
        /*tvJustDeploy.typeface = fontUtility?.latoHeavy*/
        tvGone.typeface = fontUtility?.bold
        //loadImage()
        println("test case in main activity ")

        sharedprf = PreferenceManager.getDefaultSharedPreferences(this)
        mongoId = sharedprf?.getString("mongoId", "")
        println("MOONNGOOIDDD $mongoId")
        phptoUrl = sharedprf?.getString("imageUrl", "")
        Glide.with(this).load(phptoUrl).error(R.drawable.whiteusershape).into(profile)
        println("image url in share preference " + phptoUrl)
        println("source value in main activity " + source)
        if (source.toString().trim().equals(null)||source.toString().trim().equals("null"))
        {
            println("source is null")
            getDetails()
            gitUrl()
        }else if(source.toString().trim().equals("github"))
        {
            println("get intent github url called")
            ivArrow.setImageResource(R.drawable.ic_down)
            tvPlatform.text = "GitHub"
            ivLogo.setImageResource(R.drawable.ic_github)
            gitUrl()
        }else if(source.toString().trim().equals("bitbucket"))
        {
            println("get intent bitbucket url called")
            ivArrow.setImageResource(R.drawable.ic_down)
            tvPlatform.text = "Bitbucket"
            ivLogo.setImageResource(R.drawable.ic_bitbucket)
            bitUrl()
        }

        /*if (!source.toString().equals("none"))
        {
            println("source if called "+source)
            if (source.toString().trim().equals("github"))
            {
                println("get intent github url called")
                ivArrow.setImageResource(R.drawable.ic_down)
                tvPlatform.text = "GitHub"
                ivLogo.setImageResource(R.drawable.ic_github)
                gitUrl()
            }else{
                println("get intent bitbucket url called")
                ivArrow.setImageResource(R.drawable.ic_down)
                tvPlatform.text = "Bitbucket"
                ivLogo.setImageResource(R.drawable.ic_bitbucket)
                bitUrl()
            }
        }*/
        performAction()
    }


    companion object {
        val delayTime: Long = 1500
    }

    override fun onDestroy() {
        super.onDestroy()
        (dotPb as ProgressDialog?)?.dismiss()
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

    private fun getDetails() {
        val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
        val user = mAuth?.currentUser
        displayName = user?.displayName
        gmaiId = user?.email
        googleId = user?.uid
        println("user details in main activity " + displayName + " " + gmaiId + " " + imageUrl + " " + googleId)


    }

    /**
     * Bit loading
     */
    fun bitLoading() {
        runOnUiThread(Runnable {
            reporecycleView.layoutManager = LinearLayoutManager(this)
            val adapter = BitBucket(bit, this)
            adapter.notifyDataSetChanged()
            reporecycleView.adapter = adapter
        })
    }

    /**
     * Git loading
     */
    fun githubLoading() {
        //creating our adapter
        runOnUiThread(Runnable {
            reporecycleView.layoutManager = LinearLayoutManager(this)
            val adapter = Github(git, this)
            adapter.notifyDataSetChanged()
            reporecycleView.adapter = adapter

        })

    }

    private fun performAction() {

        profile.setOnClickListener {
            startActivity(Intent(applicationContext, ViewprofileActivity::class.java))
        }
        cvPlatform.setOnClickListener {
            ivArrow.setImageResource(R.drawable.up_arrow)
            val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(view)
            dialog.show()
            val ltGithub = view.findViewById<LinearLayout>(R.id.ltGithub)
            val ltBitbucket = view.findViewById<LinearLayout>(R.id.ltBitbucket)
            ltGithub.setOnClickListener {
                ivArrow.setImageResource(R.drawable.ic_down)
                tvPlatform.text = "GitHub"
                ivLogo.setImageResource(R.drawable.ic_github)
                dialog.dismiss()
                gitUrl()
            }
            ltBitbucket.setOnClickListener {
                ivArrow.setImageResource(R.drawable.ic_down)
                tvPlatform.text = "Bitbucket"
                ivLogo.setImageResource(R.drawable.ic_bitbucket)
                dialog.dismiss()
                bitUrl()
            }
            dialog.setOnCancelListener {
                ivArrow.setImageResource(R.drawable.ic_down)
            }
        }
        slRepoReview?.setOnRefreshListener(SwipyRefreshLayout.OnRefreshListener { direction ->
            slRepoReview?.isRefreshing = false
            if (tvPlatform.text.equals("GitHub")) {
                gitstatus = true
                println("Gitbuttoncheck")
                gitUrl()
            }
            if (tvPlatform.text.equals("Bitbucket")) {
                bitstatus = true
                println("Bitbuttoncheck")
                bitUrl()
            }
            bitstatus = false
            gitstatus = false

        })
       /* slRepoReview?.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
        })*/
    }

    /**
     * BITURL
     */
    fun bitUrl() {
        git.clear()
        reporecycleView.visibility = View.VISIBLE
        tvGone.visibility = View.GONE
        slRepoReview?.isRefreshing = true
        Log.i("bitfailure==>", bitstatus.toString())
        if (bitstatus == false) {
            dotPb = ProgressDialog.show(this, "dotPb title",
                    "dotPb message", false)
            dotPb?.setContentView(R.layout.layout_loading_dot)
            dotPb?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            (dotPb as ProgressDialog?)?.show()
        }
        val bitUrld = Constants.JDMAINURL + "/users/bitbucket/$mongoId/list"
        val request = Request.Builder().url(bitUrld).build()
        val gitClientd = OkHttpClient()
        gitClientd.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.d("bitfailure==>", "git ")
                val bitFailure = Snackbar.make(tvPlatform, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                val view = bitFailure.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view.layoutParams = params
                view.setBackgroundColor(Color.BLACK)
                val bitFailurermain = bitFailure.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                bitFailurermain?.typeface = fontUtility?.regular
                (dotPb as ProgressDialog?)?.dismiss()
                bitFailure.show()
            }

            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()
                println(body)
                Log.i("bitlist==>", body.toString())
                try {
                val json = JSONObject(body)
                val status = json.getString("status")

                println("json responce for bit in main activity " + json)
                val data = json.getJSONArray("data")
                val length = data.length()
                (dotPb as ProgressDialog?)?.dismiss()
                if (status.equals("200")) {
                    if (!length.equals(0)) {
                        bit.clear()
                        val bitdata = json.getJSONArray("data")
                        for (bitObj in 0..(bitdata.length() - 1)) {
                            val dataObj = bitdata.getJSONObject(bitObj)
                            val bitCardIdPos = dataObj.getString("_id")
                            val appName = dataObj.getString("appName")
                            val lastUpdated1: Long = dataObj.getLong("lastUpdated")
                            val bitStatus = dataObj.getString("lastDeployStatus")
                            val timeAgo = calculateDays(lastUpdated1)
                            println("bit data list $appName  $bitStatus$bitCardIdPos")
                            bit.add(BitBucketRepo(appName, timeAgo, bitStatus, bitCardIdPos))

                        }
                        bitLoading()
                    } else {
                        println(" bit error in loading list")
                        runOnUiThread(Runnable {
                            reporecycleView.visibility = View.GONE
                            tvGone.visibility = View.VISIBLE
                            (dotPb as ProgressDialog?)?.dismiss()
                        })
                    }
                } else {
                    Log.d("GitListCodeError==>", "GitListCodeError")
                    val bitError = Snackbar.make(tvPlatform, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                    val view = bitError.view
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.TOP
                    view.layoutParams = params
                    view.setBackgroundColor(Color.BLACK)
                    val bitErrormain = bitError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                    bitErrormain?.typeface = fontUtility?.regular
                    (dotPb as ProgressDialog?)?.dismiss()
                    bitError.show()
                }}catch (json: JSONException)  {json.printStackTrace()
                    Log.i("BitJSONError==>", json.toString())
                    val bitError = Snackbar.make(tvPlatform, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                    val view = bitError.view
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.TOP
                    view.layoutParams = params
                    view.setBackgroundColor(Color.BLACK)
                    val bitErrormain = bitError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                    bitErrormain?.typeface = fontUtility?.regular
                    bitError.show()
                    (dotPb as ProgressDialog?)?.dismiss() }
                catch (exception: Exception)  {Log.i("BitExceptionError==>", exception.toString())
                    val gitError = Snackbar.make(tvPlatform, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                    val view = gitError.view
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.TOP
                    view.layoutParams = params
                    view.setBackgroundColor(Color.BLACK)
                    val gitErrormain = gitError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                    gitErrormain?.typeface = fontUtility?.regular
                    gitError.show()
                    (dotPb as ProgressDialog?)?.dismiss()  }
            }
        })


        slRepoReview?.isRefreshing = false

    }

    private fun calculateDays(date: Long): String {

        val current = System.currentTimeMillis()
        val update = date
        println("current $current  update $update")
        val difference = current - update
        // Calculate difference in seconds
        val diffSeconds = difference / 1000

        // Calculate difference in minutes
        val diffMinutes = difference / (60 * 1000)

        // Calculate difference in hours
        val diffHours = difference / (60 * 60 * 1000)

        // Calculate difference in days
        val diffDays = difference / (24 * 60 * 60 * 1000)
        println("seconds $diffSeconds min $diffMinutes hr $diffHours day $diffDays")

        if (diffDays != 0.toLong()) {
            if (diffDays.toString().trim().equals("1")) {
             val day = diffDays.toString() + " day"
                return day
            }else {
               val day = diffDays.toString() + " days"
                return day
            }
        } else if (diffHours != 0.toLong()) {
            if (diffHours.toString().trim().equals("1")) {
                val hour = diffHours.toString() + " hr"
                return hour
            }else {
                val hour = diffHours.toString() + " hrs"
                return hour
            }
        } else if (diffMinutes != 0.toLong()) {
            if (diffMinutes.toString().trim().equals("1")) {
                val min = diffMinutes.toString() + " min"
                return min
            }else {
                val min = diffMinutes.toString() + " mins"
                return min
            }
        } else {
            if (diffSeconds.toString().trim().equals("1")) {
                val sec = diffSeconds.toString() + " sec"
                return sec
            }else {
                val sec = diffSeconds.toString() + " secs"
                return sec
            }

        }
    }

/**
 * GITURL
 */
fun gitUrl() {
    bit.clear()
    reporecycleView.visibility = View.VISIBLE
    tvGone.visibility = View.GONE
    slRepoReview?.isRefreshing = true
    Log.i("gitStatus==>", gitstatus.toString())
    println("gitStatus==> ${gitstatus.toString()}")
    if (gitstatus == false) {
        dotPb = ProgressDialog.show(this, "dotPb title",
                "dotPb message", false)
        dotPb?.setContentView(R.layout.layout_loading_dot)
        dotPb?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        (dotPb as ProgressDialog?)?.show()
    }
    val gitUrld = Constants.JDMAINURL + "/users/github/$mongoId/list"
    println("gitStatus==> $gitUrld")
    val request = Request.Builder().url(gitUrld).build()
    val gitClientd = OkHttpClient()
    gitClientd.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call?, e: IOException?) {
            Log.d("gitfailure==>", "git ")
            val gitFailure = Snackbar.make(tvPlatform, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = gitFailure.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.setBackgroundColor(Color.BLACK)
            val gitFailurermain = gitFailure.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            gitFailurermain?.typeface = fontUtility?.regular
            (dotPb as ProgressDialog?)?.dismiss()
            gitFailure.show()
        }

        override fun onResponse(call: Call?, response: Response?) {
            val body = response?.body()?.string()
            println(body)
            Log.i("gitlist==>", body.toString())
            println("gitlist==> ${body.toString()}")
            try {
            val json = JSONObject(body)
            val status = json.getString("status")
            val data = json.getJSONArray("data")
            val length = data.length()
            if (status.equals("200")) {
                if (!length.equals(0)) {
                    git.clear()
                    val gitdata = json.getJSONArray("data")
                    for (gitObj in 0..(gitdata.length() - 1)) {
                        //adding some dummy data to the list
                        val dataObj = gitdata.getJSONObject(gitObj)

                        val gitCardIdPos = dataObj.getString("_id")
                        val appName = dataObj.getString("appName")
                        val lastUpdated1: Long = dataObj.getLong("lastUpdated")
                        val gitStatus = dataObj.getString("lastDeployStatus")
                        val timeAgo = calculateDays(lastUpdated1)
                        println("git data list $appName $timeAgo $gitStatus$gitCardIdPos")
                        git.add(GithubRepo(appName, timeAgo, gitStatus, gitCardIdPos))
                        (dotPb as ProgressDialog?)?.dismiss()

                    }
                    githubLoading()
                } else {
                    println("error in loading list")
                    runOnUiThread(Runnable {
                        reporecycleView.visibility = View.GONE
                        tvGone.visibility = View.VISIBLE
                        (dotPb as ProgressDialog?)?.dismiss()
                    })

                }
            } else {
                Log.d("GitListCodeError==>", "GitListCodeError")
                val gitError = Snackbar.make(tvPlatform, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                val view = gitError.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view.layoutParams = params
                view.setBackgroundColor(Color.BLACK)
                val gitErrormain = gitError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                gitErrormain?.typeface = fontUtility?.regular
                (dotPb as ProgressDialog?)?.dismiss()
                gitError.show()
            }
        }catch (json: JSONException)  {json.printStackTrace()
                Log.i("GitJSONError==>", json.toString())
                val gitError = Snackbar.make(tvPlatform, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                val view = gitError.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view.layoutParams = params
                view.setBackgroundColor(Color.BLACK)
                val gitErrormain = gitError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                gitErrormain?.typeface = fontUtility?.regular
                gitError.show()
                (dotPb as ProgressDialog?)?.dismiss() }
            catch (exception: Exception)  {Log.i("GitExceptionError==>", exception.toString())
                val gitError = Snackbar.make(tvPlatform, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                val view = gitError.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view.layoutParams = params
                view.setBackgroundColor(Color.BLACK)
                val gitErrormain = gitError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                gitErrormain?.typeface = fontUtility?.regular
                gitError.show()
                (dotPb as ProgressDialog?)?.dismiss()  }
        }
    })

    slRepoReview?.isRefreshing = false

}


}
