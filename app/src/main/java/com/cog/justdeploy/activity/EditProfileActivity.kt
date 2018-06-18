package com.cog.justdeploy.activity

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.os.StrictMode
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.cog.justdeploy.R
import com.cog.justdeploy.utils.*
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_editprofile.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.regex.Pattern

/**
 * Created by Rajesh on 18/5/18.
 */
open class EditProfileActivity : BaseActivity() {
    private var fontUtility: FontUtility? = null
    var dialog: Dialog? = null
    var firstName: String? = null
    var lastName: String? = null
    var emailId: String? = null
    var image: String? = null
    var imageNew: String? = null
    private var imageUri: Uri? = null
    private var firebaseUri: Uri? = null
    private var contentValues: ContentValues? = null
    private var cameraPath: Intent? = null
    val mCameraRequestCode = 0
    private var fileUri: Uri? = null
    private var editImageUri: Uri? = null
    internal lateinit var storage: FirebaseStorage
    internal lateinit var storageReference: StorageReference
    internal var firstNameLabel: TextInputLayout? = null
    internal var lastNameLabel: TextInputLayout? = null
    var sharedprf: SharedPreferences? = null
    var mongoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)
        fontUtility = FontUtility(this)
        storage = FirebaseStorage.getInstance()
        sharedprf = PreferenceManager.getDefaultSharedPreferences(this)
        mongoId = sharedprf?.getString("mongoId","")
        storageReference = storage.reference
        dialog = ProgressDialog.show(this, "dialog title",
                "dialog message", false)
        dialog?.setContentView(R.layout.layout_loading_dot)
        dialog?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        etFirstName.typeface = fontUtility?.regular
        etLastName.typeface = fontUtility?.regular
        etEmail.typeface = fontUtility?.regular
        tvUserName.typeface = fontUtility?.regular
        tvEdit.typeface = fontUtility?.regular
        firstNameLabel = findViewById(R.id.tvFirstNameLabel)
        lastNameLabel = findViewById(R.id.tvLastNameLabel)
        val font: Typeface = Typeface.createFromAsset(assets, "fonts/Roboto-Regular.ttf")
        tvFirstNameLabel.setTypeface(font)
        tvLastNameLabel.setTypeface(font)
        tvEmailId.setTypeface(font)
        tvEmailId.isEnabled = false
        peformClick()
        profile()
    }

    companion object {
        private val mPickImageRequest = 234
    }

    private fun peformClick() {

        etFirstName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                tvFirstNameLabel.isErrorEnabled = false
                tvLastNameLabel.isErrorEnabled = false
                tvEmailId.isErrorEnabled = false
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        etLastName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                tvFirstNameLabel.isErrorEnabled = false
                tvLastNameLabel.isErrorEnabled = false
                tvEmailId.isErrorEnabled = false
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                tvFirstNameLabel.isErrorEnabled = false
                tvLastNameLabel.isErrorEnabled = false
                tvEmailId.isErrorEnabled = false
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        ivBackArrow.setOnClickListener {
            intent = Intent(this, ViewprofileActivity::class.java)
            startActivity(intent)
        }
        profile.setOnClickListener {
            showDialog()
        }
        btnSave.setOnClickListener {

            val user = FirebaseAuth.getInstance().currentUser
            btnSave.setBackgroundResource(R.drawable.shade)
            btnSave.isEnabled = false
            firstName = etFirstName.text.toString()
            lastName = etLastName.text.toString()
            emailId = etEmail.text.toString()

            if (!validateFirstName(firstName.toString())) {
                btnSave.setBackgroundResource(R.drawable.buttonbackroundgreen)
                btnSave.isEnabled = true

                if (!validateLastName(lastName.toString())) {
                    Log.d("Validation", "Enter valid Last Name")
                }
            } else if (!validateLastName(lastName.toString())) {
                btnSave.setBackgroundResource(R.drawable.buttonbackroundgreen)
                btnSave.isEnabled = true
                if (!validateFirstName(firstName.toString())) {
                    Log.d("Validation", "Enter valid Last Name")
                }
            } else {

                if (editImageUri.toString().trim().equals("null")) {
                    val image = image
                    editProfile(mongoId.toString(), emailId.toString(), firstName.toString(), lastName.toString(), image.toString())
                } else {

                    val userId1: String = user?.uid.toString()
                    val storageRef = storage.reference
                    storageRef.child("profileImage").child(userId1).downloadUrl
                            .addOnSuccessListener { uri ->
                                Log.d("responce", "failure method called $uri")
                                firebaseUri = uri
                                editProfile(mongoId.toString(), emailId.toString(), firstName.toString(), lastName.toString(), firebaseUri.toString())
                                val sharedprf = PreferenceManager.getDefaultSharedPreferences(this)
                                sharedprf?.edit()?.putString("imageUrl", firebaseUri.toString())?.apply()

                            }
                            .addOnFailureListener { Log.e("responce", "failure method called") }
                }
            }
        }
    }

    private fun uploadFirebase(editImageUri: Uri?) {
        runOnUiThread {
            val user = FirebaseAuth.getInstance().currentUser
            val userId = user?.uid
            val ref = storageReference.child("profileImage").child(userId!!)
            if (editImageUri != null) {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading...")
                progressDialog.show()
                progressDialog.setCancelable(false)
                ref.putFile(editImageUri)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Log.d("EditProfile", "Success")
                        }
                        .addOnFailureListener { error ->
                            progressDialog.dismiss()
                            Log.d("EditProfile", error.toString())

                        }
                        .addOnProgressListener { taskSnapshot ->
                            val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                                    .totalByteCount
                            progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
                        }
            }
        }
    }

    /**
     * function
     */
    fun editProfile(fuserId: String, fEmail: String, fFirstname: String, fLastname: String, fProfileImg: String) {
        val okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .build()
        val fUsername = fFirstname + " " + fLastname
        println("URLEditProfile==> $fuserId $fEmail $fFirstname $fLastname  $fProfileImg")

        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                //Eg:ormbot-engine.herokuapp.com  https://justdeploy-engine.herokuapp.com/google-login
                .host(Constants.JDHOST)
                .addPathSegment("users")
                .addPathSegment("profile")
                .addPathSegment("update")
                .build()
//dNVXc4jpA0doBp8QBurdL3MDkEC3
        val form = FormBody.Builder()
                .add("email", fEmail)
                .add("userId", fuserId)
                .add("firstName", fFirstname)
                .add("lastName", fLastname)
                .add("profileImage", fProfileImg)
                .add("phonenumber", "9876543210")
                .add("userName", fUsername)
                .build()


        try {
            updateResult(doSyncPost(okHttpClient, httpUrl, form))

        } catch (exception: IOException) {
            exception.printStackTrace()
            Log.i("Error==>", exception.toString())
            val editFailure = Snackbar.make(etEmail, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = editFailure.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.setBackgroundColor(Color.BLACK)
            val editFailmain = editFailure.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            editFailmain?.typeface = fontUtility?.regular
            dialog?.dismiss()
            editFailure.show()
        }
        catch (JSONexception:JSONException)
        {
            JSONexception.printStackTrace()
            Log.i("Error==>", JSONexception.toString())
            val editFailure = Snackbar.make(etEmail, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = editFailure.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.setBackgroundColor(Color.BLACK)
            val editFailmain = editFailure.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            editFailmain?.typeface = fontUtility?.regular
            dialog?.dismiss()
            editFailure.show()
        }
        catch (exception:Exception)
        {
            exception.printStackTrace()
            Log.i("Error==>", exception.toString())
            val editFailure = Snackbar.make(etEmail, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = editFailure.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.setBackgroundColor(Color.BLACK)
            val editFailmain = editFailure.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            editFailmain?.typeface = fontUtility?.regular
            dialog?.dismiss()
            editFailure.show()
        }

    }

    public fun updateResult(myResponse: String) {
        val msg = Message.obtain()
        msg.obj = myResponse
        Log.i("Editprofile==>", myResponse.toString())
        dialog?.dismiss()
        try {
            val json = JSONObject(myResponse)
            val status = json.getString("status")
            if (status.equals("200")) {
                runOnUiThread { var firstName = etFirstName.text.toString()
                    println("first Name in edit profile $firstName")
                    sharedprf?.edit()?.putString("firstName",firstName)?.apply()}
                intent = Intent(this, ViewprofileActivity::class.java)
                startActivity(intent)
            } else {
                Log.d("Editprofile==>", "Not Succeeded")
            }
        } catch (jsonError: JSONException) {
            jsonError.printStackTrace()
            val editProfileJson = Snackbar.make(etEmail, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
            val view = editProfileJson.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            view.setBackgroundColor(Color.BLACK)
            val editProfilemain = editProfileJson.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
            editProfilemain?.typeface = fontUtility?.regular
            dialog?.dismiss()
            editProfileJson.show()
        }
    }


    private fun validateEmail(): Boolean {
        val email = etEmail.text.toString()
        if (email.equals("")) {
            tvEmailId.isErrorEnabled = true
            tvEmailId.setError("Enter Email Here")
            requestFocus(tvLastNameLabel)
            return false
        } else {
            tvEmailId.isErrorEnabled = false
        }
        return true
    }

    /**
     * function
     */
    fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    /**
     * function
     */
    fun validateLastName(lastName: String): Boolean {
        if (lastName.equals("")) {
            lastNameLabel?.isErrorEnabled = true
            lastNameLabel?.setError("Enter  Last Name Here")
            lastNameLabel?.let { requestFocus(it) }
            return false
        } else {
            lastNameLabel?.isErrorEnabled = false
        }
        return true
    }

    /**
     * function
     */
    fun requestFocus(view: View) {

        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }

    }

    /**
     * function
     */
    fun validateFirstName(firstName: String): Boolean {

        if (firstName.equals("")) {
            firstNameLabel?.isErrorEnabled = true
            firstNameLabel?.setError("Enter First Name Here")
            firstNameLabel?.let { requestFocus(it) }
            return false
        } else {
            firstNameLabel?.isErrorEnabled = false
            return true
        }
    }

    private fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val message = "Select any option"
        val spannableString = SpannableString(message)
        val typefaceSpan = TypefaceUtils.load(assets, "fonts/Roboto-Regular.ttf")?.let { CalligraphyTypefaceSpan(it) }
        spannableString.setSpan(typefaceSpan, 0, message.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        dialogBuilder.setMessage(spannableString)
        dialogBuilder.setPositiveButton("Cancel", { dialog, whichButton ->
            dialog.dismiss()

        })
        dialogBuilder.setNegativeButton("Camera", { dialog, whichButton ->
            try {
                contentValues = ContentValues()
                contentValues?.put(MediaStore.Images.Media.TITLE, "Image File name")
                imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                cameraPath = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                cameraPath?.putExtra("android.intent.extras.CAMERA_FACING", 1)
                cameraPath?.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(cameraPath, mCameraRequestCode)
            } catch (e: IOException) {
                Toast.makeText(this, "Could not create file!", Toast.LENGTH_SHORT).show()
            }
        })
        dialogBuilder.setNeutralButton("Gallery", { dialog, whichButton ->

            imageChoose()

        })
        val selectDialog = dialogBuilder.create()
        selectDialog.show()
    }

    /**
     * function
     */
    fun imageChoose() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), EditProfileActivity.mPickImageRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            mPickImageRequest -> {
                fileUri = data?.data
                println("capture from camera 2 $fileUri")
                editImageUri = fileUri
                imageNew = fileUri.toString()
                uploadFirebase(editImageUri)
                if (fileUri!=null) {
                    Glide.with(this).load(imageNew).into(cimgUserprofile)
                }
                else
                {
                    Glide.with(this).load(image).into(cimgUserprofile)
                }
            }
            mCameraRequestCode -> {
                if (resultCode == Activity.RESULT_OK) {
                    editImageUri = imageUri
                    uploadFirebase(editImageUri)
                    Glide.with(this).load(imageUri).into(cimgUserprofile)
                    //   cimgUserprofile.setImageBitmap(setScaledBitmap())
                }
            }
        }
    }

    private fun profile() {
        dialog?.show()
        val gitUrld = Constants.JDMAINURL + "/users/profile/$mongoId"
        val request = Request.Builder().url(gitUrld).build()
        val gitClientd = OkHttpClient()
        gitClientd.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.d("Info==>", "profile failure ")
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
                        runOnUiThread(Runnable {
                            etFirstName.setText(firstName)
                            firstName?.length?.let { etFirstName.setSelection(it) }
                            etLastName.setText(lastName)
                            lastName?.length?.let { etLastName.setSelection(it) }
                            etEmail.setText(emailId)
                            emailId?.length?.let { etEmail.setSelection(it) }
                            tvUserName.text = firstName + " " + lastName
                            civCameraIcon.visibility = View.VISIBLE
                            Glide.with(applicationContext).load(image).skipMemoryCache(true).centerCrop().error(R.drawable.whiteusershape).into(cimgUserprofile)
                            dialog?.dismiss()
                        })
                    } else {
                        Log.d("Profile==>", " Not Succeeded")
                    }
                } catch (jsonError: JSONException) {
                    jsonError.printStackTrace()
                    val profileJson = Snackbar.make(etEmail, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                    val view = profileJson.view
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.TOP
                    view.layoutParams = params
                    view.setBackgroundColor(Color.BLACK)
                    val profileJsonmain = profileJson.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                    profileJsonmain?.typeface = fontUtility?.regular
                    dialog?.dismiss()
                    profileJson.show()
                }
                catch (exception:Exception)
                {
                    exception.printStackTrace()
                    val profileJson = Snackbar.make(etEmail, "Oops! Something went Wrong", Snackbar.LENGTH_LONG)
                    val view = profileJson.view
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.TOP
                    view.layoutParams = params
                    view.setBackgroundColor(Color.BLACK)
                    val profileJsonmain = profileJson.view.findViewById<View>(android.support.design.R.id.snackbar_text) as? TextView
                    profileJsonmain?.typeface = fontUtility?.regular
                    dialog?.dismiss()
                    profileJson.show()

                }

            }
        })

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

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }
}
