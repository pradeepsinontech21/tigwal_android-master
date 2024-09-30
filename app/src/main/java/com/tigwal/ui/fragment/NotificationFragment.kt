package com.tigwal.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tigwal.R
import com.tigwal.app.rest.RestConstant
import com.tigwal.base.BaseFragment
import com.tigwal.data.api.Resource
import com.tigwal.data.api.UserParams
import com.tigwal.data.model.notification_list.DataItem
import com.tigwal.databinding.FragmentNotificationBinding
import com.tigwal.ui.activity.LanguageActivity
import com.tigwal.ui.adapter.NotificationAdapter
import com.tigwal.ui.factory.NotificationViewFactory
import com.tigwal.ui.view_model.notification.NotificationViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.MySharedPreferences
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


@SuppressLint("UseRequireInsteadOfGet")
class NotificationFragment : BaseFragment(), KodeinAware, View.OnClickListener {
    private val factory: NotificationViewFactory by instance()
    private lateinit var notificationViewMode: NotificationViewModel
    private lateinit var binding: FragmentNotificationBinding
    override val kodein: Kodein by closestKodein()
    private var arrayListNotification: ArrayList<DataItem?>? = null
    lateinit var  imageUri: Uri

    companion object {
    }

    override fun getIntentData() {

    }


    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)
        binding.toolBar.switchNotification.setOnClickListener(this)
//        binding.clickBtn.setOnClickListener(this)
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLD(requireActivity())
    }

    @SuppressLint("Range")
    override fun onCreateFragmentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)

        setupViewModel()
        setupObservers()
        getIntentData()
        clickListener()
        binding.toolBar.ivBack.visibility = View.GONE
        binding.toolBar.switchNotification.visibility = View.VISIBLE
        binding.toolBar.txtHeaderTitle.text = resources.getString(R.string.notification)

        if (Connectivity.isConnected(activity)) {
            notificationViewMode.notificationApi(
                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken
            )
        } else {
            Toast.makeText(
                activity,
                "" + resources.getString(R.string.noInternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }

        if (MySharedPreferences.getMySharedPreferences()!!.isNotification.equals("1")) {
            binding.toolBar.switchNotification.isChecked = true
        } else {
            binding.toolBar.switchNotification.isChecked = false
        }

        return binding.root
    }

    //click
    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.iv_back -> {
                if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                    requireActivity().supportFragmentManager.popBackStack();
                }
            }


            R.id.switchNotification -> {
                val params = java.util.HashMap<String, String?>()
                Log.e(
                    "TAG",
                    "onCreateFragmentView:  isChecked" + binding.toolBar.switchNotification.isChecked
                )
                if (binding.toolBar.switchNotification.isChecked) {
                    params[UserParams.enable_notification] = "1"
                } else {
                    params[UserParams.enable_notification] = "0"
                }
                MySharedPreferences.getMySharedPreferences()!!.isNotification =
                    params[UserParams.enable_notification]
                notificationViewMode.enableNotificationApi(
                    RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                    params
                )
            }
        }
    }

    //set view model
    private fun setupViewModel() {
        notificationViewMode =
            ViewModelProvider(this, factory).get(NotificationViewModel::class.java)
        binding.viewmodel = notificationViewMode
    }


    //set observer for currency
    private fun setupObservers() {
        notificationViewMode.updateNotificationResponse.observe(
            this
        ) { response ->
            when (response) {
                is Resource.Success -> {
//                        (activity as DashboardActivity?)!!.rlProgressView.visibility = View.GONE
                    Log.d(
                        "Response",
                        "====== updateNotificationResponse ====> " + Gson().toJson(response)
                    )
                    response.data?.let { logoutResponse ->
                        if (logoutResponse.code == 200) {
                            if (logoutResponse.status == true) {
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + logoutResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                activity,
                                "" + logoutResponse.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                is Resource.Error -> {
//                        (activity as DashboardActivity?)!!.rlProgressView.visibility = View.GONE
                    Toast.makeText(
                        activity,
                        "" + response.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Resource.Loading -> {
//                        (activity as DashboardActivity?)!!.rlProgressView.visibility = View.VISIBLE
                }
            }
        }


        notificationViewMode.notificationCallResponse.observe(
            this
        ) { response ->
            when (response) {
                is Resource.Success -> {
//                        (activity as DashboardActivity?)!!.rlProgressView.visibility = View.GONE
                    Log.d(
                        "Response",
                        "====== notificationCallResponse ====> " + Gson().toJson(response)
                    )
                    response.data?.let { notificationCallResponse ->
                        if (notificationCallResponse.code == 200) {
                            if (notificationCallResponse.status == true) {
                                if (notificationCallResponse.data != null && notificationCallResponse.data.size > 0) {
                                    binding.recyclerNotification.visibility = View.VISIBLE
                                    binding.layoutNotFound.llDataNotFoundView.visibility =
                                        View.GONE
                                    ///        arrayListNotification!!.clear()
                                    arrayListNotification = notificationCallResponse.data
                                    val adapter = NotificationAdapter(
                                        arrayListNotification
                                    )
                                    val mLayoutManager1: RecyclerView.LayoutManager =
                                        LinearLayoutManager(
                                            activity,
                                            RecyclerView.VERTICAL,
                                            false
                                        )
                                    binding.recyclerNotification.itemAnimator =
                                        DefaultItemAnimator()
                                    binding.recyclerNotification.layoutManager = mLayoutManager1
                                    binding.recyclerNotification.adapter = adapter
                                } else {
                                    binding.recyclerNotification.visibility = View.GONE
                                    binding.layoutNotFound.llDataNotFoundView.visibility =
                                        View.VISIBLE
                                    binding.layoutNotFound.txtDataNotFoundTitle.setText(
                                        resources.getString(R.string.notification_empty)
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + notificationCallResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                activity,
                                "" + notificationCallResponse.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                is Resource.Error -> {
//                        (activity as DashboardActivity?)!!.rlProgressView.visibility = View.GONE
                    Toast.makeText(
                        activity,
                        "" + response.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Resource.Loading -> {
//                        (activity as DashboardActivity?)!!.rlProgressView.visibility = View.VISIBLE
                }
            }
        }
    }
}