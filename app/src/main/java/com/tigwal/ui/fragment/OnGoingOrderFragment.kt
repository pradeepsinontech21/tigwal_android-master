package com.tigwal.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.tigwal.data.model.listOrder.ListOrderResponse
import com.tigwal.data.model.listOrder.OrdersItem
import com.tigwal.databinding.FragmentOnGoingOrderBinding
import com.tigwal.ui.activity.CancelOrderActivity
import com.tigwal.ui.activity.DashboardActivity
import com.tigwal.ui.activity.OrderSummaryActivity
import com.tigwal.ui.adapter.OngoingAdapter
import com.tigwal.ui.factory.OnGoingFactory
import com.tigwal.ui.view_model.ongoingorder.OnGoingOrderViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.MySharedPreferences
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


@SuppressLint("UseRequireInsteadOfGet")
class OnGoingOrderFragment : BaseFragment(), KodeinAware, View.OnClickListener {
    private val factory: OnGoingFactory by instance()
    private lateinit var onGoingViewModel: OnGoingOrderViewModel
    private lateinit var binding: FragmentOnGoingOrderBinding
    override val kodein: Kodein by closestKodein()
    var arrayListOngoing = ArrayList<OrdersItem?>()
    var arrayListPast = ArrayList<OrdersItem?>()

    companion object {
    }


