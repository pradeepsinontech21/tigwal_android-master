package com.tigwal.ui.activity

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.tigwal.BuildConfig
import com.tigwal.R
import com.tigwal.app.rest.RestConstant
import com.tigwal.base.BaseActivity
import com.tigwal.data.api.Resource
import com.tigwal.data.api.UserParams
import com.tigwal.databinding.ActivityDashboardBinding
import com.tigwal.ui.factory.DashboardViewFactory
import com.tigwal.ui.fragment.*
import com.tigwal.ui.view_model.home.DashboardViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.MySharedPreferences
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

class DashboardActivity : BaseActivity(), View.OnClickListener, KodeinAware {
    private lateinit var mBindingHomeActivity: ActivityDashboardBinding
    private val factory: DashboardViewFactory by instance()
    private lateinit var viewmodel: DashboardViewModel
    override val kodein: Kodein by kodein()
    var flag = ""
    var deviceUDID: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingHomeActivity = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_dashboard
        )
        if (MySharedPreferences.getMySharedPreferences()!!.locationID != "") {
            startActivity(
                Intent(activity, ProductDetailActivity::class.java)
                    .putExtra(
                        "Location_ID",
                        MySharedPreferences.getMySharedPreferences()!!.locationID
                    )
            )
            AppUtils.startFromRightToLeft(activity!!)
        }

        deviceUDID =
            Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID)
        MySharedPreferences.getMySharedPreferences()?.deviceUDID = deviceUDID
        getIntentData()
        clickListener()
        setupViewModel()
        setupObservers()

//        val filter1 = IntentFilter("NOTIFICATION_RECEIVER_FIRE_HOME")
//        LocalBroadcastManager.getInstance(activity)
//            .registerReceiver(mHandleMessageDeliverylive, filter1)

        if (intent.action != null && intent.action.equals(RestConstant.FIREBASE_PUSH_NOTIFICATION))
        {

            if (intent.getStringExtra("type").equals("chat"))
            {
                loadHomeFragment() // Inittial Load
                startActivity(
                    Intent(activity, SupportChatActivity::class.java)
                        .putExtra("VENDOR_ID", "" + intent.getStringExtra("sender_id"))
                        .putExtra("IMAGE_VENDOR", "")
                )
                AppUtils.startFromRightToLeft(activity)
            } else {
                loadHomeFragment() // Inittial Load
                pushFragment(NotificationFragment(), "")
            }
            Log.e("DashboardActivity", "remoteMessage:noti--> " + intent.getStringExtra("title"))
        } else {
            loadHomeFragment()
        }
        setFontTypeface()
        mBindingHomeActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        if (!MySharedPreferences.getMySharedPreferences()?.userId.equals("")) {
            mBindingHomeActivity.llBottomView.visibility = View.VISIBLE
        } else {
            mBindingHomeActivity.llBottomView.visibility = View.GONE
        }
    }

    override fun getIntentData() {

    }

    fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }

    override fun clickListener() {
        mBindingHomeActivity.bottomMenuBar.llHome.setOnClickListener(this)
        mBindingHomeActivity.bottomMenuBar.OnGoingOrder.setOnClickListener(this)
        mBindingHomeActivity.bottomMenuBar.llCart.setOnClickListener(this)
        mBindingHomeActivity.bottomMenuBar.llSetting.setOnClickListener(this)
        mBindingHomeActivity.bottomMenuBar.llNotication.setOnClickListener(this)
        mBindingHomeActivity.appBarHome.imgClose.setOnClickListener(this)
        mBindingHomeActivity.appBarHome.txtApplyFilter.setOnClickListener(this)
        mBindingHomeActivity.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })
    }

    override fun setFontTypeface() {
        mBindingHomeActivity.bottomMenuBar.tvHome.typeface = AppUtils.getMIDIUM(activity)
        mBindingHomeActivity.bottomMenuBar.tvCart.typeface = AppUtils.getMIDIUM(activity)
        mBindingHomeActivity.bottomMenuBar.tvNotification.typeface = AppUtils.getMIDIUM(activity)
        mBindingHomeActivity.bottomMenuBar.tvSetting.typeface = AppUtils.getMIDIUM(activity)
    }

    //set view model
    private fun setupViewModel() {
        viewmodel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)
        mBindingHomeActivity.viewmodel = viewmodel

    }

    //set observer
    private fun setupObservers() {
        // Do coding


        viewmodel.appInstallationApiResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        Log.d(
                            "Response",
                            "====== appinstallationResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { appinstallationResponse ->

                        }
                    }
                    is Resource.Error -> {
                    }
                    is Resource.Loading -> {
                    }
                }
            })
    }


    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.img_back -> {
                // No use
            }

            R.id.imgClose -> {
                mBindingHomeActivity.drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.txtApplyFilter -> {
                mBindingHomeActivity.drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.llSetting -> {
                pushFragment(
                    SettingFragment(), ""
                )
            }

            R.id.llNotication -> {
                pushFragment(
                    NotificationFragment(), ""
                )
            }

            R.id.llHome -> {
                pushFragment(DashboardFragment(), "")
            }
            R.id.OnGoingOrder -> {
                pushFragment(
                    OnGoingOrderFragment(), ""
                )
            }

            R.id.llCart -> {
                RestConstant.ContinueShopping = "0"
                pushFragment(
                    CartFragment(),
                    ""
                )
            }
        }
    }


    // onBackPressed in fragment
    override fun onBackPressed() {
        val f = supportFragmentManager.findFragmentById(R.id.frame)
        Log.d("Onbackpress", "=== Fragement Load =======> " + f)
        if (f is DashboardFragment) {
            dialogeCloseApp()
        } else {
            super.onBackPressed()
        }
    }

    private fun dialogeCloseApp() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialoge_custom_two_button)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = lp
        val txtNo: AppCompatTextView = dialog.findViewById(R.id.txtNo)
        val txtYes: AppCompatTextView = dialog.findViewById(R.id.txtYes)
        val txtMessage: AppCompatTextView = dialog.findViewById(R.id.txtMessage)
        txtMessage.text = resources.getString(R.string.are_you_sure_you_want_exit)
        txtYes.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        txtNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun loadHomeFragment() {
        pushFragment(
            DashboardFragment(), ""
        )
    }

