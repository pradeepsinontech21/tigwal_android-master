package com.tigwal.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tigwal.data.api.UserParams
import com.tigwal.R
import com.tigwal.base.BaseActivity
import com.tigwal.data.api.Resource
import com.tigwal.databinding.ActivityForgotPasswordBinding
import com.tigwal.ui.factory.ForgotPasswordFactory
import com.tigwal.ui.view_model.forgotpassword.ForgotPasswordViewModel
import com.tigwal.utils.AppUtils
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.HashMap

class ForgotPasswordActivity : BaseActivity(), KodeinAware, View.OnClickListener {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var viewModel: ForgotPasswordViewModel
    override val kodein: Kodein by kodein()
    private val factory: ForgotPasswordFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            activity, R.layout.activity_forgot_password
        )
        viewModel = ViewModelProvider(this, factory).get(ForgotPasswordViewModel::class.java)
        getIntentData()
        clickListener()
        setupObservers()
        setFontTypeface()
        binding.toolBar.txtHeaderTitle.setText(resources.getString(R.string.forgot_password))
    }

    override fun getIntentData() {

    }

    //set observer
    private fun setupObservers() {

        viewModel.forgetPasswordApiResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d("Response", "====== signupApiResponse ====> " + response)
                        response.data?.let { forgetpasswordResponse ->
                            if (forgetpasswordResponse.code == 200) {
                                if (forgetpasswordResponse.status == true) {
                                    Toast.makeText(
                                        applicationContext,
                                        "" + forgetpasswordResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(
                                        Intent(
                                            activity,
                                            VerifyOtpActivity::class.java
                                        ).putExtra("Flag", "FORGOT_PASSWORD")
                                            .putExtra(UserParams.otp, ""+forgetpasswordResponse.data!!.otp)
                                            .putExtra(UserParams.email, binding.edtEmail.text.toString())
                                    )
                                    AppUtils.startFromRightToLeft(activity)
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + forgetpasswordResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + forgetpasswordResponse.message,
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

    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.txtPasswordTitle.typeface = AppUtils.getBOLD(activity)
        binding.txtPasswordMessage.typeface = AppUtils.getMIDIUM(activity)
        binding.edtEmail.typeface = AppUtils.getREG(activity)
        binding.btnSubmit.typeface = AppUtils.getBOLDMIDIUM(activity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressed()
            }
            R.id.btn_submit -> {
                if (binding.edtEmail.text.toString().equals("")) {
                    Toast.makeText(
                        applicationContext,
                        "" + resources.getString(R.string.please_enter_email),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (!AppUtils.isEmailValid(binding.edtEmail.text.toString())) {
                    Toast.makeText(
                        applicationContext,
                        "" + resources.getString(R.string.please_enter_valid_email),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    forgotPassword()
                }
            }
        }
    }

    fun forgotPassword() {
        val params = HashMap<String, String?>()
        params[UserParams.email] = binding.edtEmail.text.toString()
        viewModel.forgotPasswordApi(params)
    }
}