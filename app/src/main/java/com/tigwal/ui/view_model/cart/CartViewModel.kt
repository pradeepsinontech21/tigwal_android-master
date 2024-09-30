package com.tigwal.ui.view_model.cart

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.cart_item.CartItemResponse
import com.tigwal.data.model.cart_item.UpdateCartItemResponse
import com.tigwal.data.model.listCart.CartListResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body

class CartViewModel(
    val application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var errorString = MutableLiveData<String>()
    var noInternetConnection: String = ""


    private val cartApiCall = MutableLiveData<Resource<CartListResponse>>()
    val cartApiCallResponse: LiveData<Resource<CartListResponse>> = cartApiCall

    fun cartApi(authToken: String) = viewModelScope.launch {
        cartApiCall(authToken)
    }

    private suspend fun cartApiCall(authToken: String) {
        cartApiCall.postValue(Resource.Loading())
        Log.e("Auth Token",authToken)
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.listCart(authToken)
            cartApiCall.postValue(handleResponse(response))
        } else {
            cartApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleResponse(response: Response<CartListResponse>): Resource<CartListResponse>? {
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


    // Delete Cart Api
    private val deleteCartApiCall = MutableLiveData<Resource<CartItemResponse>>()
    val deleteCartApiCallResponse: LiveData<Resource<CartItemResponse>> = deleteCartApiCall

    fun deleteCartApi(authToken: String, delete_id: String, @Body params1: RequestBody) = viewModelScope.launch {
        deleteCartApiCall(authToken, delete_id,params1)
    }

    private suspend fun deleteCartApiCall(authToken: String, delete_id: String, @Body params1: RequestBody) {
        deleteCartApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.deleteCart(authToken, delete_id,params1)
            deleteCartApiCall.postValue(handleDeleteCartResponse(response))
        } else {
            deleteCartApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleDeleteCartResponse(response: Response<CartItemResponse>): Resource<CartItemResponse>? {
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


    // Updated Cart Item Api
    private val updatedCartApiCall = MutableLiveData<Resource<UpdateCartItemResponse>>()
    val updatedCartApiResponse: LiveData<Resource<UpdateCartItemResponse>> = updatedCartApiCall

    fun updateCartApi(authToken: String, cart_item_id1: String, params: RequestBody) =
        viewModelScope.launch {
            updateCartApiCall(authToken, cart_item_id1, params)
        }

    private suspend fun updateCartApiCall(
        authToken: String,
        cart_item_id1: String,
        params: RequestBody
    ) {
        updatedCartApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.updateCart(authToken, cart_item_id1, params)

            updatedCartApiCall.postValue(handleUpdateCartResponse(response))
        } else {
            updatedCartApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleUpdateCartResponse(response: Response<UpdateCartItemResponse>): Resource<UpdateCartItemResponse>? {
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