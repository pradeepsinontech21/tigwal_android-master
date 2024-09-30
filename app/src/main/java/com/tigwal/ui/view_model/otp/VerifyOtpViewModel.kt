package com.tigwal.ui.view_model.otp

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.forgotpassword.ForgotPasswordResponse
import com.tigwal.data.model.login.LoginResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import retrofit2.Response

class VerifyOtpViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var errorString = MutableLiveData<String>()
    var mobile: String? = ""
    var otp: String? = ""
    var noInternetConnection: String = ""

    private val signupApiCall = MutableLiveData<Resource<LoginResponse>>()
    val signupApiResponse: LiveData<Resource<LoginResponse>> = signupApiCall

    private val resendApiCall = MutableLiveData<Resource<ForgotPasswordResponse>>()
    val resendApiResponse: LiveData<Resource<ForgotPasswordResponse>> = resendApiCall

    // resendApi call
    fun resendApi(params: HashMap<String, String?>) = viewModelScope.launch {
        resendApiCall(params)
    }

    private suspend fun resendApiCall(params: HashMap<String, String?>) {
        resendApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.forgot_password(params)
            resendApiCall.postValue(handleResponse(response))
        } else {
            resendApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleResponse(response: Response<ForgotPasswordResponse>): Resource<ForgotPasswordResponse>? {
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


    // signupApi
    fun signupApi(params: java.util.HashMap<String, String?>) = viewModelScope.launch {
        signupApiCall(params)
    }

    private suspend fun signupApiCall(params: java.util.HashMap<String, String?>) {
        signupApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.signup(params)
            signupApiCall.postValue(handleSignupResponse(response))
        } else {
            signupApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleSignupResponse(response: Response<LoginResponse>): Resource<LoginResponse>? {
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