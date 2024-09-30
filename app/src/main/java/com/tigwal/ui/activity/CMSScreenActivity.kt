package com.tigwal.ui.activity

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.tigwal.app.rest.RestConstant
import com.tigwal.R
import com.tigwal.base.BaseActivity
import com.tigwal.data.api.Resource
import com.tigwal.data.model.cms_page.Data
import com.tigwal.databinding.ActivityCmsScreenBinding
import com.tigwal.ui.factory.CMSScreenFactory
import com.tigwal.ui.view_model.cmsscreen.CMSSCreenViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.MySharedPreferences
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class CMSScreenActivity : BaseActivity(), KodeinAware, View.OnClickListener {
    private lateinit var binding: ActivityCmsScreenBinding
    private lateinit var viewModel: CMSSCreenViewModel
    override val kodein: Kodein by kodein()
    private val factory: CMSScreenFactory by instance()
    var flag: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_cms_screen
        )
        setupViewModel()
        setupObservers()

        getIntentData()
        clickListener()
        setFontTypeface()
        if (flag.equals("privacy_policy"))
        {
            binding.toolBar.txtHeaderTitle.text = resources.getString(R.string.privacy_policy)
        } else if (flag.equals("terms_condition")) {
            binding.toolBar.txtHeaderTitle.text = resources.getString(R.string.terms_and_condition)
        } else if (flag.equals("help")) {
            binding.toolBar.txtHeaderTitle.text = resources.getString(R.string.help)
        }
        else if (flag.equals("faq"))
        {
            binding.toolBar.txtHeaderTitle.text = resources.getString(R.string.faq)
        }
        else if(flag.equals("privacy-policy"))
        {
            binding.toolBar.txtHeaderTitle.text = resources.getString(R.string.privacy_policy)
        }
        else if(flag.equals("user-agreement"))
        {
            binding.toolBar.txtHeaderTitle.text = resources.getString(R.string.customer_aggrement)
        }
         else if(flag.equals("legal"))
        {
            binding.toolBar.txtHeaderTitle.text = resources.getString(R.string.legal)
        }


        if (Connectivity.isConnected(activity)) {
            viewModel.cmsPagesApi(
                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()!!.authToken,
                flag!!
            )
        } else {
            Toast.makeText(
                activity,
                "" + resources.getString(R.string.noInternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getIntentData() {
        flag = intent.getStringExtra("Flag")
    }

    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.txtDescription.typeface = AppUtils.getREG(activity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressed()
            }

        }
    }

    //set view model
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, factory).get(CMSSCreenViewModel::class.java)
        binding.viewmodel = viewModel
    }

    //set observer
    private fun setupObservers() {
        viewModel.cmsPageCallResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d(
                            "Response",
                            "====== cmsPageCallResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { cmsPageCallResponse ->
                            if (cmsPageCallResponse.code == 200) {
                                if (cmsPageCallResponse.status == true) {
                                    cmspagesList(cmsPageCallResponse.data)
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + cmsPageCallResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + cmsPageCallResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    is Resource.Error -> {
                        hideProgressDialog()
                        Toast.makeText(
                            activity,
                            "" + response.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Resource.Loading -> {
                        showProgressDialog(activity)
                    }
                }
            })
    }

    fun cmspagesList(cmsPageData: Data?) {

        if (MySharedPreferences.getMySharedPreferences()!!.language.equals("ar"))
        {
            if (cmsPageData!!.ar_description != null) {
                binding.txtDescription.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(cmsPageData.ar_description, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(cmsPageData.ar_description)
                }
            }
        } else {
            if (cmsPageData!!.description != null) {
                binding.txtDescription.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(cmsPageData.description, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(cmsPageData.description)
                }
            }
        }


    }
}