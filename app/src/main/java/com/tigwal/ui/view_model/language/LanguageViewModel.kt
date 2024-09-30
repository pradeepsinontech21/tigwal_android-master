package com.tigwal.ui.view_model.language

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigwal.data.api.Resource
import com.tigwal.data.model.check_email_mobile_exist.CheckEmailMobileExitsResponse
import com.tigwal.data.model.login.LoginResponse
import com.tigwal.data.model.otp.SendOtpResponse
import com.tigwal.data.repository.AppRepository
import com.tigwal.utils.AppUtils

import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.HashMap

class LanguageViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var noInternetConnection: String = ""
}