package com.tigwal.ui.view_model.splash

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.splash.AppInstallationResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import retrofit2.Response

class SplashViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var errorString = MutableLiveData<String>()
    var noInternetConnection: String = ""

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