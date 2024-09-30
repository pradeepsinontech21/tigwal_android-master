package com.tigwal.ui.view_model.setting

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.login.LoginResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.HashMap

class SettingViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var errorString = MutableLiveData<String>()
    var noInternetConnection: String = ""


    private val logoutApiCall = MutableLiveData<Resource<LoginResponse>>()
    val logoutApiResponse: LiveData<Resource<LoginResponse>> = logoutApiCall

    // Logout api call
    fun logoutApi(authToken: String,params: HashMap<String, String?>) = viewModelScope.launch {
        logoutApiCall(authToken,params)
    }

    private suspend fun logoutApiCall(authToken:String ,params: HashMap<String, String?>) {
        logoutApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.logout(authToken,params)
            logoutApiCall.postValue(handlechecksocialoginResponse(response))
        } else {
            logoutApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handlechecksocialoginResponse(response: Response<LoginResponse>): Resource<LoginResponse>? {
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