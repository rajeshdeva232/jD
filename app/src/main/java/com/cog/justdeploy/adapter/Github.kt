package com.cog.justdeploy.adapter

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.StrictMode
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.cog.justdeploy.R
import com.cog.justdeploy.activity.BuildLogActivity
import com.cog.justdeploy.model.GithubRepo
import com.cog.justdeploy.utils.Constants
import com.cog.justdeploy.utils.FontUtility
import com.facebook.stetho.okhttp3.StethoInterceptor
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


/**
 * Created by Rajesh on 10/5/18.
 */
open class Github(private val githubRepoList: ArrayList<GithubRepo>, applicationContext: Context) : RecyclerView.Adapter<Github.ViewHolder>() {

    private var context: Context = applicationContext
    private var fontUtility: FontUtility? = FontUtility(applicationContext)
    var sharedprf: SharedPreferences? = null
    var mongoId: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Github.ViewHolder {
        val vView = LayoutInflater.from(parent.context).inflate(R.layout.platformcard, parent, false)
        return Github.ViewHolder(vView, context)
    }


    override fun getItemCount(): Int {
        return githubRepoList.size
    }

    override fun onBindViewHolder(holder: Github.ViewHolder, position: Int) {
        holder.bindItems(githubRepoList[position])
        setFontStyle(holder)
        sharedprf = PreferenceManager.getDefaultSharedPreferences(context)
        mongoId = sharedprf?.getString("mongoId", "")
        Log.i("Bitcountttt==>", githubRepoList.size.toString())

        holder.deploy.setOnClickListener({

            val dialogBuilder = AlertDialog.Builder(context, R.style.CustomDialog)
            val inflater = LayoutInflater.from(context)
            val updateDialog = inflater.inflate(R.layout.dialog_deploy, null)
            dialogBuilder.setView(updateDialog)
            val updateDialogDb = dialogBuilder.create()
            updateDialogDb.setCancelable(false)
            updateDialogDb.show()
            val okDb = updateDialog.findViewById<TextView>(R.id.ok)
            val cancelDesc = updateDialog.findViewById<TextView>(R.id.cancel_desc)

            okDb.setOnClickListener {
                val dialog = ProgressDialog.show(context, "dotPb title",
                        "dotPb message", false)
                dialog.setContentView(R.layout.layout_loading_dot)
                dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                if (updateDialogDb.isShowing)
                {
                    updateDialogDb.dismiss()
                }
                dialog.show()
                val t = Thread(Runnable {
                    deployUrl(githubRepoList[position].getGitcardId,"0", mongoId.toString(),dialog,holder.lastdate)
                })
                t.start()
                /*Handler().postDelayed({
                    deployUrl(githubRepoList[position].getGitcardId,"0", mongoId.toString(),dialog)
                }, 500)*/
            }

            cancelDesc.setOnClickListener { updateDialogDb.dismiss() }

        })
        holder.delete.setOnClickListener({

            val dialogBuilder = AlertDialog.Builder(context, R.style.CustomDialog)
            val inflater = LayoutInflater.from(context)
            val updateDialog = inflater.inflate(R.layout.dialog_delete, null)
            dialogBuilder.setView(updateDialog)
            val updateDialogDb = dialogBuilder.create()
            updateDialogDb.setCancelable(false)
            updateDialogDb.show()
            val okDb = updateDialog.findViewById<TextView>(R.id.ok)
            val cancelDesc = updateDialog.findViewById<TextView>(R.id.cancel_desc)

            okDb.setOnClickListener {
                val dialog = ProgressDialog.show(context, "dotPb title",
                        "dotPb message", false)
                dialog.setContentView(R.layout.layout_loading_dot)
                dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
                val gitcardIdPos = githubRepoList[position].getGitcardId
                println("GITCardID==>$gitcardIdPos")

                val gitUrld = Constants.JDMAINURL + "/users/github/$gitcardIdPos/delete"
                val request = Request.Builder().url(gitUrld).build()
                val bitClientd = OkHttpClient()
                bitClientd.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        println("Error")
                        BitBucket.deploystatus = false
                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        val body = response?.body()?.string()
                        println(body)
                        Log.i("GitDelete==>", response.toString())
                        try{
                        val json = JSONObject(body)
                        val status = json.getString("status")
                        if (status.equals("200")) {
                            Github.deploystatus = true
                            println("gitDelete==>in${Github.deploystatus}")

                            launch(UI) {
                                githubRepoList.removeAt(position)
                                //print("click the item menu "+position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, githubRepoList.size)
                                dialog.dismiss()
                            }
                        } else {
                            println("Error In  GIT Delete Response")
                            val deleteError = Snackbar.make(holder.delete, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                            val view = deleteError.view
                            val params = view.layoutParams as FrameLayout.LayoutParams
                            params.gravity = Gravity.TOP
                            view.layoutParams = params
                            view.setBackgroundColor(Color.BLACK)
                            val deleteErrormain = deleteError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                            deleteErrormain?.typeface = fontUtility?.regular
                            dialog.dismiss()
                            deleteError.show()
                        }}catch (jsonError: JSONException) {
                            jsonError.printStackTrace()
                            val deleteJson = Snackbar.make(holder.delete, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                            val view = deleteJson.view
                            val params = view.layoutParams as FrameLayout.LayoutParams
                            params.gravity = Gravity.TOP
                            view.layoutParams = params
                            view.setBackgroundColor(Color.BLACK)
                            val deleteJsonmain = deleteJson.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                            deleteJsonmain?.typeface = fontUtility?.regular
                            dialog.dismiss()
                            deleteJson.show()
                        }
                    }


                })



                updateDialogDb.dismiss()
            }

            cancelDesc.setOnClickListener { updateDialogDb.dismiss() }

        })
    }

    private fun setFontStyle(holder: Github.ViewHolder) {

        //Add Font
        holder.status.typeface = fontUtility?.regular
        holder.projectName.typeface = fontUtility?.regular
        holder.time1.typeface = fontUtility?.medium
        holder.deploy.typeface = fontUtility?.regular
        holder.log.typeface = fontUtility?.regular
        holder.lastdate.typeface = fontUtility?.regular
        holder.tvTimeago.typeface = fontUtility?.medium

    }

    /**
     * Created by Rajesh on 17/04/2018
     */
    class ViewHolder(itemView: View, applicationContext: Context) : RecyclerView.ViewHolder(itemView) {

        internal var status: TextView = itemView.findViewById<TextView>(R.id.tvStatus)
        internal var viewBorder: View = itemView.findViewById<View>(R.id.viewBorder)
        internal var projectName: TextView = itemView.findViewById<TextView>(R.id.tvProjectname)
        internal var time1: TextView = itemView.findViewById<TextView>(R.id.tvTime)
        internal var delete: ImageView = itemView.findViewById<ImageView>(R.id.ivDelete)
        internal var deploy: Button = itemView.findViewById<Button>(R.id.btnDeply)
        internal var log: TextView = itemView.findViewById<TextView>(R.id.tvLog)
        internal var lastdate: TextView = itemView.findViewById<TextView>(R.id.tvLabletime)
        internal var tvTimeago: TextView = itemView.findViewById<TextView>(R.id.tvTimeago)

        /**
         * function
         */
        fun bindItems(user: GithubRepo) {


            if (user.getGitstatus == "3") {
                status.text = "Failed"
                viewBorder.setBackgroundResource(R.color.red)
                status.setBackgroundResource(R.color.red)
                status.setTextColor(Color.parseColor("#ffffff"))
                projectName.setTextColor(Color.parseColor("#FF0000"))
            } else if (user.getGitstatus == "0") {
                status.text = "Ready"
                viewBorder.setBackgroundResource(R.color.colorPrimary)
                status.setBackgroundResource(R.color.colorPrimary)
                status.setTextColor(Color.parseColor("#ffffff"))
                projectName.setTextColor(Color.parseColor("#f85729"))
            } else if (user.getGitstatus == "1") {

                status.text = "Success"
                viewBorder.setBackgroundResource(R.color.green)
                status.setBackgroundResource(R.color.green)
                status.setTextColor(Color.parseColor("#ffffff"))
                projectName.setTextColor(Color.parseColor("#377303"))
            } else if (user.getGitstatus == "2") {
                status.text = "Success"
                viewBorder.setBackgroundResource(R.color.green)
                status.setBackgroundResource(R.color.green)
                status.setTextColor(Color.parseColor("#ffffff"))
                projectName.setTextColor(Color.parseColor("#377303"))

            }
            projectName.text = user.getGitrepo
            time1.text = user.getGitupdate

        }
    }

    /**
     * PostURL Mainfunction
     */
    fun deployUrl(projectId: String, sourceId: String, userId: String, dialog:Dialog,last:TextView) {

        println("projectId $projectId $sourceId  $userId")
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
            updateResult(doSyncPost(okHttpClient, httpUrl, form),dialog,last)

        }catch (e: IOException) {
            e.printStackTrace()
            Log.i("GitAdapterIoError==>", e.toString())
            val deployError = Snackbar.make(last, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = deployError.view
             val params = view.layoutParams as FrameLayout.LayoutParams
             params.gravity = Gravity.TOP
             view.layoutParams = params
             view.setBackgroundColor(Color.BLACK)
            val deployErrormain = deployError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            deployErrormain?.typeface = fontUtility?.regular
            deployError.show()
            dialog.dismiss()
        }

    }

    public fun updateResult(myResponse: String,dialog: Dialog,last: TextView) {
        Log.i("Deploygitstatus==>", myResponse)
        try {
            val json = JSONObject(myResponse)
            println("Deploygitstatus ==> ${json.getString("status")}")
            val status = json.getString("status")
            if (status.equals("200")) {
                val data = json.getJSONArray("data")
                val dataObj = data.getJSONObject(0)
                val appName = dataObj.getString("appName")
                val noOfDeployes = dataObj.getString("noOfDeploy")
                val gbAuthKey = dataObj.getString("gitAuthKey")
                val branch =dataObj.getString("repoBranchName")
                val repoName = dataObj.getString("repoName")
                val herokuAuthKey = dataObj.getString("herokuAuthKey")
                val repoUser = dataObj.getString("repoUser")
                val projectCardId = dataObj.getString("_id")
                val userId = dataObj.getString("userId")
                val buildHistoryId = json.getString("buildHistoryId")
                println("Extra Detaiils $gbAuthKey $branch $repoName $herokuAuthKey $repoUser$appName$buildHistoryId$projectCardId")
                val intent = Intent(context, BuildLogActivity::class.java)
                intent.putExtra("appName", appName)
                intent.putExtra("projectId",projectCardId )
                intent.putExtra("userId",userId )
                intent.putExtra("buildHistoryId", buildHistoryId)
                intent.putExtra("noOfDeployes", noOfDeployes)
                intent.putExtra("gbAuthKey", gbAuthKey)
                intent.putExtra("branch", branch)
                intent.putExtra("repoName", repoName)
                intent.putExtra("herokuAuthKey", herokuAuthKey)
                intent.putExtra("repoUser", repoUser)
                intent.putExtra("sourceName", "github")
                intent.putExtra("sourceId", "0")
                if (dialog.isShowing)
                {
                    dialog.dismiss()
                }
                context.startActivity(intent)
            }
             else
            {
                if (dialog.isShowing)
                {
                    dialog.dismiss()
                }
            }
             }catch (jsonError: JSONException) {
            jsonError.printStackTrace()
            val deployError = Snackbar.make(last, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = deployError.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.setBackgroundColor(Color.BLACK)
            val deployErrormain = deployError.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            deployErrormain?.typeface = fontUtility?.regular
            deployError.show()
            dialog.dismiss()
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
                //.method("POST", body) already done in post()
                .post(body)
                .build()
//        if (android.os.Build.VERSION.SDK_INT > 9) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
//        }
        val response = client.newCall(request).execute()
        return response.body()?.string().toString()
    }


    companion object {
        var deploystatus: Boolean? = false

    }

}
