package com.tigwal.ui.view_model.chatlist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.chatlist.ChatListResponse
import com.tigwal.data.model.product_listing.ProductListResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import retrofit2.Response

class ChatListViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var mobile: String? = ""
    var noInternetConnection: String = ""
    private val ChatListCall = MutableLiveData<Resource<ChatListResponse>>()
    val ChatListCallResponse: LiveData<Resource<ChatListResponse>> = ChatListCall

    fun ChatListApi(authToken: String) = viewModelScope.launch {
        ChatListApiCall(authToken)
    }

    private suspend fun ChatListApiCall(authToken: String) {
        ChatListCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.ChatList(
                authToken
            )
            ChatListCall.postValue(handleResponse(response))
        } else {
            ChatListCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleResponse(response: Response<ChatListResponse>): Resource<ChatListResponse>? {
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