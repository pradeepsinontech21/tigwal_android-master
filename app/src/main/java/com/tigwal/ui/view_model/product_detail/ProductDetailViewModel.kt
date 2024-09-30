package com.tigwal.ui.view_model.product_detail

import android.app.Activity
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.addtocart.AddToCartResponse
import com.tigwal.data.model.getTimeSlot.GetTimeSlotResposne
import com.tigwal.data.model.product_detail.ProductDetailResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response

class ProductDetailViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var mobile: String? = ""
    var noInternetConnection: String = ""

    private val productDetailCall = MutableLiveData<Resource<ProductDetailResponse>>()
    val productDetailCallResponse: LiveData<Resource<ProductDetailResponse>> = productDetailCall

    fun productDetailApi(authToken: String, strSlug: String) = viewModelScope.launch {
        productDetailApiCall(authToken, strSlug)
    }

    private suspend fun productDetailApiCall(authToken: String, locationId: String) {
        productDetailCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.get_location_details(authToken, locationId)
            productDetailCall.postValue(handleResponse(response))
        } else {
            productDetailCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleResponse(response: Response<ProductDetailResponse>): Resource<ProductDetailResponse>? {
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


    private val addToCarCall = MutableLiveData<Resource<AddToCartResponse>>()
    val addToCartCallResponse: LiveData<Resource<AddToCartResponse>> = addToCarCall

    fun addToCartApi(activity: Activity, authToken: String, params1: HashMap<String, String?>) =
        viewModelScope.launch {
            addToCartApiCall(activity, authToken, params1)
        }

    private suspend fun addToCartApiCall(
        activity: Activity,
        authToken: String,
        params1: HashMap<String, String?>
    ) {
        addToCarCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.addCart(authToken, params1)
            addToCarCall.postValue(handleAddToCartResponse(activity, response))
        } else {
            addToCarCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleAddToCartResponse(
        activity: Activity,
        response: Response<AddToCartResponse>
    ): Resource<AddToCartResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        } else if (response.errorBody() != null) {
//            AppUtils.getErrorMessage(response.errorBody()!!)?.let { it1 ->
//                return Resource.Error(it1)
//            }
            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
            Toast.makeText(
                activity,
                "" + jsonObj.getString("message"),
                Toast.LENGTH_SHORT
            ).show()
        }
        return Resource.Error(response.message())
    }


    private val getTimeSlotsCall = MutableLiveData<Resource<GetTimeSlotResposne>>()
    val getTimeSlotCallResponse: LiveData<Resource<GetTimeSlotResposne>> = getTimeSlotsCall

    fun getTimeSlotApi(authToken: String, params: RequestBody) = viewModelScope.launch {
        getTimeSlotApiCall(authToken, params)
    }

    private suspend fun getTimeSlotApiCall(authToken: String, params: RequestBody) {
        getTimeSlotsCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.getSlots(authToken, params)
            getTimeSlotsCall.postValue(handleTimeSlotResponse(response))
        } else {
            getTimeSlotsCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleTimeSlotResponse(response: Response<GetTimeSlotResposne>): Resource<GetTimeSlotResposne>? {
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