//    var mHandleMessageDeliverylive: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            Log.d("BroadcastReceiver", "=========== Chat is refresh ============ ")
//
//            Toast.makeText(
//                activity,
//                "BroadcastReceiver" ,
//                Toast.LENGTH_SHORT
//            ).show()
//
//        }
//    }

    class CustomBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // do work

            Toast.makeText(
                context,
                "Custome BroadcastReceiver",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // clearAppNotifications();
//        LocalBroadcastManager.getInstance(activity).registerReceiver(
//            mHandleMessageDeliverylive,
//            IntentFilter("NOTIFICATION_RECEIVER_FIRE_HOME")
//        )

        if (Connectivity.isConnected(activity)) {
            val params = HashMap<String, String?>()
            params[UserParams.app_name] = resources.getString(R.string.app_name)
            params[UserParams.app_version] = BuildConfig.VERSION_NAME
            params[UserParams.device_token] =
                MySharedPreferences.getMySharedPreferences()!!.deviceToken
            params[UserParams.device_type] = RestConstant.DEVICE_TYPE
            params[UserParams.fcm_token] = "" //As blank
            params[UserParams.apns_token] = "" //As blank
            params[UserParams.app_identifier] = deviceUDID
            params[UserParams.timezone] = TimeZone.getDefault().id.toString()  // Local.timezone
            params[UserParams.user_id] = MySharedPreferences.getMySharedPreferences()!!.userId
            viewmodel.appInstallationApi(params)
        } else {
            Toast.makeText(
                activity,
                "" + resources.getString(R.string.noInternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onPause() {
        super.onPause()
//        LocalBroadcastManager.getInstance(activity)
//            .unregisterReceiver(mHandleMessageDeliverylive)

    }

}