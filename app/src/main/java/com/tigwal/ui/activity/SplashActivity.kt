package com.tigwal.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.tigwal.BuildConfig
import com.tigwal.R
import com.tigwal.app.rest.RestConstant
import com.tigwal.base.BaseActivity
import com.tigwal.data.api.Resource
import com.tigwal.data.api.UserParams
import com.tigwal.databinding.ActivitySplashBinding
import com.tigwal.ui.factory.SplashFactory
import com.tigwal.ui.view_model.splash.SplashViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.MySharedPreferences
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.TimeZone


class SplashActivity : BaseActivity(), KodeinAware {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var splashviewmodel: SplashViewModel
    private val factory: SplashFactory by instance()
    override val kodein: Kodein by kodein()
    private var handler: Handler? = null
    var errorString = MutableLiveData<String>()
    var deviceUDID: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_splash)
        deviceUDID =
            Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID)
        MySharedPreferences.getMySharedPreferences()?.deviceUDID = deviceUDID
        hashkey(activity)
        deviceToken()
        getIntentData()
        clickListener()
        setupViewModel()
        setupObservers()
        setFontTypeface()

        if(MySharedPreferences.getMySharedPreferences()!!.language == "ar"){
            MySharedPreferences.getMySharedPreferences()!!.language = "ar"
            AppUtils.languageSelection(activity, "ar")
            setLangauge()
        }
        else if(MySharedPreferences.getMySharedPreferences()!!.language == "en"){
            MySharedPreferences.getMySharedPreferences()!!.language = "en"
            AppUtils.languageSelection(activity, "en")
            setLangauge()
        }

        if (Connectivity.isConnected(activity)) {
            val params = HashMap<String, String?>()
            params[UserParams.app_name] = resources.getString(R.string.app_name)
            params[UserParams.app_version] = BuildConfig.VERSION_NAME
            params[UserParams.device_token] =
                MySharedPreferences.getMySharedPreferences()!!.deviceToken
            params[UserParams.device_type] = RestConstant.DEVICE_TYPE
            params[UserParams.fcm_token] = "" //As blank
            params[UserParams.apns_token] = "" //As blank
            params[UserParams.app_identifier] = deviceUDID
            params[UserParams.timezone] = TimeZone.getDefault().id.toString()  // Local.timezone
            params[UserParams.user_id] = MySharedPreferences.getMySharedPreferences()!!.userId
            splashviewmodel.appInstallationApi(params)
            try {
                val versionName = packageManager.getPackageInfo(packageName, 0).versionName
                binding.txtAppVersion.setText(resources.getString(R.string.app_version) + " " + versionName)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(
                activity,
                "" + resources.getString(R.string.noInternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //set view model
    private fun setupViewModel() {
        splashviewmodel = ViewModelProvider(this, factory).get(SplashViewModel::class.java)
        binding.viewmodel = splashviewmodel
    }



    //set observer
    private fun setupObservers() {
        splashviewmodel.appInstallationApiResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d(
                            "Response",
                            "====== appinstallationResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { appinstallationResponse ->
                            if (appinstallationResponse.status == true) {
                                init()
                            }
                        }
                    }
                    is Resource.Error -> {
                        hideProgressDialog()
                        init()
                    }
                    is Resource.Loading -> {
                        showProgressDialog(activity)
                    }
                }
            })
    }

    private fun init() {
        MySharedPreferences.getMySharedPreferences()!!.deviceId = AppUtils.getDeviceId(this)
        handler = Handler(Looper.getMainLooper())
        handler!!.postDelayed({
            if (MySharedPreferences.getMySharedPreferences()!!.isLogin) {
                startActivity(
                    Intent(
                        this,
                        DashboardActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                AppUtils.startFromRightToLeft(activity)
                finish()
            } else {
                startActivity(Intent(this@SplashActivity, LanguageActivity::class.java))
                AppUtils.startFromRightToLeft(activity)
                finish()
            }
        }, 3000)
    }

    private fun deviceToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            if (instanceIdResult.token != null && instanceIdResult.token != "") {
                MySharedPreferences.getMySharedPreferences()!!.deviceToken = instanceIdResult.token
                Log.e("TAG", "deviceToken: " + instanceIdResult.token)
            } else {
                MySharedPreferences.getMySharedPreferences()!!.deviceToken = "dummy_token"
            }
        }
    }

    override fun getIntentData() {

    }

    override fun clickListener() {

    }

    override fun setFontTypeface() {
        binding.txtAppVersion.typeface = AppUtils.getMIDIUM(activity)
    }

    fun hashkey(activity: Activity) {
        try {
            val info: PackageInfo = activity.getPackageManager().getPackageInfo(
                activity.getPackageName(), PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                    Log.d(
                        "Keyhash:", Base64.encodeToString(md.digest(), Base64.DEFAULT)
                    )
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
    }
}