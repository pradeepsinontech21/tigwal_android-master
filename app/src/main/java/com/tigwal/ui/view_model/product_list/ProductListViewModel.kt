package com.tigwal.ui.view_model.product_list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.product_listing.ProductListResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import retrofit2.Response

class ProductListViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var mobile: String? = ""
    var noInternetConnection: String = ""
    private val productListCall = MutableLiveData<Resource<ProductListResponse>>()
    val productListCallResponse: LiveData<Resource<ProductListResponse>> = productListCall

    fun productListApi(
        authToken: String,
        subCategoyID: String?,
        vendor_id: String?,
        price_order_By: String?,
        totalPage: Int,
        pageNumber: Int
    ) = viewModelScope.launch {
        productListApiCall(authToken, subCategoyID,vendor_id, price_order_By, totalPage, pageNumber)
    }

    private suspend fun productListApiCall(
        authToken: String,
        subCategoyID: String?,
        vendor_id: String?,
        price_order_By: String?,
        totalPage: Int,
        pageNumber: Int
    ) {
        productListCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.get_category_locations(
                authToken,
                subCategoyID.toString(),
                vendor_id.toString(),
                price_order_By.toString(),
                totalPage.toString(),
                pageNumber.toString()
            )
            productListCall.postValue(handleResponse(response))
        } else {
            productListCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleResponse(response: Response<ProductListResponse>): Resource<ProductListResponse>? {
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