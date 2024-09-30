package com.tigwal.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.hbb20.CountryCodePicker.OnCountryChangeListener
import com.tigwal.BuildConfig
import com.tigwal.data.api.UserParams
import com.tigwal.R
import com.tigwal.app.rest.RestConstant
import com.tigwal.base.BaseActivity
import com.tigwal.data.api.Resource
import com.tigwal.databinding.ActivityLoginBinding
import com.tigwal.ui.factory.LoginViewFactory
import com.tigwal.ui.view_model.login.LoginViewModel
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
import java.util.*
import java.util.concurrent.TimeUnit


@Suppress("INACCESSIBLE_TYPE")
class LoginActivity : BaseActivity(), KodeinAware, View.OnClickListener, Callback {
    private lateinit var viewmodellogin: LoginViewModel
    private val factory: LoginViewFactory by instance()
    override val kodein: Kodein by kodein()
    private lateinit var binding: ActivityLoginBinding
    private lateinit var callbackManager: CallbackManager
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val GOOGLE_SIGNIN = 9001
    var isValidMini8Length: Boolean? = false
    var isValidOneUpperAndLower: Boolean? = false
    var isValidOneNumber: Boolean? = false
    var isValidOneSpecial: Boolean? = false
    var validOneUppercase: Boolean? = false
    var validOneLowercase: Boolean? = false
    var loginPreferences: SharedPreferences? = null
    var loginPrefsEditor: SharedPreferences.Editor? = null
    var saveLogin: Boolean? = false
    private var mAuth: FirebaseAuth? = null
    private var mVerificationId: String? = ""
    var deviceUDID: String? = ""

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            activity, R.layout.activity_login
        )
        setupViewModel()
        deviceUDID =
            Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID)
        MySharedPreferences.getMySharedPreferences()?.deviceUDID = deviceUDID
        mAuth = FirebaseAuth.getInstance()
        getIntentData()
        clickListener()
        setFontTypeface()
        binding.viewBgSignin.setBackgroundColor(resources.getColor(R.color.color_blue_light))
        binding.txtSignin.setTextColor(resources.getColor(R.color.color_blue_light))
        binding.viewBgSignup.setBackgroundColor(resources.getColor(R.color.grey))
        binding.txtSignUp.setTextColor(resources.getColor(R.color.grey))
        binding.layoutSignIn.llSignIn.visibility = View.VISIBLE
        binding.layoutSignUp.llSignUp.visibility = View.GONE
        passwordValidation()
        binding.layoutSignUp.edtSignUpPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                passwordValidation()
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

            }
        })
        setupObservers()
        rememberMeLogin()
        logoutFBGoogle()
        // Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        // Facebook
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    val request = GraphRequest.newMeRequest(
                        loginResult!!.accessToken
                    )
                    { `object`, response ->
                        // 2 for Facebook
                        val facebookid = `object`!!.getString("id").toString()
                        val name = `object`!!.getString("name").toString()
                        val email = `object`!!.getString("email").toString()
                        RestConstant.socialTokenId = facebookid
                        RestConstant.socialTokenLoginType = "2"
                        RestConstant.socialName = name
                        RestConstant.socialEmail = email
                        checkSocialLoginCall(RestConstant.socialTokenId, "2")
                    }
                    val parameters = Bundle()
                    parameters.putString(
                        "fields",
                        "id,name,email,gender,birthday,picture.type(large)"
                    )
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    Toast.makeText(
                        applicationContext,
                        "" + resources.getString(R.string.login_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(exception: FacebookException) {
                    Toast.makeText(
                        applicationContext,
                        "" + resources.getString(R.string.login_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun passwordValidation() {
        var tempCount = binding.layoutSignUp.edtSignUpPassword.text!!.length
        if (tempCount > 7) {
            isValidMini8Length = true
        } else {
            isValidMini8Length = false
        }
        var OneUppercasePattern = ".*[A-Z]+.*"
        validOneUppercase = checkPatternValidOnString(
            binding.layoutSignUp.edtSignUpPassword.text.toString(),
            OneUppercasePattern
        )
        var OneLowercasePattern = ".*[a-z]+.*"
        validOneLowercase = checkPatternValidOnString(
            binding.layoutSignUp.edtSignUpPassword.text.toString(),
            OneLowercasePattern
        )
        if (validOneUppercase == true && validOneLowercase == true) {
            isValidOneUpperAndLower = true
        } else {
            isValidOneUpperAndLower = false
        }
        var OneNumberPattern = ".*[0-9]+.*"
        isValidOneNumber =
            checkPatternValidOnString(
                binding.layoutSignUp.edtSignUpPassword.text.toString(),
                OneNumberPattern
            )
        var OneSpecialPattern = ".*[!&^%$#_@()/]+.*"
        isValidOneSpecial = checkPatternValidOnString(
            binding.layoutSignUp.edtSignUpPassword.text.toString(),
            OneSpecialPattern
        )
    }

    override fun getIntentData() {

    }

    @SuppressLint("Range")
    override fun clickListener() {
        binding.layoutSignIn.btnLogin.setOnClickListener(this)
        binding.layoutSignUp.btnSignup.setOnClickListener(this)
        binding.layoutSignIn.llFb.setOnClickListener(this)
        binding.layoutSignIn.llGoogle.setOnClickListener(this)
        binding.layoutSignIn.txtForgtPassword.setOnClickListener(this)
        binding.layoutSignIn.txtSkip.setOnClickListener(this)



        binding.txtSignUp.setOnClickListener(this)
        binding.txtSignin.setOnClickListener(this)
        binding.layoutSignUp.txtTermsConditions.setOnClickListener(this)
        binding.layoutSignIn.imgSigninPassword.setOnClickListener(this)
        binding.layoutSignUp.imgSignUpPassword.setOnClickListener(this)
        binding.layoutSignUp.imgSignUpConfirmPassword.setOnClickListener(this)
        binding.layoutSignIn.imgSigninPassword.setPadding(10, 10, 10, 10)
        binding.layoutSignUp.imgSignUpPassword.setPadding(10, 10, 10, 10)
        binding.layoutSignUp.imgSignUpConfirmPassword.setPadding(10, 10, 10, 10)
        binding.layoutSignUp.txtSkipSignup.setOnClickListener(this)

        binding.layoutSignUp.countrypicker.setOnCountryChangeListener(OnCountryChangeListener
        {
            binding.layoutSignUp.txtSelectedCountryCode.visibility = View.GONE
            binding.layoutSignUp.countrypicker.alpha = 255F
            MySharedPreferences.getMySharedPreferences()!!.countrycode =
                binding.layoutSignUp.countrypicker.selectedCountryCodeWithPlus
            MySharedPreferences.getMySharedPreferences()!!.countryshortname =
                binding.layoutSignUp.countrypicker.selectedCountryNameCode
        })
    }

    override fun setFontTypeface() {
        binding.txtSignin.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.txtSignUp.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.layoutSignIn.etEmail.typeface = AppUtils.getREG(activity)
        binding.layoutSignIn.etPassword.typeface = AppUtils.getREG(activity)
        binding.layoutSignIn.txtForgtPassword.typeface = AppUtils.getMIDIUM(activity)
        binding.layoutSignIn.btnLogin.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.layoutSignIn.txtLoginWith.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.layoutSignUp.edtSignUpName.typeface = AppUtils.getREG(activity)
        binding.layoutSignUp.txtSelectedCountryCode.typeface = AppUtils.getREG(activity)
        binding.layoutSignUp.edtSignUpNumber.typeface = AppUtils.getREG(activity)
        binding.layoutSignUp.edtSignUpEmail.typeface = AppUtils.getREG(activity)
        binding.layoutSignUp.edtSignUpPassword.typeface = AppUtils.getREG(activity)
        binding.layoutSignUp.edtSignUpConfirmPassword.typeface = AppUtils.getREG(activity)
        binding.layoutSignUp.chkIAgree.typeface = AppUtils.getMIDIUM(activity)
        binding.layoutSignUp.txtTermsConditions.typeface = AppUtils.getMIDIUM(activity)
        binding.layoutSignUp.btnSignup.typeface = AppUtils.getBOLDMIDIUM(activity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btn_login -> {
                hideKeyboard(activity)
                if (Connectivity.isConnected(activity)) {
                    if (binding.layoutSignIn.etEmail.text.toString().isEmpty()) {
                        Toast.makeText(
                            activity,
                            "" + resources.getString(R.string.please_enter_email),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (!AppUtils.isEmailValid(binding.layoutSignIn.etEmail.text.toString())) {
                        Toast.makeText(
                            applicationContext,
                            "" + resources.getString(R.string.please_enter_valid_email),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (binding.layoutSignIn.etPassword.text.toString().isEmpty()) {
                        Toast.makeText(
                            applicationContext,
                            "" + resources.getString(R.string.please_enter_password),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        loginPrefsEditor!!.putBoolean("saveLogin", true);
                        loginPrefsEditor!!.putString(
                            "username",
                            binding.layoutSignIn.etEmail.getText().toString()
                        );
                        loginPrefsEditor!!.putString(
                            "password",
                            binding.layoutSignIn.etPassword.getText().toString()
                        );
                        loginPrefsEditor!!.commit();
                        loginApiCall()
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "" + resources.getString(R.string.noInternetConnection),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            R.id.btn_signup -> {
                hideKeyboard(activity)
                if (!RestConstant.socialTokenId.equals("")) {
                    if (binding.layoutSignUp.edtSignUpName.text.toString().isEmpty()) {
                        Toast.makeText(
                            activity,
                            "" + resources.getString(R.string.please_enter_name),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (binding.layoutSignUp.edtSignUpEmail.text.toString().isEmpty()) {
                        Toast.makeText(
                            activity,
                            "" + resources.getString(R.string.please_enter_email),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (!AppUtils.isEmailValid(binding.layoutSignUp.edtSignUpEmail.text.toString())) {
                        Toast.makeText(
                            applicationContext,
                            "" + resources.getString(R.string.please_enter_valid_email),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (binding.layoutSignUp.edtSignUpNumber.text.toString().isEmpty()) {
                        Toast.makeText(
                            activity,
                            "" + resources.getString(R.string.please_enter_mobile),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (binding.layoutSignUp.chkIAgree.isChecked) {
                        socialSignUpStep2()
                    } else {
                        Toast.makeText(
                            activity,
                            "" + resources.getString(R.string.please_agree_with_policy),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    if (binding.layoutSignUp.edtSignUpName.text.toString().isEmpty()) {
                        Toast.makeText(
                            activity,
                            "" + resources.getString(R.string.please_enter_name),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (binding.layoutSignUp.edtSignUpNumber.text.toString().isEmpty()) {
                        Toast.makeText(
                            activity,
                            "" + resources.getString(R.string.please_enter_mobile),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (binding.layoutSignUp.edtSignUpEmail.text.toString().isEmpty()) {
                        Toast.makeText(
                            activity,
                            "" + resources.getString(R.string.please_enter_email),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (!AppUtils.isEmailValid(binding.layoutSignUp.edtSignUpEmail.text.toString())) {
                        Toast.makeText(
                            applicationContext,
                            "" + resources.getString(R.string.please_enter_valid_email),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (binding.layoutSignUp.edtSignUpPassword.text.toString()
                            .isEmpty()
                    ) {
                        Toast.makeText(
                            applicationContext,
                            "" + resources.getString(R.string.please_enter_password),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (isValidMini8Length == true && isValidOneUpperAndLower == true && isValidOneNumber == true && isValidOneSpecial == true) {

                        binding.layoutSignUp.passwordError.visibility = View.GONE
                        if (binding.layoutSignUp.edtSignUpConfirmPassword.text.toString()
                                .isEmpty()
                        ) {
                            Toast.makeText(
                                applicationContext,
                                "" + resources.getString(R.string.please_enter_confirm_password),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (!binding.layoutSignUp.edtSignUpConfirmPassword.text.toString()
                                .equals(binding.layoutSignUp.edtSignUpPassword.text.toString())
                        ) {
                            Toast.makeText(
                                applicationContext,
                                "" + resources.getString(R.string.please_enter_password_not_match),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (binding.layoutSignUp.chkIAgree.isChecked) {
                                if (Connectivity.isConnected(activity)) {
                                    val params = java.util.HashMap<String, String?>()
                                    params[UserParams.email] =
                                        binding.layoutSignUp.edtSignUpEmail.text.toString()
                                    params[UserParams.mobile_no] =
                                        binding.layoutSignUp.edtSignUpNumber.text.toString()
                                    viewmodellogin.checkEmailMobileExitsApiCall(params)
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "" + resources.getString(R.string.noInternetConnection),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + resources.getString(R.string.please_agree_with_policy),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        binding.layoutSignUp.passwordError.visibility = View.VISIBLE
                        Toast.makeText(
                            applicationContext,
                            "" + resources.getString(R.string.password_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            R.id.imgSigninPassword -> {
                if (binding.layoutSignIn.imgSigninPassword.getTag().toString().equals("SHOW")) {
                    binding.layoutSignIn.etPassword.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                    )
                    binding.layoutSignIn.imgSigninPassword.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_show))
                    binding.layoutSignIn.imgSigninPassword.setTag("HIDE")
                } else {
                    binding.layoutSignIn.etPassword.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                    )
                    binding.layoutSignIn.imgSigninPassword.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_hide))
                    binding.layoutSignIn.imgSigninPassword.setTag("SHOW")
                }
            }

            R.id.imgSignUpPassword -> {
                if (binding.layoutSignUp.imgSignUpPassword.getTag().toString().equals("SHOW")) {
                    binding.layoutSignUp.edtSignUpPassword.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                    )
                    binding.layoutSignUp.imgSignUpPassword.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_show))
                    binding.layoutSignUp.imgSignUpPassword.setTag("HIDE")
                } else {
                    binding.layoutSignUp.edtSignUpPassword.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                    )
                    binding.layoutSignUp.imgSignUpPassword.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_hide))
                    binding.layoutSignUp.imgSignUpPassword.setTag("SHOW")
                }
            }

            R.id.txtSkip -> {
                startActivity(Intent(activity, DashboardActivity::class.java))
                AppUtils.startFromRightToLeft(activity)
            }
            R.id.txtSkipSignup -> {
                startActivity(Intent(activity, DashboardActivity::class.java))
                AppUtils.startFromRightToLeft(activity)
            }

            R.id.imgSignUpConfirmPassword -> {
                if (binding.layoutSignUp.imgSignUpConfirmPassword.getTag().toString()
                        .equals("SHOW")
                ) {
                    binding.layoutSignUp.edtSignUpConfirmPassword.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                    )
                    binding.layoutSignUp.imgSignUpConfirmPassword.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.ic_pwd_show
                        )
                    )
                    binding.layoutSignUp.imgSignUpConfirmPassword.setTag("HIDE")
                } else {
                    binding.layoutSignUp.edtSignUpConfirmPassword.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                    )
                    binding.layoutSignUp.imgSignUpConfirmPassword.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.ic_pwd_hide
                        )
                    )
                    binding.layoutSignUp.imgSignUpConfirmPassword.setTag("SHOW")
                }
            }

            R.id.txt_forgtPassword -> {
                startActivity(Intent(activity, ForgotPasswordActivity::class.java))
                AppUtils.startFromRightToLeft(activity)
            }

            R.id.txtTermsConditions -> {
                startActivity(
                    Intent(activity, CMSScreenActivity::class.java).putExtra(
                        "Flag",
                        "user-agreement"
                    )
                )
                AppUtils.startFromRightToLeft(activity)
            }

            R.id.txt_signUp -> {
                hideKeyboard(activity)
                binding.txtSignUp.setTextColor(resources.getColor(R.color.color_blue_light))
                binding.viewBgSignup.setBackgroundColor(resources.getColor(R.color.color_blue_light))
                binding.txtSignin.setTextColor(resources.getColor(R.color.grey))
                binding.viewBgSignin.setBackgroundColor(resources.getColor(R.color.grey))
                binding.layoutSignIn.llSignIn.visibility = View.GONE
                binding.layoutSignUp.llSignUp.visibility = View.VISIBLE
                binding.layoutSignUp.llConfirmPassword.visibility = View.VISIBLE
                binding.layoutSignUp.edtSignUpEmail.setEnabled(true)
                binding.layoutSignUp.passwordError.visibility = View.VISIBLE
                binding.layoutSignUp.viewPassword.visibility = View.VISIBLE
                binding.layoutSignUp.viewPassword1.visibility = View.VISIBLE
                binding.layoutSignUp.llPassword.visibility = View.VISIBLE
                RestConstant.socialName = ""
                RestConstant.socialEmail = ""
                RestConstant.socialTokenId = ""
            }

            R.id.txt_signin -> {
                hideKeyboard(activity)
                binding.viewBgSignin.setBackgroundColor(resources.getColor(R.color.color_blue_light))
                binding.txtSignin.setTextColor(resources.getColor(R.color.color_blue_light))
                binding.viewBgSignup.setBackgroundColor(resources.getColor(R.color.grey))
                binding.txtSignUp.setTextColor(resources.getColor(R.color.grey))
                binding.layoutSignIn.llSignIn.visibility = View.VISIBLE
                binding.layoutSignUp.llSignUp.visibility = View.GONE
            }

            R.id.llGoogle -> {
                googleSignIn()
            }
            R.id.llFb -> {
                loginFacebook()
            }
        }
    }

    fun checkPatternValidOnString(text: String, pattern: String): Boolean {
        val pattern = Regex(pattern)
        if (pattern.containsMatchIn(text)) {
            return true
        }
        return false
    }

    fun rememberMeLogin() {
        loginPreferences = activity.getSharedPreferences("loginPrefs", MODE_PRIVATE)
        loginPrefsEditor = loginPreferences!!.edit()
        saveLogin = loginPreferences!!.getBoolean("saveLogin", false)
        if (saveLogin!!) {
            binding.layoutSignIn.etEmail.setText(loginPreferences!!.getString("username", ""))
            binding.layoutSignIn.etPassword.setText(loginPreferences!!.getString("password", ""))
        }
    }

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, GOOGLE_SIGNIN
        )
    }

    private fun loginApiCall() {
        val params = HashMap<String, String?>()
        params[UserParams.email] = binding.layoutSignIn.etEmail.text.toString()
        params[UserParams.password] = binding.layoutSignIn.etPassword.text.toString()
        Log.e("TAG", "loginApiCall:params " + params.toString())
        viewmodellogin.loginApi(params)
    }

    //set view model
    private fun setupViewModel() {
        viewmodellogin = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
        binding.viewmodel = viewmodellogin
    }

    //set observer
    private fun setupObservers() {

        viewmodellogin.sendotpApiResponse.observe(
            this
        ) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressDialog()
                    Log.d("Response", "====== signupApiResponse ====> " + response)
                    response.data?.let { sendotpResponse ->
                        if (sendotpResponse.code == 200) {
                            if (sendotpResponse.status == true) {
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        VerifyOtpActivity::class.java
                                    ).putExtra("Flag", "SIGNUP")
                                        .putExtra(
                                            UserParams.otp,
                                            "" + sendotpResponse.data!!.otp
                                        )
                                        .putExtra(
                                            UserParams.email,
                                            binding.layoutSignUp.edtSignUpEmail.text.toString()
                                        )
                                        .putExtra(
                                            UserParams.mobile_no,
                                            binding.layoutSignUp.edtSignUpNumber.text.toString()
                                        )
                                        .putExtra(
                                            UserParams.country_code,
                                            binding.layoutSignUp.countrypicker.selectedCountryCodeWithPlus.toString()
                                        )
                                        .putExtra(
                                            UserParams.country_short_name,
                                            binding.layoutSignUp.countrypicker.selectedCountryNameCode.toString()
                                        )
                                        .putExtra(
                                            UserParams.password,
                                            binding.layoutSignUp.edtSignUpPassword.text.toString()
                                        )
                                        .putExtra(
                                            UserParams.name,
                                            binding.layoutSignUp.edtSignUpName.text.toString()
                                        )
                                )
                                AppUtils.startFromRightToLeft(activity)
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + sendotpResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                activity,
                                "" + sendotpResponse.message,
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
        }
        viewmodellogin.loginApiResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d(
                            "Response", "====== loginApiResponse ====> " + Gson().toJson(response)
                        )
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

        viewmodellogin.checkSocialLoginResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d("Response", "====== LOGIN ====> " + response)
                        response.data?.let { loginResponse ->
                            if (loginResponse.code == 200) {
                                if (loginResponse.status == true) {
                                    Toast.makeText(
                                        activity,
                                        "" + loginResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    setLoginUserData(loginResponse.data)
                                    MySharedPreferences.getMySharedPreferences()!!.isLogin = true
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
                                hideKeyboard(activity)
                                binding.txtSignUp.setTextColor(resources.getColor(R.color.color_blue_light))
                                binding.viewBgSignup.setBackgroundColor(resources.getColor(R.color.color_blue_light))
                                binding.txtSignin.setTextColor(resources.getColor(R.color.grey))
                                binding.viewBgSignin.setBackgroundColor(resources.getColor(R.color.grey))
                                binding.layoutSignIn.llSignIn.visibility = View.GONE
                                binding.layoutSignUp.llSignUp.visibility = View.VISIBLE
                                binding.layoutSignUp.llConfirmPassword.visibility = View.GONE
                                binding.layoutSignUp.passwordError.visibility = View.GONE
                                binding.layoutSignUp.viewPassword.visibility = View.GONE
                                binding.layoutSignUp.viewPassword1.visibility = View.GONE
                                binding.layoutSignUp.llPassword.visibility = View.GONE
                                binding.layoutSignUp.edtSignUpName.setText(RestConstant.socialName)
                                binding.layoutSignUp.edtSignUpEmail.setText(RestConstant.socialEmail)
                                binding.layoutSignUp.edtSignUpEmail.setEnabled(false)
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

        viewmodellogin.socialSignUpResponse.observe(
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

        viewmodellogin.checkEmailMobileExitsCallResponse.observe(
            this
        ) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressDialog()
                    Log.d("Response", "====== checkEmailMobileExitsCallResponse ====> " + response)
                    response.data?.let { checkEmailMobileExistResponse ->
                        if (checkEmailMobileExistResponse.code == 200) {
                            if (checkEmailMobileExistResponse.status == true) {
                                startPhoneNumberVerification(binding.layoutSignUp.countrypicker.selectedCountryCodeWithPlus + binding.layoutSignUp.edtSignUpNumber.text.toString())
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + checkEmailMobileExistResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                activity,
                                "" + checkEmailMobileExistResponse.message,
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
        }

        viewmodellogin.appInstallationApiResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        Log.d(
                            "Response",
                            "====== appinstallationResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { appinstallationResponse ->

                        }
                    }
                    is Resource.Error ->
                    {
                    }
                    is Resource.Loading -> {
                    }
                }
            })
    }

    fun loginFacebook() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, listOf("public_profile", "email"))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === PICK_IMAGE_REQUEST_CODE && resultCode === RESULT_OK && data != null && data.data != null) {
            val uri = data.data
            val projection =
                arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor =
                uri?.let {
                    Objects.requireNonNull(this).contentResolver
                        .query(it, projection, null, null, null)
                }!!
            Objects.requireNonNull(cursor).moveToFirst()
        } else if (requestCode == GOOGLE_SIGNIN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            RestConstant.socialTokenId = account?.id.toString()
            RestConstant.socialName = account?.displayName.toString()
            RestConstant.socialEmail = account?.email.toString()
            RestConstant.socialTokenLoginType = "3"
            // 3 - Google
            checkSocialLoginCall(RestConstant.socialTokenId, "3")
        } catch (e: ApiException) {
            Toast.makeText(
                applicationContext,
                "" + resources.getString(R.string.login_failed),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 5
        const val PICK_IMAGE_REQUEST_CODE = 1000
    }

    fun logoutFBGoogle() {
        LoginManager.getInstance().logOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(activity, gso)
        googleSignInClient.signOut()
    }

    fun checkSocialLoginCall(socialToken: String, socialLoginType: String) {
        val params = HashMap<String, String?>()
        params[UserParams.login_type] = socialLoginType
        if (socialLoginType.equals("3")) {
            params[UserParams.google_token] = socialToken
        } else {
            params[UserParams.facebook_token] = socialToken
        }
        viewmodellogin.check_social_id_exists(params, activity)
    }

    fun socialSignUpStep2() {
        val params = HashMap<String, String?>()
        params[UserParams.name] = binding.layoutSignUp.edtSignUpName.text.toString()
        params[UserParams.email] = binding.layoutSignUp.edtSignUpEmail.text.toString()
        params[UserParams.login_type] = RestConstant.socialTokenLoginType
        params[UserParams.country_code] =
            MySharedPreferences.getMySharedPreferences()!!.countrycode
        params[UserParams.mobile_no] = binding.layoutSignUp.edtSignUpNumber.text.toString()
        params[UserParams.password] = ""
        params[UserParams.facebook_token] = RestConstant.socialTokenId
        params[UserParams.google_token] = RestConstant.socialTokenId
        params[UserParams.apple_token] = ""
        params[UserParams.country_short_name] =
            MySharedPreferences.getMySharedPreferences()!!.countryshortname
        viewmodellogin.socialSignUpApi(params)
    }

    // GET TEXT CODE SENT SO YOU CAN USE IT TO SIGN IN
    private fun startPhoneNumberVerification(phoneNumber: String) {
        showProgressDialog(activity)
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(30, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onCodeSent(
                    verificationId: String,
                    token: ForceResendingToken
                ) {
                    hideProgressDialog()
                    Log.e("TAG", "onCodeSent:$verificationId")
                    mVerificationId = verificationId
                    startActivity(
                        Intent(
                            this@LoginActivity,
                            VerifyOtpActivity::class.java
                        ).putExtra("Flag", "SIGNUP")
                            .putExtra(
                                UserParams.otp,
                                "" + mVerificationId
                            )
                            .putExtra(
                                UserParams.email,
                                binding.layoutSignUp.edtSignUpEmail.text.toString()
                            )
                            .putExtra(
                                UserParams.mobile_no,
                                binding.layoutSignUp.edtSignUpNumber.text.toString()
                            )
                            .putExtra(
                                UserParams.country_code,
                                binding.layoutSignUp.countrypicker.selectedCountryCodeWithPlus.toString()
                            )
                            .putExtra(
                                UserParams.country_short_name,
                                binding.layoutSignUp.countrypicker.selectedCountryNameCode.toString()
                            )
                            .putExtra(
                                UserParams.password,
                                binding.layoutSignUp.edtSignUpPassword.text.toString()
                            )
                            .putExtra(
                                UserParams.name,
                                binding.layoutSignUp.edtSignUpName.text.toString()
                            )
                    )
                    AppUtils.startFromRightToLeft(activity)
                }

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    hideProgressDialog()
                    startActivity(
                        Intent(
                            this@LoginActivity,
                            VerifyOtpActivity::class.java
                        ).putExtra("Flag", "SIGNUP")
                            .putExtra(
                                UserParams.otp,
                                "" + mVerificationId
                            )
                            .putExtra(
                                UserParams.email,
                                binding.layoutSignUp.edtSignUpEmail.text.toString()
                            )
                            .putExtra(
                                UserParams.mobile_no,
                                binding.layoutSignUp.edtSignUpNumber.text.toString()
                            )
                            .putExtra(
                                UserParams.country_code,
                                binding.layoutSignUp.countrypicker.selectedCountryCodeWithPlus.toString()
                            )
                            .putExtra(
                                UserParams.country_short_name,
                                binding.layoutSignUp.countrypicker.selectedCountryNameCode.toString()
                            )
                            .putExtra(
                                UserParams.password,
                                binding.layoutSignUp.edtSignUpPassword.text.toString()
                            )
                            .putExtra(
                                UserParams.name,
                                binding.layoutSignUp.edtSignUpName.text.toString()
                            )
                    )
                    AppUtils.startFromRightToLeft(activity)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("TAG", "onVerificationFailed", e)
                    hideProgressDialog()
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            applicationContext,
                            "" + resources.getString(R.string.valid_sms_number),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else if (e is FirebaseTooManyRequestsException) {
                        Toast.makeText(
                            applicationContext,
                            "" + resources.getString(R.string.quate_limit_sms),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun onResume() {
        super.onResume()
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
            viewmodellogin.appInstallationApi(params)
        } else {
            Toast.makeText(
                activity,
                "" + resources.getString(R.string.noInternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}