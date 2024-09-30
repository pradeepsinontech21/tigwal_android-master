package com.tigwal.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.tigwal.R
import com.tigwal.ui.dialog.CustomProgressDialog
import com.tigwal.utils.MySharedPreferences
import java.util.*

abstract class BaseFragment : Fragment() {
    private var customProgressDialog: CustomProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = onCreateFragmentView(inflater, container, savedInstanceState);

        getIntentData()
        clickListener()
        setFontTypeface()
        setLangauge()
        return view;
    }

    override fun onResume() {
        super.onResume()
        setLangauge()

    }

    abstract fun onCreateFragmentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("TAG", "onActivityResult: Fragmentttt")
    }

    fun showProgressDialog(ctx: Context) {
        try {
            customProgressDialog = CustomProgressDialog(ctx)
            customProgressDialog!!.show()
            customProgressDialog!!.setCancelable(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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

    fun pushFragment(fragment: Fragment?) {
        if (fragment == null) return
        requireActivity().supportFragmentManager.beginTransaction()
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
            resources.updateConfiguration(
                config,
                resources.displayMetrics
            )
        } else if (MySharedPreferences.getMySharedPreferences()!!.language.equals("ar")
        ) {
            val languageToLoad = "ar" // your language
            val locale = Locale(languageToLoad)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            resources.updateConfiguration(
                config,
                resources.displayMetrics
            )
        }
    }

}