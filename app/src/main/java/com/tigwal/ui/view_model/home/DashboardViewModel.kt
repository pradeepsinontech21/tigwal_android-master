package com.tigwal.ui.view_model.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.banners.BannersResponse
import com.tigwal.data.model.getcategory.CategoryResponse
import com.tigwal.data.model.recommendation.RecommendationResponse
import com.tigwal.data.model.splash.AppInstallationResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.HashMap

class DashboardViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var errorString = MutableLiveData<String>()
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


    private val bannerApiCall = MutableLiveData<Resource<BannersResponse>>()
    val bannerApiResponse: LiveData<Resource<BannersResponse>> = bannerApiCall

    // bannerApi api call
    fun bannerApi(authToken: String) = viewModelScope.launch {
        bannerApiCalling(authToken)
    }

    private suspend fun bannerApiCalling(authToken: String) {
        bannerApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.get_banners(authToken)
            bannerApiCall.postValue(handlecheckenableBannerResponse(response))
        } else {
            bannerApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handlecheckenableBannerResponse(response: Response<BannersResponse>): Resource<BannersResponse>? {
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


    private val recommendationApiCall = MutableLiveData<Resource<RecommendationResponse>>()
    val recommendationApiResponse: LiveData<Resource<RecommendationResponse>> =
        recommendationApiCall

    // recommendationApiCall api call
    fun recommendationApi(authToken: String) = viewModelScope.launch {
        recommendationApiCalling(authToken)
    }

    private suspend fun recommendationApiCalling(authToken: String) {
        recommendationApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.RecommendsList(authToken)
            recommendationApiCall.postValue(handlecheckenableRecommendResponse(response))
        } else {
            recommendationApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handlecheckenableRecommendResponse(response: Response<RecommendationResponse>): Resource<RecommendationResponse>? {
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


    private val appInstallationApiCall = MutableLiveData<Resource<AppInstallationResponse>>()
    val appInstallationApiResponse: LiveData<Resource<AppInstallationResponse>> = appInstallationApiCall


    // appInstallationApi call
    fun appInstallationApi(params: HashMap<String, String?>) = viewModelScope.launch {
        appInstallationApiCall(params)
    }

    private suspend fun appInstallationApiCall(params: HashMap<String, String?>) {
        appInstallationApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.appInstallation(params)
            appInstallationApiCall.postValue(handleResponse(response))
        } else {
            appInstallationApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleResponse(response: Response<AppInstallationResponse>): Resource<AppInstallationResponse>? {
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