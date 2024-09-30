package com.tigwal.ui.view_model.notification

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.notification_enable.NotificationUpdateResponse
import com.tigwal.data.model.notification_list.NotificationListResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.HashMap

class NotificationViewModel(
    val application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var errorString = MutableLiveData<String>()
    var noInternetConnection: String = ""

    private val updateNotificationCall = MutableLiveData<Resource<NotificationUpdateResponse>>()
    val updateNotificationResponse: LiveData<Resource<NotificationUpdateResponse>> =
        updateNotificationCall

    // enableNotificationApi api call
    fun enableNotificationApi(authToken: String, params: HashMap<String, String?>) =
        viewModelScope.launch {
            enableNotificationApiCall(authToken, params)
        }

    private suspend fun enableNotificationApiCall(
        authToken: String,
        params: HashMap<String, String?>
    ) {
        updateNotificationCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.enable_notification(authToken, params)
            updateNotificationCall.postValue(handlecheckenableNotificationApiCallResponse(response))
        } else {
            updateNotificationCall.postValue(Resource.Error(noInternetConnection))
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


    private val notificationCall = MutableLiveData<Resource<NotificationListResponse>>()
    val notificationCallResponse: LiveData<Resource<NotificationListResponse>> = notificationCall

    fun notificationApi(authToken: String) = viewModelScope.launch {
        notificationApiCall(authToken)
    }

    private suspend fun notificationApiCall(authToken: String) {
        notificationCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.notifications(authToken)
            notificationCall.postValue(handleResponse(response))
        } else {
            notificationCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleResponse(response: Response<NotificationListResponse>): Resource<NotificationListResponse>? {
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