package com.tigwal.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tigwal.R
import com.tigwal.app.rest.RestConstant
import com.tigwal.base.BaseActivity
import com.tigwal.data.api.Resource
import com.tigwal.data.model.getcategory.DataItem
import com.tigwal.databinding.ActivityViewAllCategoryBinding
import com.tigwal.ui.adapter.CategoryAdapter
import com.tigwal.ui.factory.ViewAllCategoryFactory
import com.tigwal.ui.view_model.viewallcategory.ViewAllCategoryViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.MySharedPreferences
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ViewAllCategoryActivity : BaseActivity(), KodeinAware, View.OnClickListener {
    private lateinit var binding: ActivityViewAllCategoryBinding
    private lateinit var viewModel: ViewAllCategoryViewModel
    override val kodein: Kodein by kodein()
    private val factory: ViewAllCategoryFactory by instance()
    var arrayListCategory = ArrayList<DataItem?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_view_all_category
        )
        setupViewModel()
        setupObservers()
        getIntentData()
        clickListener()
        setFontTypeface()

        if (Connectivity.isConnected(activity)) {
            viewModel.getCategoryApi(
                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken
            )
        } else {
            Toast.makeText(
                applicationContext,
                "" + resources.getString(R.string.noInternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.toolBar.txtHeaderTitle.setText(resources.getString(R.string.product_category))
    }


    override fun getIntentData() {
    }

    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLDMIDIUM(activity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressed()
            }

        }
    }

    //set view model
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, factory).get(ViewAllCategoryViewModel::class.java)
        binding.viewmodel = viewModel
    }

    //set observer
    private fun setupObservers() {

        viewModel.getCategoryApiResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d(
                            "Response",
                            "====== getCategoryApiResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { categoryResponse ->
                            if (categoryResponse.code == 200) {
                                if (categoryResponse.status == true) {
                                    arrayListCategory = categoryResponse.data!!

                                    val adapter = CategoryAdapter(
                                        categoryResponse.data
                                    )
                                    val mLayoutManager1: RecyclerView.LayoutManager =
                                        GridLayoutManager(activity, 2)
                                    binding.recyclerCategory.itemAnimator = DefaultItemAnimator()
                                    binding.recyclerCategory.layoutManager = mLayoutManager1
                                    binding.recyclerCategory.adapter = adapter

                                    adapter.setOnItemClickLister(object :
                                        CategoryAdapter.OnItemClickLister {
                                        override fun itemClicked(view: View?, position: Int) {
                                            if (!arrayListCategory.get(position)!!.subcategory.equals(
                                                    "0"
                                                )
                                            ) {
                                                startActivity(
                                                    Intent(
                                                        activity,
                                                        SubCategoryActivity::class.java
                                                    ).putExtra(
                                                        "SubCategoryID",
                                                        arrayListCategory.get(position)!!.categoryId
                                                    )
                                                )
                                                AppUtils.startFromRightToLeft(activity!!)
                                            } else {
                                                startActivity(
                                                    Intent(
                                                        activity,
                                                        ProductListActivity::class.java
                                                    )
                                                        .putExtra(
                                                            "SubCategoryID",
                                                            arrayListCategory.get(position)!!.categoryId
                                                        )
                                                        .putExtra("VENDOR_ID", "")

                                                )
                                                AppUtils.startFromRightToLeft(activity!!)
                                            }
                                        }
                                    })

                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + categoryResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + categoryResponse.message,
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