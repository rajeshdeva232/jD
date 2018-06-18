package com.cog.justdeploy.activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.os.SystemClock
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.cog.justdeploy.R
import com.cog.justdeploy.utils.BaseActivity
import com.cog.justdeploy.utils.Constants
import com.cog.justdeploy.utils.FontUtility
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_build_log.*
import okhttp3.*
import org.joda.time.DateTime
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by GP on 10/5/18
 */
open class BuildLogActivity : AppCompatActivity() {


    internal var status: Int = 0
    internal lateinit var handler: Handler
    private var fontUtility: FontUtility? = null
    var phptoUrl: String? = null
    var userId: String? = null
    var appName: String? = null
    var userName: String? = null
    var noOfDeployes: String? = null
    var gbAuthKey: String? = null
    var branch: String? = null
    var repoName: String? = null
    var herokuAuthKey: String? = null
    var repoUser: String? = null
    var sourceName: String? = null
    var bitRefreshKey: String? = null
    var buildHistoryId: String? = null
    var projectId: String? = null
    var output_stream_url: String? = null
    var buildTimelast: String? = null
    var buildStatus: String? = null
    var sourceId: String? = null
    var firstName: String? = null
    var sharedprf: SharedPreferences? = null
    var id: String? = null
    var dialog: ProgressDialog? = null
    var totalTime = "00:00:00"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_build_log)
        sharedprf = PreferenceManager.getDefaultSharedPreferences(this)
        phptoUrl = sharedprf?.getString("imageUrl", "")
        firstName = sharedprf?.getString("firstName", "")
        val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
        val user = mAuth?.currentUser
        userName = user?.displayName
        Log.d("BuildLogURlimage", phptoUrl)
        noOfDeployes = intent.getStringExtra("noOfDeployes")
        buildHistoryId = intent.getStringExtra("buildHistoryId")
        appName = intent.getStringExtra("appName")
        projectId = intent.getStringExtra("projectId")
        userId = intent.getStringExtra("userId")
        gbAuthKey = intent.getStringExtra("gbAuthKey")
        branch = intent.getStringExtra("branch")
        repoName = intent.getStringExtra("repoName")
        herokuAuthKey = intent.getStringExtra("herokuAuthKey")
        repoUser = intent.getStringExtra("repoUser")
        sourceName = intent.getStringExtra("sourceName")
        sourceId = intent.getStringExtra("sourceId")
        println("details in get intent $gbAuthKey $branch $repoName $herokuAuthKey $repoUser $sourceName $projectId $sourceId$firstName")
        Glide.with(this).load(phptoUrl).error(R.drawable.whiteusershape).into(profile)
        fontUtility = FontUtility(this)
        handler = Handler()
        setFontStyle()
        performclick()
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

    private fun setFontStyle() {
        tvBuildlog.typeface = fontUtility?.medium
        tv_startContainer.typeface = fontUtility?.regular
        tv_startContainer1.typeface = fontUtility?.regular
        tv_startContainer2.typeface = fontUtility?.regular
        tv_timer.typeface = fontUtility?.italic
        tv_timer1.typeface = fontUtility?.italic
        tv_timer2.typeface = fontUtility?.italic
        tvProjectBuild.typeface = fontUtility?.regular
        tvStatusBuild.typeface = fontUtility?.medium
        tvFinishBuild.typeface = fontUtility?.regular
        tvFinishedBuild.typeface = fontUtility?.medium
        tvTimeBuild.typeface = fontUtility?.regular
        tvTimingBuild.typeface = fontUtility?.medium
        tvNoOfDeployBuild.typeface = fontUtility?.regular
        tvNoOfDeploysBuild.typeface = fontUtility?.medium
        tvDeployByBuild.typeface = fontUtility?.regular
        tvDeployedByBuild.typeface = fontUtility?.medium
        btn_dashboard.typeface = fontUtility?.bold

        tvStatusBuild.text = "Pending"
        tvStatusBuild.setBackgroundResource(R.drawable.buttonbackgroundshadow)
        tvProjectBuild.text = appName
        tvProjectBuild.setTextColor(Color.parseColor("#ff8c00"))
        tvFinishedBuild.text = "-"
        tvTimingBuild.text = "-"
        tvNoOfDeploysBuild.text = noOfDeployes
        tvDeployedByBuild.text = firstName
        btn_dashboard.isEnabled = false
    }

    private fun performclick() {
        cv_container.visibility = View.VISIBLE
        cv_container.visibility = View.VISIBLE
        tv_timer.base = SystemClock.elapsedRealtime()
        tv_timer.start()
        val createBuild = Thread(Runnable {
            buildCreate(projectId.toString(), sourceId.toString(), userId.toString())
        })
        createBuild.start()
        btn_dashboard.setOnClickListener {
            dialog = ProgressDialog.show(this, "dotPb title",
                    "dotPb message", false)
            dialog?.setContentView(R.layout.layout_loading_dot)
            dialog?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.show()
            buildTimelast = tvTimingBuild.text.toString()
            val hours: Int = buildTimelast!!.split(":")[0].toInt()
            val min: Int = buildTimelast!!.split(":")[1].toInt()
            val sec: Int = buildTimelast!!.split(":")[2].toInt()
            println("min in send buid $hours $min $sec " + (min * 60) + sec)
            val totalSec: Int = ((hours * 3600) + (min * 60) + sec) * 1000
            val createBuild = Thread(Runnable {
                updateDashboardBuild(totalSec)
            })
            createBuild.start()

        }
    }

    fun timeTaken(time: String) {
        val totalTimeMinSec = ((totalTime.split(":")[0].trim().toInt() * 60) + totalTime.split(":")[1].trim().toInt()) * 60 + totalTime.split(":")[2].trim().toInt()
        val totalSec = ((0 * 60) + time.split(":")[0].trim().toInt()) * 60 + time.split(":")[1].trim().toInt();

        val totalSecnds = totalTimeMinSec + totalSec
        val hours = totalSecnds / 3600  // Be sure to use integer arithmetic
        val minutes = totalSecnds / 60 % 60
        val seconds = totalSecnds % 60
        totalTime = hours.toString() + ":" + minutes + ":" + seconds.toShort()
        tvTimingBuild.text = totalTime
        println("totalTime in calculation $totalTime")
    }

    public fun deployGit(gbAuthKey: String, branch: String, repoName: String, appName: String, herokuAuthKey: String, repoUser: String) {
        val okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .readTimeout(60, TimeUnit.MINUTES)
                .build()
        println("GitdeployURL==> $gbAuthKey $branch $repoName $appName $herokuAuthKey $repoUser")
        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                //Eg:ormbot-engine.herokuapp.com  https://justdeploy-engine.herokuapp.com/google-login
                .host(Constants.JDHOST)
                .addPathSegment("deployGit")
                .build()

        val form = FormBody.Builder()
                .add("token", gbAuthKey)
                .add("branch", branch)
                .add("repoName", repoName)
                .add("appName", appName)
                .add("herokuToken", herokuAuthKey)
                .add("repoUser", repoUser)
                .build()

        try {
            updateGitBitResult(doSyncPost(okHttpClient, httpUrl, form))

        } catch (e: IOException) {
            runOnUiThread {
            e.printStackTrace()
            Log.i("GitError1==>", e.toString())
            val deployError = Snackbar.make(tvStatusBuild, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = deployError.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.setBackgroundColor(Color.BLACK)
            val deployErrormain = deployError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            deployErrormain?.typeface = fontUtility?.regular
            deployError.show()
                tv_timer1.stop()
                timeTaken(tv_timer1.text.toString())
                progressLoader1.visibility=View.GONE
                rlcontaincer1.setBackgroundResource(R.color.red)
                img_arrow1.visibility=View.VISIBLE
                img_arrow1.setImageResource(R.drawable.animated_vector_cross)
                (img_arrow1!!.getDrawable() as Animatable).start()
                Handler().postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("source", sourceName)
                    startActivity(intent)
                },515)
                btn_dashboard.isEnabled=true

            }
        }
    }

    //This function use for both Git & Bit updateResult in DeployURL
    public fun updateGitBitResult(myResponse: String) {
        try {
            Log.i("deployGitBit==>", myResponse)
            val json = JSONObject(myResponse)
            val status = json.getString("status")
            if (status.trim().equals("200")) {
                println("deploy git test called $myResponse")
                runOnUiThread {
                    tv_timer1.stop()
                    progressLoader1.visibility = View.GONE
                    rlcontaincer1.setBackgroundResource(R.color.green)
                    img_arrow1.visibility = View.VISIBLE
                    (img_arrow1!!.getDrawable() as Animatable).start()
                    Handler().postDelayed({
                        cv_container2.visibility = View.VISIBLE
                        tv_timer2.base = SystemClock.elapsedRealtime()
                        tv_timer2.start()
                        timeTaken(tv_timer1.text.toString())
                    }, 515)
                }
                val data = json.getJSONObject("data")
                id = data.getString("id")
                output_stream_url = data.getString("output_stream_url")

                println("reponse in GitBit deploy $status $id $appName $herokuAuthKey   $output_stream_url")
                var gitbitresult = buildStatus(id.toString(), appName.toString(), herokuAuthKey.toString())
                Log.i("GitBitResult==>", gitbitresult)
                if (gitbitresult.equals("succeeded") || gitbitresult.equals("failed")) {
                    runOnUiThread {
                        tv_timer2.stop()
                        val timeText = tv_timer2.text
                        timeTaken(timeText.toString())
                        btn_dashboard.isEnabled = true
                        if (gitbitresult.equals("failed")) {

                            val dt = DateTime()
                            val current = dt.millis
                            val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")
                            val resultDate = Date(current)
                            println("current date in build log " + simpleDateFormat.format(resultDate))
                            println("current date $current ")
                            val cl = Calendar.getInstance()
                            cl.timeInMillis = current
//                        println("time "+cl.get(Calendar.HOUR_OF_DAY +cl.get(Calendar.MINUTE + cl.get(Calendar.SECOND))))
                            rlcontaincer2.setBackgroundResource(R.color.red)
                            tvStatusBuild.text = "Failed"
                            buildStatus = "3"
                            tv_startContainer2.text="Build Failed"
                            tvProjectBuild.setTextColor(Color.parseColor("#f10b59"))
                            tvStatusBuild.setBackgroundResource(R.drawable.buttonbackgroundshadowerror)
                            progressLoader2.visibility = View.GONE
                            img_arrow2.visibility = View.VISIBLE
                            img_arrow2.setImageResource(R.drawable.animated_vector_cross)
                            (img_arrow2?.getDrawable() as Animatable).start()
                        } else {
                            tvStatusBuild.text = "Success"
                            buildStatus = "2"
                            tv_startContainer2.text="Build Success"
                            rlcontaincer2.setBackgroundResource(R.color.green)
                            tvProjectBuild.setTextColor(Color.parseColor("#377303"))
                            tvStatusBuild.setBackgroundResource(R.drawable.buttonbackgroundshadowsuccess)
                            rlcontaincer2.setBackgroundResource(R.color.green)
                            progressLoader2.visibility = View.GONE
                            img_arrow2.visibility = View.VISIBLE
                            img_arrow2.setImageResource(R.drawable.animated_vector_check)
                            (img_arrow2?.getDrawable() as Animatable).start()
                        }
                    }
                    println("Results==>$gitbitresult")
                } else if (gitbitresult.equals("pending")) {
                    val timer = Timer()
                    timer.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            if (id != null && appName != null && herokuAuthKey != null) {
                                gitbitresult = buildStatus(id.toString(), appName.toString(), herokuAuthKey.toString())
                                if (gitbitresult.equals("succeeded") || gitbitresult.equals("failed")) {
                                    runOnUiThread {
                                        tv_timer2.stop()
                                        val timeText = tv_timer2.text
                                        btn_dashboard.isEnabled = true
                                        timeTaken(timeText.toString())
                                        if (gitbitresult.equals("failed")) {
                                            tvStatusBuild.text = "Failed"
                                            buildStatus = "3"
                                            tv_startContainer2.text="Build Failed"
                                            rlcontaincer2.setBackgroundResource(R.color.red)
                                            tvProjectBuild.setTextColor(Color.parseColor("#f10b59"))
                                            tvStatusBuild.setBackgroundResource(R.drawable.buttonbackgroundshadowerror)
                                            rlcontaincer2.setBackgroundResource(R.color.red)
                                            progressLoader2.visibility = View.GONE
                                            img_arrow2.visibility = View.VISIBLE
                                            img_arrow2.setImageResource(R.drawable.animated_vector_cross)
                                            (img_arrow2?.getDrawable() as Animatable).start()
                                        } else {
                                            buildStatus = "2"
                                            tvStatusBuild.text = "Success"
                                            tv_startContainer2.text="Build Success"
                                            rlcontaincer2.setBackgroundResource(R.color.green)
                                            tvProjectBuild.setTextColor(Color.parseColor("#377303"))
                                            tvStatusBuild.setBackgroundResource(R.drawable.buttonbackgroundshadowsuccess)
                                            rlcontaincer2.setBackgroundResource(R.color.green)
                                            progressLoader2.visibility = View.GONE
                                            img_arrow2.visibility = View.VISIBLE
                                            img_arrow2.setImageResource(R.drawable.animated_vector_check)
                                            (img_arrow2?.getDrawable() as Animatable).start()
                                        }
                                    }
                                    println("Timer==>$gitbitresult")
                                    timer.cancel()

                                }

                            }

                        }
                    }, 0, 15000)

                }


            } else {
                runOnUiThread {
                    buildStatus="3"
                    timeTaken(tv_timer1.text.toString())
                progressLoader1.visibility = View.GONE
                println("Error In  GitBitDeploy Response")
                val deployError = Snackbar.make(tvStatusBuild, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                val view = deployError.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view.layoutParams = params
                view.setBackgroundColor(Color.BLACK)
                val deployErrormain = deployError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                deployErrormain?.typeface = fontUtility?.regular
                deployError.show()
                    tv_timer1.stop()
                    timeTaken(tv_timer1.text.toString())
                    progressLoader1.visibility=View.GONE
                    rlcontaincer1.setBackgroundResource(R.color.red)
                    img_arrow1.visibility=View.VISIBLE
                    img_arrow1.setImageResource(R.drawable.animated_vector_cross)
                    (img_arrow1!!.getDrawable() as Animatable).start()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("source",sourceName)
                    startActivity(intent)
                    btn_dashboard.isEnabled=true
                }

            }
        } catch (json: JSONException) {
            runOnUiThread {
                buildStatus="3"
                tv_timer1.stop()
                timeTaken(tv_timer1.text.toString())
            progressLoader1.visibility = View.GONE
            println("Error in json exception 3")
            val deployError = Snackbar.make(tvStatusBuild, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = deployError.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.setBackgroundColor(Color.BLACK)
            val deployErrormain = deployError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            deployErrormain?.typeface = fontUtility?.regular
            deployError.show()
                timeTaken(tv_timer1.text.toString())
                progressLoader1.visibility=View.GONE
                rlcontaincer1.setBackgroundResource(R.color.red)
                img_arrow1.visibility=View.VISIBLE
                img_arrow1.setImageResource(R.drawable.animated_vector_cross)
                (img_arrow1!!.getDrawable() as Animatable).start()
                Handler().postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("source",sourceName)
                    startActivity(intent)
                },515)
                btn_dashboard.isEnabled=true
                }

    }catch (json: Exception) {
            runOnUiThread {
                buildStatus="3"
                tv_timer1.stop()
                timeTaken(tv_timer1.text.toString())
                progressLoader1.visibility = View.GONE
                println("Error in json exception 1")
                val deployError = Snackbar.make(tvStatusBuild, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                val view = deployError.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view.layoutParams = params
                view.setBackgroundColor(Color.BLACK)
                val deployErrormain = deployError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                deployErrormain?.typeface = fontUtility?.regular
                deployError.show()
                timeTaken(tv_timer1.text.toString())
                progressLoader1.visibility=View.GONE
                /*val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("source",sourceName)
                startActivity(intent)*/
               // rlcontaincer1.setBackgroundResource(R.color.red)
                /*img_arrow1.visibility=View.VISIBLE
                img_arrow1.setImageResource(R.drawable.animated_vector_cross)
                (img_arrow1!!.getDrawable() as Animatable).start()*/
                btn_dashboard.isEnabled=true
            }


        }}

    private fun deployBitbucket(gbAuthKey: String, branch: String, repoName: String, appName: String, herokuAuthKey: String, repoUser: String, refreshKey: String) {

        val okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .readTimeout(60, TimeUnit.MINUTES)
                .build()


        println("BitdeployURL==> $gbAuthKey $branch $repoName $appName $herokuAuthKey $repoUser")
        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                //Eg:ormbot-engine.herokuapp.com  https://justdeploy-engine.herokuapp.com/google-login
                .host(Constants.JDHOST)
                .addPathSegment("deployBitbucket")
                .build()

        val form = FormBody.Builder()
                .add("authToken", gbAuthKey)
                .add("branch", branch)
                .add("repoName", repoName)
                .add("appName", appName)
                .add("herokuToken", herokuAuthKey)
                .add("repoUser", repoUser)
                .add("bucketRefreshKey", refreshKey)
                .build()


        try {
            updateGitBitResult(doSyncPost(okHttpClient, httpUrl, form))

        } catch (e: Exception) {
            runOnUiThread {
            e.printStackTrace()
            Log.i("BitError1==>", e.toString())
            val deployError = Snackbar.make(tvStatusBuild, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = deployError.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.setBackgroundColor(Color.BLACK)
            val deployErrormain = deployError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            deployErrormain?.typeface = fontUtility?.regular
            deployError.show()
                progressLoader1.visibility=View.GONE
                rlcontaincer1.setBackgroundResource(R.color.red)
                img_arrow1.setImageResource(R.drawable.animated_vector_cross)
                (img_arrow1!!.getDrawable() as Animatable).start()
                img_arrow1.visibility=View.VISIBLE
                Handler().postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("source",sourceName)
                    startActivity(intent)
                },515)
                btn_dashboard.isEnabled=true
                }
        }
    }

    fun buildStatus(id: String, appName: String, herokuAuthKey: String): String? {
        var buildurl: String? = null
        println("values in build statue==> $id $appName $herokuAuthKey")
        val okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .readTimeout(60, TimeUnit.MINUTES)
                .build()
        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                //Eg:ormbot-engine.herokuapp.com  https://justdeploy-engine.herokuapp.com/google-login
                .host(Constants.JDHOST)
                .addPathSegment("buildStatus")
                .build()

        val form = FormBody.Builder()
                .add("buildId", id)
                .add("appName", appName)
                .add("authKey", herokuAuthKey)
                .build()


        try {
            buildurl = updatebuildStatusResult(doSyncPost(okHttpClient, httpUrl, form))
        } catch (e: IOException) {
            runOnUiThread {
            e.printStackTrace()
            Log.i("Error2==>", e.toString())
            println("Error123==> container 3 error called")
            val buildError = Snackbar.make(tvStatusBuild, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = buildError.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.setBackgroundColor(Color.BLACK)
            val buildErrormain = buildError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            buildErrormain?.typeface = fontUtility?.regular
            buildError.show()
                tv_timer2.stop()
                timeTaken(tv_timer2.text.toString())
                progressLoader2.visibility=View.GONE
                rlcontaincer2.setBackgroundResource(R.color.red)
                img_arrow2.visibility=View.VISIBLE
                img_arrow2.setImageResource(R.drawable.animated_vector_cross)
                (img_arrow2!!.getDrawable() as Animatable).start()
                Handler().postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("source", sourceName)
                    startActivity(intent)
                },515)
                btn_dashboard.isEnabled=true
              }
        }
