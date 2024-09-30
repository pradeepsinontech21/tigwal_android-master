package com.tigwal.ui.view_model.viewallcategory

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

class ViewAllCategoryViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var mobile: String? = ""
    var noInternetConnection: String = ""

    private val getCategoryApiCall = MutableLiveData<Resource<CategoryResponse>>()
    val getCategoryApiResponse: LiveData<Resource<CategoryResponse>> = getCategoryApiCall

    // getCategoryApi api call
    fun getCategoryApi(authToken: String) = viewModelScope.launch {
        getCategoryApiCalling(authToken)
    }

    private suspend fun getCategoryApiCalling(authToken: String) {
        getCategoryApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.getCategory(authToken)
            getCategoryApiCall.postValue(handlecheckenableNotificationApiCallResponse(response))
        } else {
            getCategoryApiCall.postValue(Resource.Error(noInternetConnection))
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