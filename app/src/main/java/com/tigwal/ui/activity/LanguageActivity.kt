package com.tigwal.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tigwal.R
import com.tigwal.base.BaseActivity
import com.tigwal.databinding.ActivityLanguageBinding
import com.tigwal.ui.factory.LanguageFactory
import com.tigwal.ui.view_model.language.LanguageViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.MySharedPreferences
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

class LanguageActivity : BaseActivity(), KodeinAware, View.OnClickListener {
    private lateinit var binding: ActivityLanguageBinding
    private lateinit var splashviewmodel: LanguageViewModel
    private val factory: LanguageFactory by instance()
    override val kodein: Kodein by kodein()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_language)

        getIntentData()
        clickListener()
        setupViewModel()
        setupObservers()
        setFontTypeface()
        getSelectedLanguage()
    }

    private fun getSelectedLanguage() {
        if (MySharedPreferences.getMySharedPreferences()!!.language == "ar") {
            languageArabic()
        } else if (MySharedPreferences.getMySharedPreferences()!!.language == "en") {
            languageEnglish()
        }
    }

    //set view model
    private fun setupViewModel() {
        splashviewmodel = ViewModelProvider(this, factory).get(LanguageViewModel::class.java)
        binding.viewmodel = splashviewmodel
    }

    //set observer
    private fun setupObservers() {

    }

    override fun getIntentData() {

    }

    override fun clickListener() {
        binding.cvArabic.setOnClickListener(this)
        binding.cvEnglish.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }

    override fun setFontTypeface() {

    }

    fun languageArabic() {
        binding.cbEnglish.visibility = View.INVISIBLE
        binding.cbArabic.visibility = View.VISIBLE
        MySharedPreferences.getMySharedPreferences()!!.language = "ar"
        AppUtils.languageSelection(activity, "ar")
    }

    fun languageEnglish() {
        binding.cbEnglish.visibility = View.VISIBLE
        binding.cbArabic.visibility = View.INVISIBLE
        MySharedPreferences.getMySharedPreferences()!!.language = "en"
        AppUtils.languageSelection(activity, "en")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvArabic -> {
                languageArabic()
                setLangauge()
            }

            R.id.cvEnglish -> {
                languageEnglish()
                setLangauge()
            }

            R.id.btn_submit -> {
                if (!MySharedPreferences.getMySharedPreferences()!!.language.equals("")) {
                    setLangauge()
                    startActivity(Intent(this, LoginActivity::class.java))
                    AppUtils.startFromRightToLeft(activity)
                } else {
                    Toast.makeText(
                        activity, resources.getString(R.string.please_select_langauge),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun setLangauge() {
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