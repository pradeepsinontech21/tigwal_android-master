package com.tigwal.ui.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tigwal.data.repository.AppRepository
import com.tigwal.ui.view_model.cmsscreen.CMSSCreenViewModel

class CMSScreenFactory (
    private val application: Application,
    val appRepository: AppRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CMSSCreenViewModel(application, appRepository) as T
    }
}