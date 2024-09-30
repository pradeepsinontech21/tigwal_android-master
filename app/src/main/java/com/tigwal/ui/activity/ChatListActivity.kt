package com.tigwal.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tigwal.R
import com.tigwal.app.rest.RestConstant
import com.tigwal.base.BaseActivity
import com.tigwal.data.api.Resource
import com.tigwal.data.model.chatlist.DataItem
import com.tigwal.databinding.ActivityChatListBinding
import com.tigwal.ui.adapter.ChatListAdapter
import com.tigwal.ui.factory.ChatListFactory
import com.tigwal.ui.view_model.chatlist.ChatListViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.MySharedPreferences
import org.json.JSONObject
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class ChatListActivity : BaseActivity(), KodeinAware, View.OnClickListener {
    private lateinit var binding: ActivityChatListBinding
    private lateinit var viewModel: ChatListViewModel
    override val kodein: Kodein by kodein()
    private val factory: ChatListFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_chat_list
        )
        setupViewModel()
        setupObservers()
        getIntentData()
        clickListener()
        setFontTypeface()
        binding.toolBar.txtHeaderTitle.setText(resources.getString(R.string.support_chat))
        if (Connectivity.isConnected(activity)) {
            viewModel.ChatListApi(
                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken
            )
        } else {
            Toast.makeText(
                applicationContext,
                "" + resources.getString(R.string.noInternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun chatList(data: ArrayList<com.tigwal.data.model.chatlist.DataItem?>) {
        if (data != null && data.size > 0) {
            binding.recyclerChat.visibility = View.VISIBLE
            binding.layoutNotFound.llDataNotFoundView.visibility = View.GONE
            val adapter = ChatListAdapter(data)
            val mLayoutManager1: RecyclerView.LayoutManager =
                LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            binding.recyclerChat.itemAnimator = DefaultItemAnimator()
            binding.recyclerChat.layoutManager = mLayoutManager1
            binding.recyclerChat.adapter = adapter
        } else {
            binding.recyclerChat.visibility = View.GONE
            binding.layoutNotFound.llDataNotFoundView.visibility = View.VISIBLE
            binding.layoutNotFound.txtDataNotFoundTitle.setText(resources.getString(R.string.chat_empty))
        }
    }

    override fun getIntentData() {
    }

    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLDMIDIUM(activity)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressed()
            }
        }
    }

    //set view model
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, factory).get(ChatListViewModel::class.java)
        binding.viewmodel = viewModel
    }

    //set observer
    private fun setupObservers() {

        viewModel.ChatListCallResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()

                        Log.d(
                            "Response",
                            "====== ChatListCallResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { chatlistResponse ->
                            if (chatlistResponse.code == 200) {
                                if (chatlistResponse.status == true) {
                                    chatList(chatlistResponse.data!!)
                                } else {
                                    binding.recyclerChat.visibility = View.GONE
                                    binding.layoutNotFound.llDataNotFoundView.visibility =
                                        View.VISIBLE
                                    binding.layoutNotFound.txtDataNotFoundTitle.setText(
                                        resources.getString(
                                            R.string.chat_empty
                                        )
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + chatlistResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.recyclerChat.visibility = View.GONE
                                binding.layoutNotFound.llDataNotFoundView.visibility = View.VISIBLE
                                binding.layoutNotFound.txtDataNotFoundTitle.setText(
                                    resources.getString(
                                        R.string.chat_empty
                                    )
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        hideProgressDialog()
                        binding.recyclerChat.visibility = View.GONE
                        binding.layoutNotFound.llDataNotFoundView.visibility = View.VISIBLE
                        binding.layoutNotFound.txtDataNotFoundTitle.setText(resources.getString(R.string.product_list_empty))

                        var string = ""
                        try {
                            val jsonObject = JSONObject(response.toString())
                            if (jsonObject.getJSONObject("Data") != null) {
                            } else {
                                string = jsonObject.getString("message")
                            }
                        } catch (e: Exception) {
                            string = e.message.toString()
                        }
                        Toast.makeText(
                            activity,
                            "" + string,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Resource.Loading -> {
                        showProgressDialog(activity!!)
                    }
                }
            })
    }
}