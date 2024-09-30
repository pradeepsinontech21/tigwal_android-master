package com.tigwal.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media.session.MediaButtonReceiver
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tigwal.R
import com.tigwal.app.rest.RestConstant
import com.tigwal.base.BaseActivity
import com.tigwal.data.api.Resource
import com.tigwal.data.api.UserParams
import com.tigwal.data.model.chat.ChatItem
import com.tigwal.databinding.ActivitySupportChatBinding
import com.tigwal.ui.adapter.ChatAdapter
import com.tigwal.ui.factory.SupportChatFactory
import com.tigwal.ui.view_model.support_chat.SupportChatViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.MySharedPreferences
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList

class SupportChatActivity : BaseActivity(), KodeinAware, View.OnClickListener {
    private lateinit var binding: ActivitySupportChatBinding
    private lateinit var viewModel: SupportChatViewModel
    override val kodein: Kodein by kodein()
    private val factory: SupportChatFactory by instance()
    var vendorID: String? = ""
    var imageVendor: String? = ""
    var data = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_support_chat
        )
        setupViewModel()
        setupObservers()
        getIntentData()

        clickListener()
        setFontTypeface()
        binding.toolBar.txtHeaderTitle.text = resources.getString(R.string.support_chat)
        setupObservers()
        isLoading = true
        listChat()
    }



    companion object {
        var isLoading: Boolean = false
        var isBroadCast: Boolean = false
    }

   class CustomBroadcastReceiver : BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?)
        {
            isBroadCast=true
        }
    }

    override fun onResume() {
        super.onResume()
        // Timer..

        Timer().scheduleAtFixedRate(object : TimerTask()
        {
            override fun run()
            {
                if(isBroadCast)
                {
                    isLoading=false
                    isBroadCast=false
                    listChat()
                }
            }
        }, 0, 1000)
    }

    fun listChat()
    {
        if (Connectivity.isConnected(activity))
        {
            val params = java.util.HashMap<String, String?>()
            params[UserParams.receiver_id] = vendorID
            viewModel.ChatDetailsCallApi(
                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                params
            )
        } else {
            Toast.makeText(
                activity,
                "" + resources.getString(R.string.noInternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getIntentData() {
        vendorID = intent.getStringExtra("VENDOR_ID")
        if (intent.getStringExtra("IMAGE_VENDOR") != null && !intent.getStringExtra("IMAGE_VENDOR")
                .equals("")
        ) {
            imageVendor = intent.getStringExtra("IMAGE_VENDOR")
        }
    }

    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)
        binding.imgSendMessage.setOnClickListener(this)
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.editSearch.typeface = AppUtils.getMIDIUM(activity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressed()
            }

            R.id.imgSendMessage -> {
                hideKeyboard(activity)
                if (binding.editSearch.text.toString().isEmpty()) {
                    Toast.makeText(
                        activity,
                        resources.getString(R.string.please_enter_message),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (Connectivity.isConnected(activity)) {
                        val params = java.util.HashMap<String, String?>()
                        params[UserParams.receiver_id] = vendorID
                        params[UserParams.message] = binding.editSearch.text.toString()
                        binding.editSearch.text!!.clear()
                        viewModel.SendChatCallApi(
                            RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                            params
                        )
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "" + resources.getString(R.string.noInternetConnection),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    //set view model
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, factory).get(SupportChatViewModel::class.java)
        binding.viewmodel = viewModel
    }

    //set observer
    private fun setupObservers() {
        viewModel.SendChatResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        Log.d(
                            "Response",
                            "====== SendChatResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { SendChatResponse ->
                            if (SendChatResponse.code == 200) {
                                if (SendChatResponse.status == true) {
                                    isLoading = false
                                    listChat()
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + SendChatResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + SendChatResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(
                            activity,
                            "" + response.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Resource.Loading -> {
                    }
                }
            })

        viewModel.ChatDetailsResponse.observe(
            this
        ) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.rlProgressView.visibility = View.GONE
                    Log.d(
                        "Response",
                        "====== ChatDetailsResponse ====> " + Gson().toJson(response)
                    )
                    response.data?.let { ChatDetailsResponse ->
                        if (ChatDetailsResponse.code == 200) {
                            if (ChatDetailsResponse.status == true) {
                                if (isLoading) {
                                    isLoading = false
                                }
                                if (ChatDetailsResponse.data != null && ChatDetailsResponse.data.chat!!.size > 0) {
                                    binding.layoutNotFound.llDataNotFoundView.visibility =
                                        View.GONE
                                    binding.rvChat.visibility = View.VISIBLE

                                    if (ChatDetailsResponse.data.userprofile!!.imageurl != null && !ChatDetailsResponse.data.userprofile!!.imageurl.equals(
                                            ""
                                        )
                                    ) {
                                        imageVendor =
                                            ChatDetailsResponse.data.userprofile!!.imageurl
                                    } else {
                                        imageVendor = ""
                                    }
                                    val adapter =
                                        ChatAdapter(
                                            ChatDetailsResponse.data.chat as ArrayList<ChatItem?>?,
                                            imageVendor
                                        )
                                    val mLayoutManager1: RecyclerView.LayoutManager =
                                        LinearLayoutManager(activity)
                                    binding.rvChat.itemAnimator = DefaultItemAnimator()
                                    binding.rvChat.layoutManager = mLayoutManager1
                                    binding.rvChat.adapter = adapter
                                    binding.rvChat.scrollToPosition(ChatDetailsResponse.data.chat.size - 1)
                                    binding.rvChat.setHasFixedSize(true);
                                } else {
                                    binding.layoutNotFound.llDataNotFoundView.visibility =
                                        View.VISIBLE
                                    binding.rvChat.visibility = View.GONE
                                    binding.layoutNotFound.txtDataNotFoundTitle.setText(
                                        resources.getString(R.string.chat_empty)
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + ChatDetailsResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                activity,
                                "" + ChatDetailsResponse.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                is Resource.Error -> {
                    binding.rlProgressView.visibility = View.GONE
                    Toast.makeText(
                        activity,
                        "" + response.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    if (isLoading) {
                        binding.rlProgressView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
//        LocalBroadcastManager.getInstance(activity)
//            .unregisterReceiver(mHandleMessageDeliverylive)

    }

//    var mHandleMessageDeliverylive: BroadcastReceiver = object : BroadcastReceiver()
//    {
//        override fun onReceive(context: Context, intent: Intent)
//        {
//            Log.d("BroadcastReceiver", "=========== Chat is refresh ============")
//            isLoading = false
//            listChat()
//        }
//    }
}