//        Log.i("buildurl==>", buildurl)
        return buildurl
    }

    private fun updatebuildStatusResult(myResponse: String): String? {
        var buildS: String? = null
        try {
            println("buildStatus==> $myResponse")
            val json = JSONObject(myResponse)
            val status = json.getString("status")
            if (status.trim().equals("200")) {
                if (json.getJSONArray("data").length() != 0) {
                    val data = json.getJSONArray("data")
                    val jsonarray = data.getJSONObject(0)
                    val buildStatus = jsonarray.getString("buildStatus")
                    Log.d("buildstatusres==>", buildStatus)
                    buildS = buildStatus
                }
            } else {
                runOnUiThread {
                    timeTaken(tv_timer2.text.toString())
                println("Error In  Build Status Response")
                val buildError = Snackbar.make(tvStatusBuild, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                val view = buildError.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view.layoutParams = params
                view.setBackgroundColor(Color.BLACK)
                val buildErrormain = buildError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                buildErrormain?.typeface = fontUtility?.regular
                buildError.show()
                    tv_timer2.stop()
                    timeTaken(tv_timer2.text.toString())
                    progressLoader2.visibility=View.GONE
                    rlcontaincer2.setBackgroundResource(R.color.red)
                    img_arrow2.visibility=View.VISIBLE
                    img_arrow2.setImageResource(R.drawable.animated_vector_cross)
                    (img_arrow2!!.getDrawable() as Animatable).start()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("source",sourceName)
                    startActivity(intent)
                    btn_dashboard.isEnabled=true
                    }
            }
        }catch (json: JSONException)  {
            runOnUiThread {
                tv_timer2.stop()
            timeTaken(tv_timer2.text.toString())
            println("Error in json exception 2")
            val buildError = Snackbar.make(tvStatusBuild, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = buildError.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.setBackgroundColor(Color.BLACK)
            val buildErrormain = buildError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            buildErrormain?.typeface = fontUtility?.regular
            buildError.show()
                /*progressLoader1.visibility=View.GONE
                rlcontaincer2.setBackgroundResource(R.color.red)
                img_arrow2.visibility=View.VISIBLE*/
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("source",sourceName)
                startActivity(intent)
               /* img_arrow2.setImageResource(R.drawable.animated_vector_cross)
                (img_arrow2!!.getDrawable() as Animatable).start()*/
                btn_dashboard.isEnabled=true
                }
        }

        Log.i("buildS==>", buildS)
        return buildS
    }

    fun buildCreate(projectId: String, sourceId: String, userId: String) {

        val okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .build()
        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                //Eg:ormbot-engine.herokuapp.com  https://justdeploy-engine.herokuapp.com/google-login
                .host(Constants.JDHOST)
                .addPathSegment("users")
                .addPathSegment("build")
                .addPathSegment("createBuild")
                .build()

        val form = FormBody.Builder()
                .add("userId", userId)
                .add("projectId", projectId)
                .add("deployStatus", "0")
                .add("sourceId", sourceId)
                .add("buildStatus", "0")
                .add("buildInfo", "0")
                .add("buildTime", "0")
                .add("buildLog", "0")
                .add("buildStreamUrl", "0")
                .build()

        try {
            updateBuildcreateResult(doSyncPost(okHttpClient, httpUrl, form))

        } catch (e: IOException) {
            runOnUiThread {
            e.printStackTrace()
                tv_timer.stop()
                timeTaken(tv_timer.text.toString())
            Log.i("Error1==>", e.toString())
            val createError = Snackbar.make(tvStatusBuild, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = createError.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.setBackgroundColor(Color.BLACK)
            val createErrormain = createError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            createErrormain?.typeface = fontUtility?.regular
            createError.show()
                progressLoader.visibility=View.GONE
                rlcontaincer.setBackgroundResource(R.color.red)
                img_arrow.visibility=View.VISIBLE
                img_arrow.setImageResource(R.drawable.animated_vector_cross)
                (img_arrow!!.getDrawable() as Animatable).start()
                Handler().postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("source", sourceName)
                    startActivity(intent)
                },515)
                btn_dashboard.isEnabled=true
               }
        }

    }

    public fun updateBuildcreateResult(myResponse: String) {
        try {
            Log.i("BuildCreate==>", myResponse)
            val json = JSONObject(myResponse)
            val status = json.getString("status")
            if (status.trim().equals("200")) {
                println("status in if aprt $status")
                runOnUiThread {
                    tv_timer.stop()
                    tv_startContainer.text="Build Initiated"
                    progressLoader.visibility = View.GONE
                    rlcontaincer.setBackgroundResource(R.color.green)
                    img_arrow.visibility = View.VISIBLE
                    (img_arrow!!.getDrawable() as Animatable).start()
                    Handler().postDelayed({
                        cv_container1.visibility = View.VISIBLE
                        tv_timer1.base = SystemClock.elapsedRealtime()
                        tv_timer1.start()
                        timeTaken(tv_timer.text.toString())
                        val t = Thread(Runnable {
                            println("control in above the thread method ")
                            if (sourceName?.trim().equals("github")) {
                                deployGit(gbAuthKey.toString(), branch.toString(), repoName.toString(), appName.toString(), herokuAuthKey.toString(), repoUser.toString())
                            } else if (sourceName?.trim().equals("bitbucket")) {
                                bitRefreshKey = intent.getStringExtra("bitRefreshKey")
                                println("deploy bitbucket method called $bitRefreshKey")
                                deployBitbucket(gbAuthKey.toString(), branch.toString(), repoName.toString(), appName.toString(), herokuAuthKey.toString(), repoUser.toString(), bitRefreshKey.toString())
                            }
                        })

                        t.start()

                    }, 515)
                }
            } else {
                runOnUiThread {
                println("Error In  BuildCreate Response")
                val deployError = Snackbar.make(tvStatusBuild, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                val view = deployError.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view.layoutParams = params
                view.setBackgroundColor(Color.BLACK)
                val deployErrormain = deployError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                deployErrormain?.typeface = fontUtility?.regular
                deployError.show()
                    tv_timer.stop()
                    timeTaken(tv_timer.text.toString())
                    progressLoader1.visibility=View.GONE
                    rlcontaincer1.setBackgroundResource(R.color.red)
                    img_arrow1.visibility=View.VISIBLE
                    img_arrow1.setImageResource(R.drawable.animated_vector_cross)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("source",sourceName)
                    startActivity(intent)
                    (img_arrow1!!.getDrawable() as Animatable).start()
                    btn_dashboard.isEnabled=true
                    }
            }
        } catch (json: Exception) {
            runOnUiThread {
            println("Error in BuildCreate json exception")
            val deployError = Snackbar.make(tvStatusBuild, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = deployError.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.setBackgroundColor(Color.BLACK)
            val deployErrormain = deployError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            deployErrormain?.typeface = fontUtility?.regular
            deployError.show()
                tv_timer.stop()
                timeTaken(tv_timer.text.toString())
                progressLoader.visibility=View.GONE
                rlcontaincer.setBackgroundResource(R.color.red)
                img_arrow.visibility=View.VISIBLE
                img_arrow.setImageResource(R.drawable.animated_vector_cross)
                (img_arrow!!.getDrawable() as Animatable).start()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("source",sourceName)
                startActivity(intent)
                btn_dashboard.isEnabled=true
               }
        }


    }

    private fun updateDashboardBuild(totalsec: Int) {

        println("update buid user id " + userId + " buidId " + id + " projectId " + projectId + " buidstream " + output_stream_url + " buidTime " + totalsec.toString() + " buidStatus " + buildStatus + " source id " + sourceId)
        val okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .readTimeout(60, TimeUnit.MINUTES)
                .build()


        println("DashboardURL==> $gbAuthKey $branch $repoName $appName $herokuAuthKey $repoUser project $projectId build $buildHistoryId userid $userId Heroo $id")
        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                //Eg:ormbot-engine.herokuapp.com  https://justdeploy-engine.herokuapp.com/google-login
                .host(Constants.JDHOST)
                .addPathSegment("users")
                .addPathSegment("build")
                .addPathSegment("updateBuild")
                .build()


                if (id.toString().equals("null"))
                {
                    Log.d("buildId"," build id null part running")
                    val form = FormBody.Builder()
                    .add("userId", userId)
                        .add("buildHistoryId", buildHistoryId)
                        .add("projectId", projectId)
                        .add("buildTime", totalsec.toString())
                        .add("buildStatus", buildStatus)
                        .add("sourceId", sourceId)
                        .build()
                    try {
                        updateBuildResult(doSyncPost(okHttpClient, httpUrl, form))
                    } catch (e: IOException) {
                        runOnUiThread {
                        e.printStackTrace()
                        Log.i("Error1==>", e.toString())
                        val dashBoardError = Snackbar.make(tvStatusBuild, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                        val view = dashBoardError.view
                        val params = view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.TOP
                        view.layoutParams = params
                        view.setBackgroundColor(Color.BLACK)
                        val dashBoardErrormain = dashBoardError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                        dashBoardErrormain?.typeface = fontUtility?.regular
                        dashBoardError.show()
                            /*progressLoader1.visibility=View.GONE
                            rlcontaincer1.setBackgroundResource(R.color.red)
                            img_arrow1.visibility=View.VISIBLE
                            img_arrow1.setImageResource(R.drawable.animated_vector_cross)
                            (img_arrow1!!.getDrawable() as Animatable).start()*/
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("source",sourceName)
                            startActivity(intent)
                            btn_dashboard.isEnabled=true
                            }
                    }
                }
        else
                {
                    Log.d("buildId"," build id not null part running")
                    val form = FormBody.Builder()
                            .add("userId", userId)
                            .add("buildHistoryId", buildHistoryId)
                            .add("projectId", projectId)
                            .add("buildHerokuId", id)
                            .add("buildStreamUrl", output_stream_url)
                            .add("buildTime", totalsec.toString())
                            .add("buildStatus", buildStatus)
                            .add("sourceId", sourceId)
                            .build()
                    try {
                        updateBuildResult(doSyncPost(okHttpClient, httpUrl, form))
                    } catch (e: IOException) {
                        e.printStackTrace()
                        runOnUiThread {
                        Log.i("Error1==>", e.toString())
                        val dashBoardError = Snackbar.make(tvStatusBuild, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                        val view = dashBoardError.view
                        val params = view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.TOP
                        view.layoutParams = params
                        view.setBackgroundColor(Color.BLACK)
                        val dashBoardErrormain = dashBoardError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                        dashBoardErrormain?.typeface = fontUtility?.regular
                        dashBoardError.show()
                          /*  progressLoader1.visibility=View.GONE
                            rlcontaincer1.setBackgroundResource(R.color.red)
                            img_arrow1.visibility=View.VISIBLE
                            img_arrow1.setImageResource(R.drawable.animated_vector_cross)
                            (img_arrow1!!.getDrawable() as Animatable).start()*/
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("source",sourceName)
                            startActivity(intent)
                            btn_dashboard.isEnabled=true
                            }
                    }
                }
            }

    fun updateBuildResult(myResponse: String) {
        println("responce in build send   $myResponse")
        val json = JSONObject(myResponse)
        val status = json.getString("status")
        if (status.trim().equals("200")) {
            dialog?.dismiss()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("source",sourceName)
            startActivity(intent)
        } else {
            dialog?.dismiss()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("source",sourceName)
            startActivity(intent)
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
