package com.tigwal.ui.view_model.search_productlist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.search.SearchListResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response

class SearchProductListViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var mobile: String? = ""
    var noInternetConnection: String = ""
    private val seachApiCall = MutableLiveData<Resource<SearchListResponse>>()
    val searchApiCallResponse: LiveData<Resource<SearchListResponse>> = seachApiCall

    fun searchApi(authToken: String, body: RequestBody) = viewModelScope.launch {
        searchApiCall(authToken, body)
    }

    private suspend fun searchApiCall(authToken: String, body: RequestBody) {
        seachApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.SearchLocation(
                authToken, body
            )
            seachApiCall.postValue(handleResponse(response))
        } else {
            seachApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleResponse(response: Response<SearchListResponse>): Resource<SearchListResponse>? {
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