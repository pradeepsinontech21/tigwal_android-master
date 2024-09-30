package com.tigwal.ui.view_model.cancel_order

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.cancel_order.CancelOrderResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response

class CancelOrderViewModel(
    val application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var errorString = MutableLiveData<String>()
    var noInternetConnection: String = ""

    private val cancelOrderCall = MutableLiveData<Resource<CancelOrderResponse>>()
    val cancelOrderCallResponse: LiveData<Resource<CancelOrderResponse>> = cancelOrderCall

    fun cancelOrderApi(authToken: String, params: RequestBody) = viewModelScope.launch {
        cancelOrderApiCall(authToken, params)
    }

    private suspend fun cancelOrderApiCall(authToken: String, params: RequestBody) {
        cancelOrderCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.CancelOrder(authToken, params)
            cancelOrderCall.postValue(handleResponse(response))
        } else {
            cancelOrderCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleResponse(response: Response<CancelOrderResponse>): Resource<CancelOrderResponse>? {
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


//    private val refundOrderCall = MutableLiveData<Resource<RefundApiResponse>>()
//    val refundOrderCallResponse: LiveData<Resource<RefundApiResponse>> = refundOrderCall
//
//    fun refundOrderApi(authToken: String, params: RequestBody) = viewModelScope.launch {
//        refundOrderApiCall(authToken, params)
//    }
//
//    private suspend fun refundOrderApiCall(authToken: String, params: RequestBody) {
//        refundOrderCall.postValue(Resource.Loading())
//        if (AppUtils.hasInternetConnection(application)) {
//            val response = appRepository.refunds(authToken, params)
//            refundOrderCall.postValue(handleRefundResponse(response))
//        } else {
//            refundOrderCall.postValue(Resource.Error(noInternetConnection))
//        }
//    }
//
//    private fun handleRefundResponse(response: Response<RefundApiResponse>): Resource<RefundApiResponse>? {
//        if (response.isSuccessful) {
//            response.body()?.let { resultResponse ->
//                return Resource.Success(resultResponse)
//            }
//        } else if (response.errorBody() != null) {
//            AppUtils.getErrorMessage(response.errorBody()!!)?.let { it1 ->
//                return Resource.Error(it1)
//            }
//        }
//        return Resource.Error(response.message())
//    }

}