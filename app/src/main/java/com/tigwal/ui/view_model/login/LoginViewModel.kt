package com.tigwal.ui.view_model.login

import android.app.Activity
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.check_email_mobile_exist.CheckEmailMobileExitsResponse
import com.tigwal.data.model.login.LoginResponse
import com.tigwal.data.model.otp.SendOtpResponse
import com.tigwal.data.model.splash.AppInstallationResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils

import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.util.HashMap

class LoginViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {

    var noInternetConnection: String = ""
    var errorString = MutableLiveData<String>()

    private val checkSocialLoginCall = MutableLiveData<Resource<LoginResponse>>()
    val checkSocialLoginResponse: LiveData<Resource<LoginResponse>> = checkSocialLoginCall

    private val sendotpApiCall = MutableLiveData<Resource<SendOtpResponse>>()
    val sendotpApiResponse: LiveData<Resource<SendOtpResponse>> = sendotpApiCall

    // Send Otp Login api call
    fun sendOtpApi(params: HashMap<String, String?>) = viewModelScope.launch {
        sendOtpApiCall(params)
    }

    private suspend fun sendOtpApiCall(params: HashMap<String, String?>) {
        sendotpApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.send_otp(params)
            sendotpApiCall.postValue(handlesendotpResponse(response))
        } else {
            sendotpApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handlesendotpResponse(response: Response<SendOtpResponse>): Resource<SendOtpResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        } else if (response.errorBody() != null) {
            AppUtils.getErrorMessage(response.errorBody()!!)?.let { it1 ->
                return Resource.Error(it1)
            }
        }
        return Resource.Error(response.message())
    }

    private val loginApiCall = MutableLiveData<Resource<LoginResponse>>()
    val loginApiResponse: LiveData<Resource<LoginResponse>> = loginApiCall
    // loginApi api call
    fun loginApi(params: HashMap<String, String?>) = viewModelScope.launch {
        loginApiCall(params)
    }

    private suspend fun loginApiCall(params: HashMap<String, String?>) {
        loginApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.login(params)
            loginApiCall.postValue(handleloginResponse(response))
        } else {
            loginApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    //handle the response success or fail
    private fun handleloginResponse(response: Response<LoginResponse>): Resource<LoginResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        } else if (response.errorBody() != null) {
            AppUtils.getErrorMessage(response.errorBody()!!)?.let { it1 ->
                return Resource.Error(it1)
            }
        }
        return Resource.Error(response.message())
    }






    // check_social_id_exists api call
    fun check_social_id_exists(params: HashMap<String, String?>, activity: Activity) = viewModelScope.launch {
        check_social_id_existsApiCall(params,activity)
    }

    private suspend fun check_social_id_existsApiCall(
        params: HashMap<String, String?>,
        activity: Activity
    ) {
        checkSocialLoginCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.check_social_id_exists(params)
            checkSocialLoginCall.postValue(handlechecksocialoginResponse(response,activity))
        } else {
            checkSocialLoginCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    //handle the response success or fail
    private fun handlechecksocialoginResponse(response: Response<LoginResponse>, activity: Activity): Resource<LoginResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        } else if (response.errorBody() != null) {
            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
            Toast.makeText(
                activity,
                "" + jsonObj.getString("message"),
                Toast.LENGTH_SHORT
            ).show()
        }
        return Resource.Error(response.message())
    }


    private val socialSignUpCall = MutableLiveData<Resource<LoginResponse>>()
    val socialSignUpResponse: LiveData<Resource<LoginResponse>> = socialSignUpCall

    fun socialSignUpApi(params: HashMap<String, String?>) = viewModelScope.launch {
        socialSignUpApiCall(params)
    }

    private suspend fun socialSignUpApiCall(params: HashMap<String, String?>) {
        socialSignUpCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.signup(params)
            socialSignUpCall.postValue(handleSocialSignupResponse(response))
        } else {
            socialSignUpCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleSocialSignupResponse(response: Response<LoginResponse>): Resource<LoginResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        } else if (response.errorBody() != null) {
            AppUtils.getErrorMessage(response.errorBody()!!)?.let { it1 ->
                return Resource.Error(it1)
            }
        }
        return Resource.Error(response.message())
    }


    // ratingApiCall
    private val checkEmailMobileExitsApiCall = MutableLiveData<Resource<CheckEmailMobileExitsResponse>>()
    val checkEmailMobileExitsCallResponse: LiveData<Resource<CheckEmailMobileExitsResponse>> = checkEmailMobileExitsApiCall

    fun checkEmailMobileExitsApiCall(params: HashMap<String, String?>) =
        viewModelScope.launch {
            checkEmailMobileApiCall( params)
        }

    private suspend fun checkEmailMobileApiCall(params: HashMap<String, String?>) {
        checkEmailMobileExitsApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.check_email_mobile_exists_or_not( params)
            checkEmailMobileExitsApiCall.postValue(handleEmailMobileCallResponse(response))
        } else {
            checkEmailMobileExitsApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleEmailMobileCallResponse(response: Response<CheckEmailMobileExitsResponse>): Resource<CheckEmailMobileExitsResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        } else if (response.errorBody() != null) {
            AppUtils.getErrorMessage(response.errorBody()!!)?.let { it1 ->
                return Resource.Error(it1)
            }
        }
        return Resource.Error(response.message())
    }


    private val appInstallationApiCall = MutableLiveData<Resource<AppInstallationResponse>>()
    val appInstallationApiResponse: LiveData<Resource<AppInstallationResponse>> = appInstallationApiCall


    // appInstallationApi call
    fun appInstallationApi(params: HashMap<String, String?>) = viewModelScope.launch {
        appInstallationApiCall(params)
    }

    private suspend fun appInstallationApiCall(params: HashMap<String, String?>) {
        appInstallationApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.appInstallation(params)
            appInstallationApiCall.postValue(handleResponse(response))
        } else {
            appInstallationApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleResponse(response: Response<AppInstallationResponse>): Resource<AppInstallationResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        } else if (response.errorBody() != null) {
            AppUtils.getErrorMessage(response.errorBody()!!)?.let { it1 ->
                return Resource.Error(it1)
            }
        }
        return Resource.Error(response.message())
    }

}