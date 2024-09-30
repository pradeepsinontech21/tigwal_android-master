package com.tigwal.ui.view_model.changepassword

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.notification_enable.NotificationUpdateResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.HashMap

class ChangePasswordViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {

    var mobile: String? = ""
    var noInternetConnection: String = ""

    private val changePasswordCall = MutableLiveData<Resource<NotificationUpdateResponse>>()
    val changePasswordResponse: LiveData<Resource<NotificationUpdateResponse>> = changePasswordCall

    // changePasswordCallApi call
    fun changePasswordCallApi(authToken: String,params: HashMap<String, String?>) = viewModelScope.launch {
        changePasswordCallApiCall(authToken,params)
    }

    private suspend fun changePasswordCallApiCall(authToken:String ,params: HashMap<String, String?>) {
        changePasswordCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.change_password(authToken,params)
            changePasswordCall.postValue(handlecheckenableNotificationApiCallResponse(response))
        } else {
            changePasswordCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handlecheckenableNotificationApiCallResponse(response: Response<NotificationUpdateResponse>): Resource<NotificationUpdateResponse>? {
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