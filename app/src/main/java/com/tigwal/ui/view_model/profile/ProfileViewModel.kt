package com.tigwal.ui.view_model.profile

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.app.rest.RestConstant
import com.tigwal.data.api.Resource
import com.tigwal.data.api.UserParams
import com.tigwal.data.model.DeleteUserResponse
import com.tigwal.data.model.chatlist.ChatListResponse
import com.tigwal.data.model.login.LoginResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class ProfileViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var errorString = MutableLiveData<String>()
    var noInternetConnection: String = ""

    private val profileApiCall = MutableLiveData<Resource<LoginResponse>>()
    val profileApiResponse: LiveData<Resource<LoginResponse>> = profileApiCall

    // getProfileAPi Login api call
    fun getProfileAPi(strToken: String) = viewModelScope.launch {
        getProfileAPiCall(strToken)
    }

    private suspend fun getProfileAPiCall(strToken: String) {
        profileApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.referesh_user(RestConstant.BEARER + strToken)
            profileApiCall.postValue(handleProfileResponse(response))
        } else {
            profileApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleProfileResponse(response: Response<LoginResponse>): Resource<LoginResponse>? {
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


    private val updateProfileApiCall = MutableLiveData<Resource<LoginResponse>>()
    val updateProfileApiResponse: LiveData<Resource<LoginResponse>> = updateProfileApiCall

    // Update Profile api call
    fun updateProfileAPi(
        strToken: String,
        params: HashMap<String, RequestBody?>,
        picturePath: String?,
    ) = viewModelScope.launch {

        var body: MultipartBody.Part? = null
        if (picturePath!!.isNotEmpty()) {
            val file = File(picturePath)
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            body = MultipartBody.Part.createFormData(UserParams.profile, file.name, requestFile)
        }
        updateProfileAPiCall(strToken, params, body)
    }

    private suspend fun updateProfileAPiCall(
        strToken: String,
        params: HashMap<String, RequestBody?>,
        parts: MultipartBody.Part?
    ) {
        profileApiCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response =
                appRepository.update_profile(RestConstant.BEARER + strToken, params, parts)
            profileApiCall.postValue(handleUpdateProfileResponse(response))
        } else {
            profileApiCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleUpdateProfileResponse(response: Response<LoginResponse>): Resource<LoginResponse>? {
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


    private val deleteUserCall = MutableLiveData<Resource<DeleteUserResponse>>()
    val deleteUserApiCallResponse: LiveData<Resource<DeleteUserResponse>> = deleteUserCall

    fun deleteUserApi(authToken: String) = viewModelScope.launch {
        deleteUserApiCall(authToken)
    }

    private suspend fun deleteUserApiCall(authToken: String) {
        deleteUserCall.postValue(Resource.Loading())
        if (AppUtils.hasInternetConnection(application)) {
            val response = appRepository.DeleteUser(
                authToken
            )
            deleteUserCall.postValue(handleResponse(response))
        } else {
            deleteUserCall.postValue(Resource.Error(noInternetConnection))
        }
    }

    private fun handleResponse(response: Response<DeleteUserResponse>): Resource<DeleteUserResponse>? {
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