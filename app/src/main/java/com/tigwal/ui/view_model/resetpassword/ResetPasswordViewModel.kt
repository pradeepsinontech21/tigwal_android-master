package com.tigwal.ui.view_model.resetpassword

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.resetpassword.ResetPasswordResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import retrofit2.Response

class ResetPasswordViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var mobile: String? = ""
    var noInternetConnection: String = ""

    private val resetPasswordApiCall = MutableLiveData<Resource<ResetPasswordResponse>>()
    val resetPasswordApiResponse: LiveData<Resource<ResetPasswordResponse>> = resetPasswordApiCall

    // Reset Password Api call
    fun resetPasswordApi(params: HashMap<String, String?>) = viewModelScope.launch {
        resetPasswordApiCall(params)
    }

    private suspend fun resetPasswordApiCall(params: HashMap<String, String?>) {
        resetPasswordApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.reset_password(params)
            resetPasswordApiCall.postValue(handleResponse(response))
        } else {
            resetPasswordApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleResponse(response: Response<ResetPasswordResponse>): Resource<ResetPasswordResponse>? {
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