package com.tigwal.ui.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tigwal.data.repository.AppRepository
import com.tigwal.ui.view_model.ongoingorder.OnGoingOrderViewModel


@Suppress("UNCHECKED_CAST")
class OnGoingFactory(
    private val application: Application,
    val appRepository: AppRepository
) : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OnGoingOrderViewModel(application,appRepository) as T
    }
}