package com.tigwal.ui.view_model.support_chat

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.chat.ChatDetailsResponse
import com.tigwal.data.model.chat.SendChatResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.HashMap

class SupportChatViewModel(
    val application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var errorString = MutableLiveData<String>()
    var noInternetConnection: String = ""

    private val SendChatCall = MutableLiveData<Resource<SendChatResponse>>()
    val SendChatResponse: LiveData<Resource<SendChatResponse>> = SendChatCall

    // SendChat call
    fun SendChatCallApi(authToken: String, params: HashMap<String, String?>) =
        viewModelScope.launch {
            SendChatCallApiCall(authToken, params)
        }

    private suspend fun SendChatCallApiCall(authToken: String, params: HashMap<String, String?>) {
        SendChatCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.SendChat(authToken, params)
            SendChatCall.postValue(handleSendChatCallResponse(response))
        } else {
            SendChatCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleSendChatCallResponse(response: Response<SendChatResponse>): Resource<SendChatResponse>? {
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




    private val ChatDetailsCall = MutableLiveData<Resource<ChatDetailsResponse>>()
    val ChatDetailsResponse: LiveData<Resource<ChatDetailsResponse>> = ChatDetailsCall

    // ChatDetails call
    fun ChatDetailsCallApi(authToken: String, params: HashMap<String, String?>) =
        viewModelScope.launch {
            ChatDetailsApiCall(authToken, params)
        }

    private suspend fun ChatDetailsApiCall(authToken: String, params: HashMap<String, String?>) {
        ChatDetailsCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.ChatDetails(authToken, params)
            ChatDetailsCall.postValue(handleChatDetailsResponse(response))
        } else {
            ChatDetailsCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleChatDetailsResponse(response: Response<ChatDetailsResponse>): Resource<ChatDetailsResponse>? {
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