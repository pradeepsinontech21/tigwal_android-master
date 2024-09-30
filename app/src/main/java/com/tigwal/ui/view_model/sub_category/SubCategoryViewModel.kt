package com.tigwal.ui.view_model.sub_category

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.getcategory.CategoryResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import retrofit2.Response

class SubCategoryViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {

    var mobile: String? = ""
    var noInternetConnection: String = ""

    private val getSubCategoryApiCall = MutableLiveData<Resource<CategoryResponse>>()
    val getSubCategoryApiResponse: LiveData<Resource<CategoryResponse>> = getSubCategoryApiCall

    // getSubCategoryApi api call
    fun getSubCategoryApi(authToken: String,subCategoryID: String) = viewModelScope.launch {
        getSubCategoryApiCalling(authToken,subCategoryID)
    }

    private suspend fun getSubCategoryApiCalling(authToken:String,subCategoryID:String) {
        getSubCategoryApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.getSubCategory(authToken,subCategoryID)
            getSubCategoryApiCall.postValue(handlecheckenableNotificationApiCallResponse(response))
        } else {
            getSubCategoryApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handlecheckenableNotificationApiCallResponse(response: Response<CategoryResponse>): Resource<CategoryResponse>? {
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