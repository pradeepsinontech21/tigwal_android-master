package com.tigwal.ui.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tigwal.ui.view_model.login.LoginViewModel
import com.tigwal.data.repository.AppRepository

@Suppress("UNCHECKED_CAST")
class LoginViewFactory(
    private val application: Application,
    val appRepository: AppRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(application, appRepository) as T
    }
}