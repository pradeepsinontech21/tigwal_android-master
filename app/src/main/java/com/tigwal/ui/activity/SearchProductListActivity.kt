package com.tigwal.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.tigwal.data.api.UserParams
import com.tigwal.databinding.ActivitySearchproductListBinding
import com.tigwal.ui.adapter.SearchListAdapter
import com.tigwal.ui.factory.SearchProductListFactory
import com.tigwal.ui.view_model.search_productlist.SearchProductListViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.MySharedPreferences
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class SearchProductListActivity : BaseActivity(), KodeinAware, View.OnClickListener {
    private lateinit var binding: ActivitySearchproductListBinding
    private lateinit var viewModel: SearchProductListViewModel
    override val kodein: Kodein by kodein()
    private val factory: SearchProductListFactory by instance()
    var strLatitude: String = "0.0"
    var strLongitude: String = "0.0"
    var strSearch: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_searchproduct_list
        )
        setupViewModel()
        setupObservers()
        getIntentData()
        clickListener()
        setFontTypeface()
        binding.toolBar.txtHeaderTitle.setText(resources.getString(R.string.search))
        if (Connectivity.isConnected(activity)) {
            val params = HashMap<String, Any?>()
            params.put(UserParams.search, strSearch)
            params.put(UserParams.current_latitude, strLatitude)
            params.put(UserParams.current_longtitude, strLongitude)
            val body: RequestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                JSONObject(params).toString()
            )
            viewModel.searchApi(
                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                body
            )
        }
    }

    private fun productList(data: ArrayList<com.tigwal.data.model.search.DataItem?>) {
        if (data != null && data.size > 0) {
            binding.recyclerProductList.visibility = View.VISIBLE
            binding.layoutNotFound.llDataNotFoundView.visibility = View.GONE
            val adapter = SearchListAdapter(
                data
            )
            val mLayoutManager1: RecyclerView.LayoutManager =
                LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            binding.recyclerProductList.itemAnimator = DefaultItemAnimator()
            binding.recyclerProductList.layoutManager = mLayoutManager1
            binding.recyclerProductList.adapter = adapter
            adapter.setOnItemClickLister(object :
                SearchListAdapter.OnItemClickLister {
                override fun itemClicked(view: View?, position: Int) {
                    startActivity(
                        Intent(activity, ProductDetailActivity::class.java)
                            .putExtra("Location_ID", "" + data!!.get(position)!!.id)
                    )
                    AppUtils.startFromRightToLeft(activity)
                }
            })
        } else {
            binding.recyclerProductList.visibility = View.GONE
            binding.layoutNotFound.llDataNotFoundView.visibility = View.VISIBLE
            binding.layoutNotFound.txtDataNotFoundTitle.setText(resources.getString(R.string.search_empty))
        }
    }

    override fun getIntentData() {
        strLatitude = intent.getStringExtra("LATITUDE").toString()
        strLongitude = intent.getStringExtra("LONGITUDE").toString()
        strSearch = intent.getStringExtra("SEARCH").toString()
    }

    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)
        binding.toolBar.ivFilter.setOnClickListener(this)
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
        viewModel = ViewModelProvider(this, factory).get(SearchProductListViewModel::class.java)
        binding.viewmodel = viewModel
    }

    //set observer
    private fun setupObservers() {

        viewModel.searchApiCallResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()

                        Log.d(
                            "Response",
                            "====== searchApiCallResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { searchlistResponse ->
                            if (searchlistResponse.code == 200) {
                                if (searchlistResponse.status == true) {
                                    productList(searchlistResponse.data!!)
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + searchlistResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + searchlistResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    is Resource.Error -> {
                        hideProgressDialog()
                        Toast.makeText(
                            activity,
                            "" + response.message,
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