package com.tigwal.ui.view_model.addnewcard

import android.app.Application
import androidx.lifecycle.ViewModel
import com.tigwal.data.repository.AppRepository

class AddNewCardViewModel(
    var application: Application,
    private var appRepository: AppRepository
) : ViewModel() {
    var mobile: String? = ""
    var noInternetConnection: String = ""
}