package com.tigwal.ui.view_model.ongoingorder

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.listOrder.ListOrderResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import retrofit2.Response

class OnGoingOrderViewModel(
    val application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var errorString = MutableLiveData<String>()
    var noInternetConnection: String = ""


    private val listOrderListCall = MutableLiveData<Resource<ListOrderResponse>>()
    val listOrderCallResponse: MutableLiveData<Resource<ListOrderResponse>> = listOrderListCall

    fun listOrderApi(authToken: String) = viewModelScope.launch {
        listOrderApiCall(authToken)
    }

    private suspend fun listOrderApiCall(authToken: String) {
        listOrderListCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.listOrder(authToken)
            listOrderListCall.postValue(handleResponse(response))
        } else {
            listOrderListCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleResponse(response: Response<ListOrderResponse>): Resource<ListOrderResponse>? {
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