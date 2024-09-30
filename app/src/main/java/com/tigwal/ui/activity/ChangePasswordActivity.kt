package com.tigwal.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.tigwal.data.api.UserParams
import com.tigwal.app.rest.RestConstant
import com.tigwal.R
import com.tigwal.base.BaseActivity
import com.tigwal.data.api.Resource
import com.tigwal.databinding.ActivityChangePasswordBinding
import com.tigwal.ui.factory.ChangePasswordFactory
import com.tigwal.ui.view_model.changepassword.ChangePasswordViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.MySharedPreferences
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ChangePasswordActivity : BaseActivity(), KodeinAware, View.OnClickListener {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var viewModel: ChangePasswordViewModel
    override val kodein: Kodein by kodein()
    private val factory: ChangePasswordFactory by instance()
    var flag: String? = ""
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
            R.layout.activity_change_password
        )
        viewModel = ViewModelProvider(this, factory).get(ChangePasswordViewModel::class.java)
        getIntentData()
        clickListener()
        setupObservers()
        setFontTypeface()
        binding.toolBar.txtHeaderTitle.setText(resources.getString(R.string.change_password))

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
                    setTextImageColor(
                        binding.imgEightDigitValid, binding.txtMinEightChar,
                        isValidMini8Length!!
                    )
                } else {
                    isValidMini8Length = false
                    setTextImageColor(
                        binding.imgEightDigitValid, binding.txtMinEightChar,
                        isValidMini8Length!!
                    )
                }

                //Validation One Uppercase Character
                var OneUppercasePattern = ".*[A-Z]+.*"
                validOneUppercase = checkPatternValidOnString(
                    binding.etNewPassword.text.toString(),
                    OneUppercasePattern
                )

                //Validation One Lowercase Character
                var OneLowercasePattern = ".*[a-z]+.*"
                validOneLowercase = checkPatternValidOnString(
                    binding.etNewPassword.text.toString(),
                    OneLowercasePattern
                )

                if (validOneUppercase == true && validOneLowercase == true) {
                    isValidOneUpperAndLower = true
                    setTextImageColor(
                        binding.imgUpperLowerLetters, binding.txtUpperLowerCase,
                        isValidOneUpperAndLower!!
                    )
                } else {
                    isValidOneUpperAndLower = false
                    setTextImageColor(
                        binding.imgUpperLowerLetters, binding.txtUpperLowerCase,
                        isValidOneUpperAndLower!!
                    )
                }

                //Validation One Number Character
                var OneNumberPattern = ".*[0-9]+.*"
                isValidOneNumber =
                    checkPatternValidOnString(
                        binding.etNewPassword.text.toString(),
                        OneNumberPattern
                    )
                if (isValidOneNumber == true) {
                    setTextImageColor(
                        binding.imgOneNumber, binding.txtAtLeastOneNumber,
                        isValidOneNumber!!
                    )
                } else {
                    setTextImageColor(
                        binding.imgOneNumber, binding.txtAtLeastOneNumber,
                        isValidOneNumber!!
                    )
                }

                //Validation One Special Character
                var OneSpecialPattern = ".*[!&^%$#_@()/]+.*"
                isValidOneSpecial = checkPatternValidOnString(
                    binding.etNewPassword.text.toString(),
                    OneSpecialPattern
                )

                if (isValidOneSpecial == true) {
                    setTextImageColor(
                        binding.imgSpecialCharctaer, binding.txtAtLeastOneChar,
                        isValidOneSpecial!!
                    )
                } else {
                    setTextImageColor(
                        binding.imgSpecialCharctaer, binding.txtAtLeastOneChar,
                        isValidOneSpecial!!
                    )
                }
            }
        })

    }

    fun checkPatternValidOnString(text: String, pattern: String): Boolean {
        val pattern = Regex(pattern)
        if (pattern.containsMatchIn(text)) {
            return true
        }
        return false
    }

    private fun setTextImageColor(
        imgEightDigitValid: AppCompatImageView,
        txtMinEightChar: AppCompatTextView,
        validMini8Length: Boolean
    ) {

        if (validMini8Length) {
            imgEightDigitValid.setImageResource(R.drawable.ic_checked)
            imgEightDigitValid.setColorFilter(
                ContextCompat.getColor(
                    activity,
                    R.color.color_blue_light
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
            txtMinEightChar.setTextColor(ContextCompat.getColor(activity, R.color.color_blue_light))
        } else {

            imgEightDigitValid.setImageResource(R.drawable.ic_unchecked)
            imgEightDigitValid.setColorFilter(
                ContextCompat.getColor(
                    activity,
                    R.color.grey
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
            txtMinEightChar.setTextColor(ContextCompat.getColor(activity, R.color.grey))
        }
    }

    private fun setupObservers() {

        viewModel.changePasswordResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d(
                            "Response",
                            "====== changePasswordResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { logoutResponse ->
                            if (logoutResponse.code == 200) {
                                if (logoutResponse.status == true) {
                                    Toast.makeText(
                                        activity,
                                        "" + logoutResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onBackPressed()
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + logoutResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + logoutResponse.message,
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
                        showProgressDialog(activity!!)
                    }
                }
            })

    }

    override fun getIntentData() {
        flag = intent.getStringExtra("Flag")
    }

    override fun clickListener() {
        //Click
        binding.toolBar.ivBack.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.ivEyePassword.setOnClickListener(this)
        binding.ivEyeConPassword.setOnClickListener(this)
        binding.ivEyeOldPassword.setOnClickListener(this)
        binding.ivEyePassword.setPadding(10, 10, 10, 10)
        binding.ivEyeConPassword.setPadding(10, 10, 10, 10)
        binding.ivEyeOldPassword.setPadding(10, 10, 10, 10)
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.edtOldPassword.typeface = AppUtils.getREG(activity)
        binding.etNewPassword.typeface = AppUtils.getREG(activity)
        binding.etConPassword.typeface = AppUtils.getREG(activity)
        binding.txtMinEightChar.typeface = AppUtils.getREG(activity)
        binding.txtUpperLowerCase.typeface = AppUtils.getREG(activity)
        binding.txtAtLeastOneNumber.typeface = AppUtils.getREG(activity)
        binding.txtAtLeastOneChar.typeface = AppUtils.getREG(activity)
        binding.btnSubmit.typeface = AppUtils.getMIDIUM(activity)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressed()
            }
            R.id.btn_submit -> {
                if (binding.edtOldPassword.text.toString().equals("")) {
                    Toast.makeText(
                        applicationContext,
                        "" + resources.getString(R.string.old_password_is_required),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (binding.etNewPassword.text.toString().equals("")) {
                    Toast.makeText(
                        applicationContext,
                        "" + resources.getString(R.string.new_password_is_required),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (binding.edtOldPassword.text.toString()
                        .equals(binding.etNewPassword.text.toString())
                ) {
                    Toast.makeText(
                        applicationContext,
                        "" + resources.getString(R.string.old_and_new_password_match),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (binding.etConPassword.text.toString().equals("")) {
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

                    if (isValidMini8Length == true && isValidOneUpperAndLower == true && isValidOneNumber == true && isValidOneSpecial == true) {
                        hideKeyboard(activity!!)
                        if (Connectivity.isConnected(activity)) {
                            val params = java.util.HashMap<String, String?>()
                            params[UserParams.old_password] = binding.edtOldPassword.text.toString()
                            params[UserParams.new_password] = binding.etNewPassword.text.toString()
                            viewModel.changePasswordCallApi(
                                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                                params
                            )
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "" + resources.getString(R.string.noInternetConnection),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "" + resources.getString(R.string.password_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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

            R.id.iv_eye_old_password -> {
                if (binding.ivEyeOldPassword.getTag().toString().equals("SHOW")) {
                    binding.edtOldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                    binding.ivEyeOldPassword.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_show))
                    binding.ivEyeOldPassword.setTag("HIDE")
                } else {
                    binding.edtOldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())
                    binding.ivEyeOldPassword.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_hide))
                    binding.ivEyeOldPassword.setTag("SHOW")
                }
            }
        }
    }



}