
package com.tigwal.ui.view_model.payment_option

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.tap_payment.TapPaymentResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body

class PaymentOptionViewModel(
    val application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var errorString = MutableLiveData<String>()
    var noInternetConnection: String = ""

    // Tap Payment Api
    private val tapPaymentApiCall = MutableLiveData<Resource<TapPaymentResponse>>()
    val tapPaymentApiResponse: LiveData<Resource<TapPaymentResponse>> = tapPaymentApiCall

    fun tapPaymentApi(authToken: String, cart_item_id1: String) = viewModelScope.launch {
        tapPaymentApiCall(authToken, cart_item_id1)
    }

    private suspend fun tapPaymentApiCall(authToken: String, charges_id1: String) {
        tapPaymentApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.getTapPayment(authToken, charges_id1,)
            tapPaymentApiCall.postValue(handleTapPaymentResponse(response))
        } else {
            tapPaymentApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleTapPaymentResponse(response: Response<TapPaymentResponse>): Resource<TapPaymentResponse>? {
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




    // Create Order Api
    private val createOrderApiCall = MutableLiveData<Resource<TapPaymentResponse>>()
    val  createOrderResponse: LiveData<Resource<TapPaymentResponse>> = createOrderApiCall

//    fun createOrderApi(authToken: String, params: RequestBody, @Body params1: RequestBody) = viewModelScope.launch {
//        createOrderApiCall(authToken, params,params1)
//    }

    fun createOrderApi(authToken: String, params: HashMap<String, RequestBody?>) = viewModelScope.launch {
        createOrderApiCall(authToken, params)
    }


//    private suspend fun createOrderApiCall(authToken: String, params: RequestBody, @Body params1: RequestBody) {
        private suspend fun createOrderApiCall(authToken: String, params: HashMap<String, RequestBody?>) {
        createOrderApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
//            val response = appRepository.createOrder(authToken,params,params1)
            val response = appRepository.createOrder(authToken,params)
            createOrderApiCall.postValue(handleCreateOrderResponse(response))
        } else {
            createOrderApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleCreateOrderResponse(response: Response<TapPaymentResponse>): Resource<TapPaymentResponse>? {
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