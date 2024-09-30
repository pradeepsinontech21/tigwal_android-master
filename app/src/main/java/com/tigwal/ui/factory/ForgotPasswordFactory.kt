package com.tigwal.ui.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tigwal.ui.view_model.forgotpassword.ForgotPasswordViewModel
import com.tigwal.data.repository.AppRepository

class ForgotPasswordFactory (
    private val application: Application,
    val appRepository: AppRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ForgotPasswordViewModel(application, appRepository) as T
    }
}