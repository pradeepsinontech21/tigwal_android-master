package com.tigwal.ui.view_model.cmsscreen

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.cms_page.CMSResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import retrofit2.Response

class CMSSCreenViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {

    var mobile: String? = ""
    var noInternetConnection: String = ""

    private val cmsPageCall = MutableLiveData<Resource<CMSResponse>>()
    val cmsPageCallResponse: LiveData<Resource<CMSResponse>> = cmsPageCall

    fun cmsPagesApi(authToken: String,strSlug:String) = viewModelScope.launch {
        cmsPagesApiCall(authToken,strSlug)
    }

    private suspend fun cmsPagesApiCall(authToken:String ,strSlug:String) {
        cmsPageCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.get_cms_pages(authToken,strSlug)
            cmsPageCall.postValue(handlecheckCMSApiCallResponse(response))
        } else {
            cmsPageCall.postValue(Resource.Error(noInternetConnection))
        }
    }
    private fun handlecheckCMSApiCallResponse(response: Response<CMSResponse>): Resource<CMSResponse>? {
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