    @SuppressLint("Range")
    override fun onCreateFragmentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_on_going_order, container, false)
        RestConstant.ongoingMenuType="1"
        setupViewModel()
        setupObservers()
        return binding.getRoot()
    }

    private fun listOrderApi() {
        if (Connectivity.isConnected(activity)) {
            onGoingViewModel.listOrderApi(RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken)
        } else {
            Toast.makeText(
                activity,
                "" + resources.getString(R.string.noInternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getIntentData() {
        binding.toolBar.txtHeaderTitle.setText(resources.getString(R.string.onGoingOrderTitle))
    }

    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)
        binding.toolBar.ivBack.visibility = View.GONE
        binding.txtOnGoing.setOnClickListener(this)
        binding.txtpastOrder.setOnClickListener(this)
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLD(requireActivity())
        binding.txtOnGoing.typeface = AppUtils.getBOLDMIDIUM(requireActivity())
        binding.txtpastOrder.typeface = AppUtils.getBOLDMIDIUM(requireActivity())
    }

    //click
    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.iv_back -> {
                if (getFragmentManager()!!.getBackStackEntryCount() > 0) {
                    getFragmentManager()!!.popBackStack();
                }
            }
            R.id.txtOnGoing -> {
//                txtOnGoing.setBackgroundDrawable(resources.getDrawable(R.drawable.border_button_blue_app))
//                txtOnGoing.setTextColor(resources.getColor(R.color.white))
//                txtpastOrder.setBackgroundDrawable(resources.getDrawable(R.drawable.background_fill_corner_gray))
//                txtpastOrder.setTextColor(resources.getColor(R.color.black))
                    RestConstant.ongoingMenuType = "1"
                listOrderApi()

            }
            R.id.txtpastOrder -> {
//                txtpastOrder.setBackgroundDrawable(resources.getDrawable(R.drawable.border_button_blue_app))
//                txtpastOrder.setTextColor(resources.getColor(R.color.white))
//                txtOnGoing.setBackgroundDrawable(resources.getDrawable(R.drawable.background_fill_corner_gray))
//                txtOnGoing.setTextColor(resources.getColor(R.color.black))
                RestConstant.ongoingMenuType = "2"
                listOrderApi()
            }
        }
    }

    //set view model
    private fun setupViewModel() {
        onGoingViewModel = ViewModelProvider(this, factory).get(OnGoingOrderViewModel::class.java)
        binding.viewmodel = onGoingViewModel
    }

    private fun setupObservers() {
        onGoingViewModel.listOrderCallResponse.observe(
            this
        ) { response ->
            when (response) {
                is Resource.Success -> {
//                    (activity as DashboardActivity?)!!.rlProgressView.visibility = View.GONE
                    Log.d(
                        "Response",
                        "====== listOrderCallResponse ====> " + Gson().toJson(response)
                    )
                    response.data?.let { productlistResponse ->
                        if (productlistResponse.code == 200) {
                            if (productlistResponse.status == true) {
                                arrayListOngoing.clear()
                                arrayListPast.clear()

                                if (productlistResponse.data!!.orders != null && productlistResponse.data!!.orders!!.size > 0) {
                                    for (i in 0 until productlistResponse.data!!.orders!!.size) {
                                        if (productlistResponse.data!!.orders!!.get(i)!!.order_status.equals(
                                                "1"
                                            )
                                        ) {
                                            arrayListOngoing.add(
                                                productlistResponse.data!!.orders!!.get(
                                                    i
                                                )
                                            )
                                            Log.d(
                                                "ProductlistResponse",
                                                "== arrayListOngoing ======> " + Gson().toJson(
                                                    arrayListOngoing
                                                )
                                            )
                                        } else {
                                            arrayListPast.add(
                                                productlistResponse.data!!.orders!!.get(
                                                    i
                                                )
                                            )

                                            Log.d(
                                                "ProductlistResponse",
                                                "== arrayListPast ======> " + Gson().toJson(
                                                    arrayListPast
                                                )
                                            )
                                        }
                                    }
                                    if (RestConstant.ongoingMenuType.equals("1")) {
                                        if (arrayListOngoing != null && arrayListOngoing.size > 0) {
                                            onGoingOrder()
                                        } else {
                                            binding.recyclerviewOngoing.visibility = View.GONE
                                            binding.nodatafoundview.llDataNotFoundView.visibility =
                                                View.VISIBLE
                                            binding.nodatafoundview.txtDataNotFoundTitle.setText(
                                                resources.getString(R.string.ongoing_order_empty)
                                            )
                                        }
                                    } else {
                                        if (arrayListPast != null && arrayListPast.size > 0) {
                                            onPastOrder()
                                        } else {
                                            binding.recyclerviewOngoing.visibility = View.GONE
                                            binding.nodatafoundview.llDataNotFoundView.visibility =
                                                View.VISIBLE
                                            binding.nodatafoundview.txtDataNotFoundTitle.setText(
                                                resources.getString(R.string.past_order_empty)
                                            )
                                        }
                                    }
                                } else {
                                    if (RestConstant.ongoingMenuType.equals("1")) {
                                        binding.recyclerviewOngoing.visibility = View.GONE
                                        binding.nodatafoundview.llDataNotFoundView.visibility =
                                            View.VISIBLE
                                        binding.nodatafoundview.txtDataNotFoundTitle.setText(
                                            resources.getString(R.string.ongoing_order_empty)
                                        )
                                    } else {
                                        binding.recyclerviewOngoing.visibility = View.GONE
                                        binding.nodatafoundview.llDataNotFoundView.visibility =
                                            View.VISIBLE
                                        binding.nodatafoundview.txtDataNotFoundTitle.setText(
                                            resources.getString(R.string.past_order_empty)
                                        )
                                    }
                                }


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
                        }
                    }
                }

                is Resource.Error -> {
//                    (activity as DashboardActivity?)!!.rlProgressView.visibility = View.GONE
                    Toast.makeText(
                        activity,
                        "" + response.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Resource.Loading -> {
//                    (activity as DashboardActivity?)!!.rlProgressView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun onGoingOrder() {
        if (arrayListOngoing != null && arrayListOngoing.size > 0) {
            binding.recyclerviewOngoing.visibility = View.VISIBLE
            val adapter = OngoingAdapter(arrayListOngoing,"1")
            val mLayoutManager1: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            binding.recyclerviewOngoing.itemAnimator = DefaultItemAnimator()
            binding.recyclerviewOngoing.layoutManager = mLayoutManager1
            binding.recyclerviewOngoing.adapter = adapter
            adapter.setOnItemClickLister(object :
                OngoingAdapter.OnItemClickLister {
                override fun itemClicked(view: View?, position: Int) {

                }
            })
            adapter.setOnItemOrderSummaryClickLister(object :
                OngoingAdapter.OnItemOrderSummaryClickLister {
                override fun itemOrderSummaryClicked(position: Int, data: OrdersItem?) {
                    startActivity(
                        Intent(activity!!, OrderSummaryActivity::class.java)
                            .putExtra("ORDER_ID", data!!.id.toString())
                            .putExtra("Flag", "HISTORY")
                            .putExtra("ORDER_TYPE", "1")

                    )
                    AppUtils.startFromRightToLeft(activity!!)
                }
            })
            adapter.setOnItemCancelOrderClickLister(object :
                OngoingAdapter.OnItemCancelOrderClickLister {
                override fun itemCancelOrderClicked(position: Int, data: OrdersItem?) {
                    startActivity(
                        Intent(activity!!, CancelOrderActivity::class.java)
                            .putExtra("DATA", Gson().toJson(data))
                    )
                    AppUtils.startFromRightToLeft(activity!!)
                }
            })
        } else {
            binding.recyclerviewOngoing.visibility = View.GONE
            binding.nodatafoundview.llDataNotFoundView.visibility =
                View.VISIBLE
            binding.nodatafoundview.txtDataNotFoundTitle.setText(
                resources.getString(R.string.ongoing_order_empty)
            )
        }
    }

    private fun onPastOrder() {
        if (arrayListPast != null && arrayListPast.size > 0) {
            binding.recyclerviewOngoing.visibility = View.VISIBLE
            val adapter = OngoingAdapter(arrayListPast,"2")
            val mLayoutManager1: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            binding.recyclerviewOngoing.itemAnimator = DefaultItemAnimator()
            binding.recyclerviewOngoing.layoutManager = mLayoutManager1
            binding.recyclerviewOngoing.adapter = adapter
            adapter.setOnItemClickLister(object :
                OngoingAdapter.OnItemClickLister {
                override fun itemClicked(view: View?, position: Int) {

                }
            })
            adapter.setOnItemOrderSummaryClickLister(object :
                OngoingAdapter.OnItemOrderSummaryClickLister {
                override fun itemOrderSummaryClicked(position: Int, data: OrdersItem?) {
                    startActivity(
                        Intent(activity!!, OrderSummaryActivity::class.java)
                            .putExtra("ORDER_ID", data!!.id.toString())
                            .putExtra("Flag", "HISTORY")
                            .putExtra("ORDER_TYPE", "2")
                    )
                    AppUtils.startFromRightToLeft(activity!!)
                }
            })
            adapter.setOnItemCancelOrderClickLister(object :
                OngoingAdapter.OnItemCancelOrderClickLister {
                override fun itemCancelOrderClicked(position: Int, data: OrdersItem?) {
                    startActivity(
                        Intent(activity!!, CancelOrderActivity::class.java)
                            .putExtra("DATA", Gson().toJson(data))
                    )
                    AppUtils.startFromRightToLeft(activity!!)
                }
            })
        } else {
            binding.recyclerviewOngoing.visibility = View.GONE
            binding.nodatafoundview.llDataNotFoundView.visibility =
                View.VISIBLE
            binding.nodatafoundview.txtDataNotFoundTitle.setText(
                resources.getString(R.string.past_order_empty)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        RestConstant.ongoingMenuType="1"
        listOrderApi()
binding.txtOnGoing.performClick()
    }
}