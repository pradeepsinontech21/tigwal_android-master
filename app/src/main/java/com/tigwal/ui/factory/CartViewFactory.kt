package com.tigwal.ui.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tigwal.data.repository.AppRepository
import com.tigwal.ui.view_model.cart.CartViewModel


@Suppress("UNCHECKED_CAST")
class CartViewFactory(
    private val application: Application,
    val appRepository: AppRepository
) : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CartViewModel(application,appRepository) as T
    }
}