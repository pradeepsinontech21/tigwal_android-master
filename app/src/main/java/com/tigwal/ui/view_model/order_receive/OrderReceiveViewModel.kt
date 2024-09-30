package com.tigwal.ui.view_model.order_receive

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tigwal.data.repository.AppRepository

class OrderReceiveViewModel(
    val application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var errorString = MutableLiveData<String>()
    var noInternetConnection: String = ""
}