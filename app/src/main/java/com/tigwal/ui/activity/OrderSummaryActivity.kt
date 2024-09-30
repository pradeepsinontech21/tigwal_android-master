package com.tigwal.ui.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
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
import com.tigwal.data.model.order_detail.Data
import com.tigwal.data.model.order_detail.OrderitemsItem
import com.tigwal.databinding.ActivityOrderSummaryBinding
import com.tigwal.ui.adapter.OrderSummaryItemAdapter
import com.tigwal.ui.factory.OrderSummaryFactory
import com.tigwal.ui.view_model.order_summary.OrderSummaryViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.MySharedPreferences
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class OrderSummaryActivity : BaseActivity(), KodeinAware, View.OnClickListener {
    private lateinit var binding: ActivityOrderSummaryBinding
    private lateinit var viewModel: OrderSummaryViewModel
    override val kodein: Kodein by kodein()
    private val factory: OrderSummaryFactory by instance()
    var orderId: String? = ""
    var flag: String? = ""
    private lateinit var orderData: Data
    var orderType:String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_order_summary
        )
        setupViewModel()
        setupObservers()
        getIntentData()
        clickListener()
        setFontTypeface()

        binding.toolBar.txtHeaderTitle.text = resources.getString(R.string.order_summary)

        if (orderType.equals("1")) {
            binding.btnCancelOrder.visibility = View.VISIBLE
        } else {
            binding.btnCancelOrder.visibility = View.GONE
        }
    }

    override fun getIntentData() {
        orderId = intent.getStringExtra("ORDER_ID")
        flag = intent.getStringExtra("Flag")
        orderType = intent.getStringExtra("ORDER_TYPE")
    }

    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)
        binding.btnCancelOrder.setOnClickListener(this)
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.tvOrderDetails.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.tvOrderId.typeface = AppUtils.getREG(activity)
        binding.txtCancelOrderId.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.tvTotalItems.typeface = AppUtils.getREG(activity)
        binding.txtCancelTotalOrder.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.tvTotalCharges.typeface = AppUtils.getREG(activity)
        binding.txtOrderTotalCharge.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.txtCancelOrderDate.typeface = AppUtils.getREG(activity)
        binding.txtCancelOrderTime.typeface = AppUtils.getREG(activity)
        binding.txtOrderStatus.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.tvOrderStatus.typeface = AppUtils.getREG(activity)
        binding.tvItemList.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.btnCancelOrder.typeface = AppUtils.getREG(activity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressed()
            }

            R.id.btnCancelOrder -> {
                startActivity(
                    Intent(activity, CancelOrderActivity::class.java)
                        .putExtra("DATA", Gson().toJson(orderData))
                )
                AppUtils.startFromRightToLeft(activity)
            }

        }
    }

    //set view model
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, factory).get(OrderSummaryViewModel::class.java)
        binding.viewmodel = viewModel
    }

    //set observer
    private fun setupObservers() {

        viewModel.orderDetailCallResponse.observe(
            this
        ) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressDialog()
                    Log.d(
                        "Response",
                        "====== orderDetailCallResponse ====> " + Gson().toJson(response)
                    )
                    response.data?.let { orderResponse ->
                        if (orderResponse.code == 200) {
                            if (orderResponse.status == true) {
                                /*if (RestConstant.ongoingMenuType.equals("1")) {
                                    binding.btnCancelOrder.visibility = View.VISIBLE
                                } else {
                                    binding.btnCancelOrder.visibility = View.GONE
                                }*/
                                if (orderType.equals("1")) {
                                    binding.btnCancelOrder.visibility = View.VISIBLE
                                } else {
                                    binding.btnCancelOrder.visibility = View.GONE
                                }
                                orderData = orderResponse.data!!
                                if (orderResponse.data!!.orderId != null && !orderResponse.data!!.orderId.equals(
                                        ""
                                    )
                                ) {
                                    binding.txtCancelOrderId.setText("" + orderResponse.data.orderId)
                                }

                                if (orderResponse.data!!.totalAmount != null && !orderResponse.data!!.totalAmount.equals(
                                        ""
                                    )
                                ) {
                                    binding.txtOrderTotalCharge.setText(
                                        getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
                                            orderResponse.data!!.totalAmount.toString()
                                        )
                                    )
                                }


                                if (orderResponse.data!!.orderitems!!.get(0)!!.bookDate != null && !orderResponse.data!!.orderitems!!.get(
                                        0
                                    )!!.bookDate.equals(
                                        ""
                                    )
                                ) {
                                    binding.txtCancelOrderDate.setText(
                                        AppUtils.dateFormateNew(
                                            orderResponse.data!!.orderitems!!.get(0)!!.bookDate.toString()
                                        )
                                    )
                                }


                                if (orderResponse.data!!.order_created != null && !orderResponse.data!!.order_created.equals(
                                        ""
                                    )
                                ) {
                                    binding.txtCancelOrderTime.setText(
                                        AppUtils.convertUTC2LocalDateTime(
                                            orderResponse.data!!.order_created.toString()
                                        )
                                    )
                                }


                                if (orderResponse.data!!.status != null && !orderResponse.data!!.status.equals(
                                        ""
                                    )
                                ) {
                                    binding.llStatusView.visibility = View.VISIBLE
                                    if (orderType.equals("1") && orderResponse.data.status.equals("2")) {
                                        binding.txtOrderStatus.text = resources.getString(R.string.success_order)

                                        binding.btnCancelOrder.visibility = View.VISIBLE
                                        binding.txtOrderStatus.setTextColor(resources.getColor(R.color.color_blue_light))

                                    } else if (orderResponse.data!!.status.equals("3")) {
                                        binding.txtOrderStatus.setText(resources.getString(R.string.failed_success))
                                        binding.btnCancelOrder.visibility = View.GONE
//                                        binding.txtOrderStatus.setTextColor(resources.getColor(R.color.red))
                                    } else if (orderResponse.data!!.status.equals("4")) {
                                        binding.txtOrderStatus.setText(resources.getString(R.string.cancel_order))
                                        binding.btnCancelOrder.visibility = View.GONE
//                                        binding.txtOrderStatus.setTextColor(resources.getColor(R.color.red))
                                    }
                                } else {
                                    binding.llStatusView.visibility = View.GONE
                                }

                                if (orderResponse.data!!.orderitems != null && orderResponse.data!!.orderitems!!.size > 0) {

                                    binding.txtCancelTotalOrder.setText("" + orderResponse.data!!.orderitems!!.size)

                                    binding.recyclerOrdersammry.visibility = View.VISIBLE
                                    val adapter = OrderSummaryItemAdapter(
                                        orderResponse.data!!.orderitems
                                    )
                                    val mLayoutManager1: RecyclerView.LayoutManager =
                                        LinearLayoutManager(activity)
                                    binding.recyclerOrdersammry.itemAnimator =
                                        DefaultItemAnimator()
                                    binding.recyclerOrdersammry.layoutManager = mLayoutManager1
                                    binding.recyclerOrdersammry.adapter = adapter
                                    adapter.setOnItemClickLister(object :
                                        OrderSummaryItemAdapter.OnItemClickLister {
                                        override fun itemClicked(view: View?, position: Int) {

                                        }

                                        override fun itemChat(
                                            view: View?,
                                            position: Int,
                                            orderitemsItem: OrderitemsItem
                                        ) {
                                            startActivity(
                                                Intent(
                                                    activity,
                                                    SupportChatActivity::class.java
                                                )
                                                    .putExtra(
                                                        "VENDOR_ID",
                                                        "" + orderitemsItem.locations!!.vendorId.toString()
                                                    )
                                                    .putExtra(
                                                        "IMAGE_VENDOR",
                                                        "" + orderitemsItem.locations.images.get(0)
                                                            .toString()
                                                    )
                                            )
                                            AppUtils.startFromRightToLeft(activity!!)
                                        }

                                        override fun itemRating(
                                            view: View?,
                                            position: Int,
                                            orderitemsItem: OrderitemsItem
                                        ) {
                                            addRating(orderitemsItem)
                                        }
                                    })
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + orderResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                activity,
                                "" + orderResponse.message,
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
                    showProgressDialog(activity)
                }
            }
        }

        viewModel.addRatingApiCallResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d(
                            "Response",
                            "====== addRatingApiCallResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { ratingResponse ->
                            if (ratingResponse.code == 200) {
                                if (ratingResponse.status == true) {
                                    Toast.makeText(
                                        activity,
                                        "" + ratingResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    if (Connectivity.isConnected(activity)) {
                                        viewModel.orderDetailApi(
                                            RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                                            orderId.toString()
                                        )
                                    } else {
                                        Toast.makeText(
                                            activity,
                                            "" + resources.getString(R.string.noInternetConnection),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + ratingResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + ratingResponse.message,
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
                        showProgressDialog(activity)

                    }
                }
            })
    }

    private fun addRating(orderitemsItem: OrderitemsItem) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialoge_custom_review_add)
        var lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow()!!.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.getWindow()!!.setAttributes(lp)
        var ratingsBusiness = dialog.findViewById<AppCompatRatingBar>(R.id.ratingsBusiness)
        var buttonYes = dialog.findViewById<AppCompatTextView>(R.id.buttonYes)
        var buttonNo = dialog.findViewById<AppCompatTextView>(R.id.buttonNo)
        dialog.show()
        buttonYes.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View) {
                    dialog.dismiss()
                    if (Connectivity.isConnected(activity)) {
                        val params = java.util.HashMap<String, String?>()
                        params[UserParams.location_id] = orderitemsItem.locationId.toString()
                        params[UserParams.rating] = ratingsBusiness.rating.toInt().toString()
                        params[UserParams.review] = ""
                        viewModel.ratingApiCall(
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
            })
        buttonNo.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View) {
                    dialog.dismiss()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        if (Connectivity.isConnected(activity)) {
            viewModel.orderDetailApi(
                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                orderId.toString()
            )
        } else {
            Toast.makeText(
                activity,
                "" + resources.getString(R.string.noInternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (flag.equals("NEW_ORDER")) {
            startActivity(
                Intent(
                    this,
                    DashboardActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}