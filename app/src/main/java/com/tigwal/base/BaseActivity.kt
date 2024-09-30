package com.tigwal.base

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tigwal.R
import com.tigwal.data.model.login.LoginResponse
import com.tigwal.ui.dialog.CustomProgressDialog
import com.tigwal.utils.AppUtils
import com.tigwal.utils.MySharedPreferences
import java.util.*

abstract class


BaseActivity : AppCompatActivity() {

    private var customProgressDialog: CustomProgressDialog? = null

    fun showProgressDialog(ctx: Context) {
        try {
            customProgressDialog = CustomProgressDialog(ctx)
            customProgressDialog!!.setCancelable(false)
            customProgressDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setLoginUserData(data: LoginResponse.Data?) {
        MySharedPreferences.getMySharedPreferences()?.userId =
            data?.userId?.toString()
        MySharedPreferences.getMySharedPreferences()?.userName =
            data?.name
        MySharedPreferences.getMySharedPreferences()?.email =
            data?.email
        MySharedPreferences.getMySharedPreferences()?.phoneNumber =
            data!!.mobileNo
        MySharedPreferences.getMySharedPreferences()?.userImage =
            data?.imageurl
        MySharedPreferences.getMySharedPreferences()?.countrycode =
            data?.countryCode
        MySharedPreferences.getMySharedPreferences()!!.isNotification = data?.enable_notification
        MySharedPreferences.getMySharedPreferences()?.userType =
            data?.userType
        MySharedPreferences.getMySharedPreferences()?.loginType =
            data?.loginType
        MySharedPreferences.getMySharedPreferences()?.authToken =
            data?.token
    }

    fun hideProgressDialog() {
        try {
            if (customProgressDialog != null && customProgressDialog!!.isShowing) {
                customProgressDialog!!.dismiss()
                customProgressDialog = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setLangauge()
    }

    override fun onResume() {
        super.onResume()
        setLangauge()

    }


    open fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    val activity: Activity
        get() = this


    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.finishFromLeftToRight(activity!!)
    }

    fun pushFragment(fragment: Fragment?, title: String?) {
        if (fragment == null) return
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, fragment)
            .addToBackStack(null)
            .commit()
    }

    abstract fun getIntentData()
    abstract fun clickListener()
    abstract fun setFontTypeface()

    open fun setLangauge() {
        if (MySharedPreferences.getMySharedPreferences()!!.language.equals("en")
        ) {
            val languageToLoad = "en" // your language
            val locale = Locale(languageToLoad)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            baseContext.resources.updateConfiguration(
                config,
                baseContext.resources.displayMetrics
            )
        } else if (MySharedPreferences.getMySharedPreferences()!!.language.equals("ar")
        ) {
            val languageToLoad = "ar" // your language
            val locale = Locale(languageToLoad)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            baseContext.resources.updateConfiguration(
                config,
                baseContext.resources.displayMetrics
            )
        }
    }
}