package com.tigwal.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tigwal.data.api.UserParams
import com.tigwal.R
import com.tigwal.base.BaseActivity
import com.tigwal.data.api.Resource
import com.tigwal.databinding.ActivityResetPasswordBinding
import com.tigwal.ui.factory.ResetPasswordFactory
import com.tigwal.ui.view_model.resetpassword.ResetPasswordViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.HashMap

class ResetPasswordActivity : BaseActivity(), KodeinAware, View.OnClickListener {
    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var viewModel: ResetPasswordViewModel
    override val kodein: Kodein by kodein()
    private val factory: ResetPasswordFactory by instance()
    var flag: String? = ""
    var emailID: String? = ""
    var isValidMini8Length: Boolean? = false
    var isValidOneUpperAndLower: Boolean? = false
    var isValidOneNumber: Boolean? = false
    var isValidOneSpecial: Boolean? = false
    var validOneUppercase: Boolean? = false
    var validOneLowercase: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_reset_password
        )
        viewModel = ViewModelProvider(this, factory).get(ResetPasswordViewModel::class.java)
        getIntentData()
        clickListener()
        setupObservers()
        setFontTypeface()
        binding.toolBar.txtHeaderTitle.setText(resources.getString(R.string.new_password))

        binding.etNewPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                //Validation Minimum 8 character length
                var tempCount = binding.etNewPassword.text!!.length
                if (tempCount > 7) {
                    isValidMini8Length = true

                } else {
                    isValidMini8Length = false
                }

                //Validation One Uppercase Character
                var OneUppercasePattern = ".*[A-Z]+.*"
                validOneUppercase = checkPatternValidOnString(binding.etNewPassword.text.toString(), OneUppercasePattern)

                //Validation One Lowercase Character
                var OneLowercasePattern = ".*[a-z]+.*"
                validOneLowercase= checkPatternValidOnString(binding.etNewPassword.text.toString(), OneLowercasePattern)

                if (validOneUppercase==true && validOneLowercase==true)
                {
                    isValidOneUpperAndLower = true
                } else {
                    isValidOneUpperAndLower = false
                }

                //Validation One Number Character
                var OneNumberPattern = ".*[0-9]+.*"
                isValidOneNumber =
                    checkPatternValidOnString(binding.etNewPassword.text.toString(), OneNumberPattern)

                //Validation One Special Character
                var OneSpecialPattern = ".*[!&^%$#_@()/]+.*"
                isValidOneSpecial = checkPatternValidOnString(binding.etNewPassword.text.toString(), OneSpecialPattern)
            }
        })
    }

    private fun setupObservers() {
        viewModel.resetPasswordApiResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d("Response", "====== resetPasswordApiResponse ====> " + response)
                        response.data?.let { resetPasswordResponse ->
                            if (resetPasswordResponse.code == 200)
                            {
                                if (resetPasswordResponse.status == true)
                                {
                                    Toast.makeText(
                                        applicationContext,
                                        "" + resetPasswordResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(activity, LoginActivity::class.java))
                                    AppUtils.startFromRightToLeft(activity)
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + resetPasswordResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + resetPasswordResponse.message,
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

    override fun getIntentData() {
        flag = intent.getStringExtra("Flag")
        emailID = intent.getStringExtra("email")
    }


    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.ivEyePassword.setOnClickListener(this)
        binding.ivEyeConPassword.setOnClickListener(this)
        binding.ivEyePassword.setPadding(10, 10, 10, 10)
        binding.ivEyeConPassword.setPadding(10, 10, 10, 10)
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.txtPasswordTitle.typeface = AppUtils.getBOLD(activity)
        binding.txtPasswordMessage.typeface = AppUtils.getMIDIUM(activity)
        binding.etNewPassword.typeface = AppUtils.getREG(activity)
        binding.etConPassword.typeface = AppUtils.getREG(activity)
        binding.btnSubmit.typeface = AppUtils.getBOLDMIDIUM(activity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressed()
            }
            R.id.btn_submit -> {
                if (binding.etNewPassword.text.toString().equals("")) {
                    Toast.makeText(
                        applicationContext,
                        "" + resources.getString(R.string.new_password_is_required),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (isValidMini8Length==true && isValidOneUpperAndLower==true && isValidOneNumber==true && isValidOneSpecial==true){
                    binding.passwordError.visibility = View.GONE
                    if (binding.etConPassword.text.toString().equals("")) {
                        Toast.makeText(
                            applicationContext,
                            "" + resources.getString(R.string.confirm_password_is_required),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (!binding.etNewPassword.text.toString()
                            .equals(binding.etConPassword.text.toString())
                    ) {
                        Toast.makeText(
                            applicationContext,
                            "" + resources.getString(R.string.new_and_confirm_password_miss_match),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        hideKeyboard(activity!!)
                        if (Connectivity.isConnected(activity))
                        {
                            resetPassword()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "" + resources.getString(R.string.noInternetConnection),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else{
                    binding.passwordError.visibility = View.VISIBLE
                    Toast.makeText(
                        applicationContext,
                        "" + resources.getString(R.string.password_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            R.id.iv_eye_password -> {
                if (binding.ivEyePassword.getTag().toString().equals("SHOW")) {
                    binding.etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                    binding.ivEyePassword.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_show))
                    binding.ivEyePassword.setTag("HIDE")
                } else {
                    binding.etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())
                    binding.ivEyePassword.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_hide))
                    binding.ivEyePassword.setTag("SHOW")
                }
            }
            R.id.iv_eye_con_password -> {
                if (binding.ivEyeConPassword.getTag().toString().equals("SHOW")) {
                    binding.etConPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                    binding.ivEyeConPassword.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_show))
                    binding.ivEyeConPassword.setTag("HIDE")
                } else {
                    binding.etConPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())
                    binding.ivEyeConPassword.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_hide))
                    binding.ivEyeConPassword.setTag("SHOW")
                }
            }
        }
    }
    fun checkPatternValidOnString(text: String, pattern: String): Boolean
    {
        val pattern = Regex(pattern)
        if (pattern.containsMatchIn(text)) {
            return true
        }
        return false
    }

    fun resetPassword()
    {
        val params = HashMap<String, String?>()
        params[UserParams.email] = emailID
        params[UserParams.password] = binding.etNewPassword.text.toString()
        viewModel.resetPasswordApi(params)
    }
}