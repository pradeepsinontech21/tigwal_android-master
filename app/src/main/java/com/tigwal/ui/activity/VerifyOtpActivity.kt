package com.tigwal.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tigwal.data.api.UserParams
import com.tigwal.R
import com.tigwal.base.BaseActivity
import com.tigwal.data.api.Resource
import com.tigwal.databinding.ActivityVerifyOtpBinding
import com.tigwal.ui.factory.VerifyOtpViewFactory
import com.tigwal.ui.view_model.otp.VerifyOtpViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.MySharedPreferences
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.IOException
import java.util.concurrent.TimeUnit

class VerifyOtpActivity : BaseActivity(), KodeinAware, View.OnClickListener, Callback {
    private lateinit var binding: ActivityVerifyOtpBinding
    private lateinit var viewModel: VerifyOtpViewModel
    override val kodein: Kodein by kodein()
    private val factory: VerifyOtpViewFactory by instance()
    var flag: String? = ""
    var otp: String? = ""
    var emailID: String? = ""
    var strCountryCode: String? = ""
    var strMobileMo: String? = ""
    var strCountryShortName: String? = ""
    var strPassword: String? = ""
    var strName: String? = ""
    private var mAuth: FirebaseAuth? = null
    private var mVerificationId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_verify_otp
        )
        viewModel = ViewModelProvider(this, factory).get(VerifyOtpViewModel::class.java)
        getIntentData()
        clickListener()
        setupObservers()
        setFontTypeface()

        mAuth = FirebaseAuth.getInstance()
        if (flag.equals("SIGNUP"))
        {
            binding.txtPasswordTitle.setText(resources.getString(R.string.verify_otp_title))
            strMobileMo = intent.getStringExtra(UserParams.mobile_no)
            strCountryCode = intent.getStringExtra(UserParams.country_code)
            strCountryShortName = intent.getStringExtra(UserParams.country_short_name)
            strPassword = intent.getStringExtra(UserParams.password)
            strName = intent.getStringExtra(UserParams.name)
            binding.txtPasswordMessage.setText(resources.getString(R.string.verify_otp_message) + " " + strCountryCode + " " + strMobileMo)
        } else {
            binding.txtPasswordTitle.setText(resources.getString(R.string.enter_recovery_code))
            binding.txtPasswordMessage.setText(resources.getString(R.string.verify_otp_message_email) + " " + emailID)

        }
        binding.toolBar.txtHeaderTitle.setText(resources.getString(R.string.phone_verification))
    }

    override fun getIntentData() {
        flag = intent.getStringExtra("Flag")
        otp = intent.getStringExtra(UserParams.otp)
        emailID = intent.getStringExtra(UserParams.email)
    }

    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.txtResendOtp.setOnClickListener(this)
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.txtPasswordTitle.typeface = AppUtils.getBOLD(activity)
        binding.txtPasswordMessage.typeface = AppUtils.getMIDIUM(activity)
        binding.txtDontReceiveCOde.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.txtResendOtp.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.btnSubmit.typeface = AppUtils.getBOLDMIDIUM(activity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressed()
            }
            R.id.btn_submit -> {
                if (binding.pinviewOTP.getValue().isEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "" + resources.getString(R.string.otp_is_required),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (binding.pinviewOTP.getValue().length != 6) {
                    Toast.makeText(
                        applicationContext,
                        "" + resources.getString(R.string.otp_is_not_valid),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (Connectivity.isConnected(activity)) {
                        if (flag.equals("SIGNUP")) {
                            verifyPhoneNumberWithCode(
                                otp!!,
                                binding.pinviewOTP.getValue().toString()
                            )
                        } else {
                            if (binding.pinviewOTP.getValue().toString().equals(otp)) {
                                startActivity(
                                    Intent(activity, ResetPasswordActivity::class.java)
                                        .putExtra(UserParams.email, emailID)
                                )
                                AppUtils.startFromRightToLeft(activity)
                            } else {

                                Toast.makeText(
                                    applicationContext,
                                    "" + resources.getString(R.string.otp_is_not_valid),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            activity,
                            "" + resources.getString(R.string.noInternetConnection),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            R.id.txtResendOtp -> {
                if (flag.equals("SIGNUP")) {
                    resendVerificationCode(strCountryCode + strMobileMo.toString())
                } else {
                    forgotPasswordResendOTP()
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.resendApiResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d("Response", "====== resendApiResponse ====> " + response)
                        response.data?.let { forgetpasswordResponse ->
                            if (forgetpasswordResponse.code == 200) {
                                if (forgetpasswordResponse.status == true) {
                                    Toast.makeText(
                                        applicationContext,
                                        "" + forgetpasswordResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    otp = forgetpasswordResponse.data!!.otp.toString()
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

        viewModel.signupApiResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d("Response", "====== signupApiResponse ====> " + response)
                        response.data?.let { loginResponse ->
                            if (loginResponse.code == 200) {
                                if (loginResponse.status == true) {
                                    MySharedPreferences.getMySharedPreferences()!!.isLogin = true
                                    setLoginUserData(loginResponse.data)
                                    Toast.makeText(
                                        applicationContext,
                                        "" + loginResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(
                                        Intent(
                                            this,
                                            DashboardActivity::class.java
                                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                    AppUtils.startFromRightToLeft(activity)
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + loginResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + loginResponse.message,
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

    fun forgotPasswordResendOTP() {
        val params = HashMap<String, String?>()
        params[UserParams.email] = emailID
        viewModel.resendApi(params)
    }

    fun signupApiCall() {
        val params = HashMap<String, String?>()
        params[UserParams.name] = strName
        params[UserParams.email] = emailID
        params[UserParams.login_type] = "1"
        params[UserParams.country_code] = strCountryCode
        params[UserParams.mobile_no] = strMobileMo
        params[UserParams.password] = strPassword
        params[UserParams.facebook_token] = ""
        params[UserParams.google_token] = ""
        params[UserParams.apple_token] = ""
        params[UserParams.country_short_name] = strCountryShortName
        Log.e("TAG", "signupApiCall:params " + params.toString())
        viewModel.signupApi(params)
    }

    // ENTERED CODE AND MANUALLY SIGNING IN WITH THAT CODE
    private fun verifyPhoneNumberWithCode(verificationId: String, code: String)
    {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    // USE TEXT TO SIGN IN
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        showProgressDialog(activity)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val user = task.result.user
                    hideProgressDialog()
                    signupApiCall()
                } else {
                    Log.e("TAG", "signInWithCredential:failure", task.exception)
                    hideProgressDialog()
                    if (task.exception is FirebaseAuthInvalidCredentialsException)
                    {
                    }
                    Toast.makeText(
                        activity,
                        "" + resources.getString(R.string.otp_is_not_valid),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    // Resend Otp
    private fun resendVerificationCode(phoneNumber: String)
    {
        Log.d("resendVerificationCode","==== resendVerificationCode =====> "+phoneNumber)
        showProgressDialog(activity)
        val options1 = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.MICROSECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    hideProgressDialog()
                    Log.e("TAG", "onCodeSent:$verificationId")
                    mVerificationId = verificationId
                    otp=mVerificationId
                    Toast.makeText(
                        applicationContext,
                        "" + resources.getString(R.string.otp_send_successfully),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.e("TAG", "onVerificationCompleted",)
                    hideProgressDialog()
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    hideProgressDialog()
                    Log.e("TAG", "onVerificationFailed", e)
                    if (e is FirebaseAuthInvalidCredentialsException) {
                    } else if (e is FirebaseTooManyRequestsException) {
                        Toast.makeText(applicationContext, ""+resources.getString(R.string.quate_limit_sms), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options1)
    }

    override fun onFailure(call: Call, e: IOException) {
        println(e.fillInStackTrace())
    }

    override fun onResponse(call: Call, response: Response) {
        if (response.isSuccessful) {
            val body = response.body?.string()
            Log.e("TAG", "Inside on response body=" + body.toString())
        } else {
            val codename = response.code;
            Log.e("TAG", "Inside on Response Code=" + codename)
        }
    }
}