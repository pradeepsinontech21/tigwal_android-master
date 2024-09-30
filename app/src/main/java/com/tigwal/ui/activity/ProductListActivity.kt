package com.tigwal.ui.activity

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
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
import com.tigwal.data.model.product_listing.DataItem
import com.tigwal.data.model.product_listing.ProductListResponse
import com.tigwal.databinding.ActivityProductListBinding
import com.tigwal.ui.adapter.ProductListAdapter
import com.tigwal.ui.factory.ProductListFactory
import com.tigwal.ui.view_model.product_list.ProductListViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.MySharedPreferences
import org.json.JSONObject
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class ProductListActivity : BaseActivity(), KodeinAware, View.OnClickListener {
    private lateinit var binding: ActivityProductListBinding
    private lateinit var viewModel: ProductListViewModel
    override val kodein: Kodein by kodein()
    private val factory: ProductListFactory by instance()
    var subCategoryID: String? = ""
    var vendorID: String? = ""
    var isLoading: Boolean = false
    private var pageNumber = 1
    private var totalApiPages = 1
    var totalPage = 5
    var adapter: ProductListAdapter? = null
    var mLayoutManager1: LinearLayoutManager? = null
    var arrayList: java.util.ArrayList<DataItem> = java.util.ArrayList<DataItem>()
    private var price_order_By = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_product_list
        )
        setupViewModel()
        setupObservers()
        getIntentData()
        clickListener()
        setFontTypeface()
        binding.toolBar.txtHeaderTitle.setText(resources.getString(R.string.product_list))

        binding.nestedScroview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                if (isLoading) {
                    isLoading = false
                }
                pageNumber++
                if (pageNumber < totalApiPages) {
                    productListApiCall()
                }
            }
        })
        isLoading = true
        productListApiCall()
    }

    override fun onResume() {
        super.onResume()
        RestConstant.productFilter = ""
    }

    private fun productListApiCall() {
        if (Connectivity.isConnected(activity)) {
            viewModel.productListApi(
                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                subCategoryID, vendorID, price_order_By, totalPage, pageNumber
            )
        } else {
            Toast.makeText(
                applicationContext,
                "" + resources.getString(R.string.noInternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun productList(data: ArrayList<DataItem>, productlistResponse: ProductListResponse) {
        if (data != null && data.size > 0)
        {
            binding.recyclerProductList.visibility = View.VISIBLE
            binding.toolBar.ivFilter.visibility = View.VISIBLE
            binding.layoutNotFound.llDataNotFoundView.visibility = View.GONE

            if (arrayList.isEmpty()) {
                totalApiPages = productlistResponse.data!!.total!! / totalPage
                totalApiPages = totalApiPages + 1
                arrayList!!.addAll(data)
                adapter = ProductListAdapter(arrayList)
                mLayoutManager1 = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                binding.recyclerProductList.itemAnimator = DefaultItemAnimator()
                binding.recyclerProductList.layoutManager = mLayoutManager1
                binding.recyclerProductList.adapter = adapter
            } else {
                arrayList!!.addAll(data)
                adapter!!.notifyDataSetChanged()
            }

            adapter!!.setOnItemClickLister(object :
                ProductListAdapter.OnItemClickLister {
                override fun itemClicked(view: View?, position: Int, dataitem: DataItem?) {
                    startActivity(
                        Intent(activity, ProductDetailActivity::class.java)
                            .putExtra("Location_ID", "" + dataitem!!.id)
                    )
                    AppUtils.startFromRightToLeft(activity)
                }
            })
        } else {
            binding.recyclerProductList.visibility = View.GONE
            binding.layoutNotFound.llDataNotFoundView.visibility = View.VISIBLE
            binding.toolBar.ivFilter.visibility = View.GONE
            binding.layoutNotFound.txtDataNotFoundTitle.setText(resources.getString(R.string.product_list_empty))
        }
    }

    override fun getIntentData() {
        if (!intent.getStringExtra("SubCategoryID").equals(""))
        {
            subCategoryID = intent.getStringExtra("SubCategoryID")
        } else {
            vendorID = intent.getStringExtra("VENDOR_ID")
        }
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
            R.id.ivFilter -> {
                popUpFilter()
            }
        }
    }

    fun popUpFilter() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_filter)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = lp
        val llLowCost: LinearLayoutCompat = dialog.findViewById(R.id.llLowCost)
        val imgClose: AppCompatImageView = dialog.findViewById(R.id.imgClose)
        val llHighCost: LinearLayoutCompat = dialog.findViewById(R.id.llHighCost)
        val ivHighCost: AppCompatImageView = dialog.findViewById(R.id.ivHighCost)
        val ivLowCost: AppCompatImageView = dialog.findViewById(R.id.ivLowCost)
        val txtResetFilter: AppCompatTextView = dialog.findViewById(R.id.txtResetFilter)

        if (RestConstant.productFilter.equals("0")) // 0 for low Cost
        {
            ivLowCost.setImageResource(R.drawable.ic_checked)
            ivLowCost.setColorFilter(
                ContextCompat.getColor(activity, R.color.color_blue_dark),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            ivHighCost.setImageResource(R.drawable.checkbox)
        } else if (RestConstant.productFilter.equals("1")) {
            ivHighCost.setImageResource(R.drawable.ic_checked)
            ivHighCost.setColorFilter(
                ContextCompat.getColor(activity, R.color.color_blue_dark),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            ivLowCost.setImageResource(R.drawable.checkbox)
        } else {
            ivHighCost.setImageResource(R.drawable.checkbox)
            ivLowCost.setImageResource(R.drawable.checkbox)
        }

        llLowCost.setOnClickListener {
            dialog.dismiss()
            RestConstant.productFilter = "0"
            applyFilter(ivHighCost, ivLowCost)
        }
        llHighCost.setOnClickListener {
            dialog.dismiss()
            RestConstant.productFilter = "1"
            applyFilter(ivHighCost, ivLowCost)
        }
        txtResetFilter.setOnClickListener {
            dialog.dismiss()
            RestConstant.productFilter = ""
            price_order_By = ""
            pageNumber = 1
            arrayList.clear()
            applyFilter(ivHighCost, ivLowCost)
        }

        imgClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun applyFilter(ivHighCost: AppCompatImageView, ivLowCost: AppCompatImageView) {
        pageNumber = 1
        arrayList.clear()

        if (RestConstant.productFilter.equals("0")) // 0 for low Cost
        {
            price_order_By = "ASC"
            ivLowCost.setImageResource(R.drawable.ic_checked)
            ivLowCost.setColorFilter(
                ContextCompat.getColor(activity, R.color.color_blue_dark),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            ivHighCost.setImageResource(R.drawable.checkbox)
        } else if (RestConstant.productFilter.equals("1")) {
            price_order_By = "DESC"
            ivHighCost.setImageResource(R.drawable.ic_checked)
            ivHighCost.setColorFilter(
                ContextCompat.getColor(activity, R.color.color_blue_dark),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
            ivLowCost.setImageResource(R.drawable.checkbox)
        } else {
            price_order_By = ""
            ivHighCost.setImageResource(R.drawable.checkbox)
            ivLowCost.setImageResource(R.drawable.checkbox)
        }
        isLoading = true
        productListApiCall()
    }

    //set view model
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, factory).get(ProductListViewModel::class.java)
        binding.viewmodel = viewModel
    }

    //set observer
    private fun setupObservers() {

        viewModel.productListCallResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        if (isLoading) {
                            hideProgressDialog()
                        } else {
                            binding.progressPagination.visibility = View.GONE
                        }
                        Log.d(
                            "Response",
                            "====== productListCallResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { productlistResponse ->
                            if (productlistResponse.code == 200)
                            {
                                if (productlistResponse.status == true) {
                                    productList(
                                        (productlistResponse.data!!.data as ArrayList<DataItem>?)!!,
                                        productlistResponse
                                    )
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + productlistResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + productlistResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.recyclerProductList.visibility = View.GONE
                                binding.layoutNotFound.llDataNotFoundView.visibility = View.VISIBLE
                                binding.layoutNotFound.txtDataNotFoundTitle.setText(
                                    resources.getString(
                                        R.string.product_list_empty
                                    )
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        if (isLoading) {
                            hideProgressDialog()
                        } else {
                            binding.progressPagination.visibility = View.GONE
                        }
                        binding.recyclerProductList.visibility = View.GONE
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
                        if (isLoading) {
                            showProgressDialog(activity!!)
                        } else {
                            binding.progressPagination.visibility = View.VISIBLE
                        }
                    }
                }
            })
    }
}
