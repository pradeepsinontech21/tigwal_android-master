package com.tigwal.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tigwal.R
import com.tigwal.base.BaseActivity
import com.tigwal.databinding.ActivityOrderReceiveBinding
import com.tigwal.ui.factory.OrderReceiveFactory
import com.tigwal.ui.view_model.order_receive.OrderReceiveViewModel
import com.tigwal.utils.AppUtils
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class OrderReceiveActivity : BaseActivity(), KodeinAware, View.OnClickListener {
    private lateinit var binding: ActivityOrderReceiveBinding
    private lateinit var viewModel: OrderReceiveViewModel
    override val kodein: Kodein by kodein()
    private val factory: OrderReceiveFactory by instance()
    var orderId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_order_receive
        )
        setupViewModel()
        setupObservers()
        setLangauge()

        getIntentData()
        clickListener()
        setFontTypeface()

    }

    override fun onResume() {
        super.onResume()
        setLangauge()
    }

    override fun getIntentData() {
        orderId = intent.getStringExtra("ORDER_ID")
    }

    override fun clickListener() {
        binding.tvHome.setOnClickListener(this)
        binding.tvOrderDetails.setOnClickListener(this)
    }

    override fun setFontTypeface() {
        binding.tvHome.typeface = AppUtils.getMIDIUM(activity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                startActivity(
                    Intent(this, DashboardActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
                )
                AppUtils.startFromRightToLeft(activity)
            }
            R.id.tvHome -> {
                startActivity(
                    Intent(this, DashboardActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
                )
                AppUtils.startFromRightToLeft(activity)
            }

            R.id.tvOrderDetails -> {
                startActivity(
                    Intent(this, OrderSummaryActivity::class.java)
                        .putExtra("ORDER_ID", orderId)
                        .putExtra("Flag", "NEW_ORDER")
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
                AppUtils.startFromRightToLeft(activity)
            }

        }
    }

    //set view model
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, factory).get(OrderReceiveViewModel::class.java)
        binding.viewmodel = viewModel
    }

    //set observer
    private fun setupObservers() {
    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(
            Intent(this, DashboardActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        AppUtils.startFromRightToLeft(activity)
    }
}