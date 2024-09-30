package com.tigwal.ui.view_model.order_summary

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.chat.ChatDetailsResponse
import com.tigwal.data.model.chat.SendChatResponse
import com.tigwal.data.model.order_detail.OrderDetailResponse
import com.tigwal.data.model.rating.RatingResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.HashMap

class OrderSummaryViewModel(
    val application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var errorString = MutableLiveData<String>()
    var noInternetConnection: String = ""


    private val orderDetailCall = MutableLiveData<Resource<OrderDetailResponse>>()
    val orderDetailCallResponse: LiveData<Resource<OrderDetailResponse>> = orderDetailCall

    fun orderDetailApi(authToken: String, orderId: String) = viewModelScope.launch {
        orderDetaiApiCall(authToken, orderId)
    }

    private suspend fun orderDetaiApiCall(authToken: String, orderId: String) {
        orderDetailCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.getOrder(authToken, orderId)
            orderDetailCall.postValue(handleResponse(response))
        } else {
            orderDetailCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleResponse(response: Response<OrderDetailResponse>): Resource<OrderDetailResponse>? {
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


    // ratingApiCall
    private val addRatingApiCall = MutableLiveData<Resource<RatingResponse>>()
    val addRatingApiCallResponse: LiveData<Resource<RatingResponse>> = addRatingApiCall

    fun ratingApiCall(authToken: String, params: HashMap<String, String?>) =
        viewModelScope.launch {
            addRatingApiCall(authToken, params)
        }

    private suspend fun addRatingApiCall(authToken: String, params: HashMap<String, String?>) {
        addRatingApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.addRating(authToken, params)
            addRatingApiCall.postValue(handleRatingCallResponse(response))
        } else {
            addRatingApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleRatingCallResponse(response: Response<RatingResponse>): Resource<RatingResponse>? {
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