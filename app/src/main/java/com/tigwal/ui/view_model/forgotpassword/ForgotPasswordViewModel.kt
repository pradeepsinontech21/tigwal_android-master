package com.tigwal.ui.view_model.forgotpassword

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.forgotpassword.ForgotPasswordResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import retrofit2.Response

class ForgotPasswordViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var mobile: String? = ""
    var noInternetConnection: String = ""
    private val forgetPasswordApiCall = MutableLiveData<Resource<ForgotPasswordResponse>>()
    val forgetPasswordApiResponse: LiveData<Resource<ForgotPasswordResponse>> = forgetPasswordApiCall

    // forgetPasswordApiResponse call
    fun forgotPasswordApi(params: HashMap<String, String?>) = viewModelScope.launch {
        forgotPasswordApiCall(params)
    }

    private suspend fun forgotPasswordApiCall(params: HashMap<String, String?>) {
        forgetPasswordApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.forgot_password(params)
            forgetPasswordApiCall.postValue(handleResponse(response))
        } else {
            forgetPasswordApiCall.postValue(Resource.Error(noInternetConnection))
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
}