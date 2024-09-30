package com.tigwal.ui.activity

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.tigwal.R
import com.tigwal.app.rest.RestConstant
import com.tigwal.base.BaseActivity
import com.tigwal.data.api.ApiClientTAP
import com.tigwal.data.api.ApiInterfaceTAP
import com.tigwal.data.api.Resource
import com.tigwal.data.api.UserParams
import com.tigwal.data.model.cancel_order.RefundApiResponse
import com.tigwal.data.model.order_detail.Data
import com.tigwal.databinding.ActivityCancelOrderBinding
import com.tigwal.ui.factory.CancelOrderFactory
import com.tigwal.ui.view_model.cancel_order.CancelOrderViewModel
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap

class CancelOrderActivity : BaseActivity(), KodeinAware, View.OnClickListener {
    private lateinit var binding: ActivityCancelOrderBinding
    private lateinit var viewModel: CancelOrderViewModel
    override val kodein: Kodein by kodein()
    private val factory: CancelOrderFactory by instance()
    var flag: String? = ""
    var createdDate: String? = ""
    var refund_percentage: String? = "0"
    var refundOrderIndex: Int? = 0
    private lateinit var orderData: Data
     var strTotalAmountCancellatoin: Int? =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_cancel_order
        )
        setupViewModel()
        setupObservers()
        getIntentData()
        clickListener()
        setFontTypeface()
        binding.toolBar.txtHeaderTitle.text = resources.getString(R.string.cancel_order)
        orderDetailData()
    }

    private fun orderDetailData() {

        if (orderData.orderId != null && !orderData.orderId.equals("")) {
            binding.txtCancelOrderId.text = "" + orderData.orderId
        }

//        if (!orderData.order_created.toString()
//                .equals("")
//        ) {
//            binding.txtCancelOrderDate.text =
//                AppUtils.dateFormate(orderData.order_created.toString())
//        }

        if (!orderData.order_created.toString()
                .equals("")
        ) {
            binding.txtCancelOrderTime.text =
                AppUtils.convertUTC2LocalDateTime(orderData.order_created.toString())
        }



        if (orderData.orderitems!!.get(0)!!.bookDate != null && !orderData.orderitems!!.get(0)!!.bookDate.equals(
                ""
            )
        ) {
            createdDate =   AppUtils.dateFormateNew(
                orderData.orderitems!!.get(0)!!.bookDate.toString()
            )
            Log.e("TAG", "getIntentData:createdDate: "+createdDate )
            binding.txtCancelOrderDate.setText(createdDate)
        }


        if (orderData.totalAmount != null && !orderData.totalAmount.equals("")) {
            binding.txtOrderTotalCharge.text =
                AppUtils.fractionalPartValueFormate(
                    orderData.totalAmount!!
                ).toString() .plus(" ")+  getString(R.string.currency_symbol)
        }

        if (orderData.orderitems != null && orderData.orderitems!!.size > 0) {
            binding.txtCancelTotalOrder.text = "" + orderData.orderitems!!.size
        }

        if (orderData.totalAmount != null && !orderData.totalAmount.equals(""))
        {
            val utcFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            utcFormatter.timeZone = TimeZone.getTimeZone("UTC")
            var gpsUTCDate: Date? = null
            try {
                gpsUTCDate = utcFormatter.parse(orderData.orderitems!!.get(0)!!.bookDate.toString())
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            // 2. Output
            val diff: Long = gpsUTCDate!!.getTime() - System.currentTimeMillis()
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            Log.d("days","days=======>"+days)
//            1- cancellation before 72 hrs = 100% refund
//            2- cancellation before 24 and after 72 hrs = 50% refund
//            3- no refund for cancellation after 24 hrs
            if (days>3){
                binding.tvCancelOrderRef.text="You will get 100% payment back"
                binding.btnSubmit.visibility=View.VISIBLE
                refund_percentage="100"
            }else if (days<=3&&days>0){
                binding.tvCancelOrderRef.text="You will get 50% payment back"
                binding.btnSubmit.visibility=View.VISIBLE
                refund_percentage="50"
            }else{
                binding.tvCancelOrderRef.text="You will not get any refund as only one day is remaining"
                binding.btnSubmit.visibility=View.GONE
                refund_percentage="0"
            }
//            if(days<=24)
//            {
//                strTotalAmountCancellatoin=orderData.totalAmount
//                Log.d("CancellAmount","=======>"+strTotalAmountCancellatoin)
//            }
//            else if(days<=48)
//            {
//                strTotalAmountCancellatoin=orderData.totalAmount
//                Log.d("CancellAmount","=======>"+strTotalAmountCancellatoin)
//            }
//            else if(days>72)
//            {
//                strTotalAmountCancellatoin= (orderData.totalAmount.toString().toDouble().toInt()/2).toString()
//                Log.d("CancellAmount","=======>"+strTotalAmountCancellatoin)
//            }
        }
    }

    override fun getIntentData() {
        if (intent.getSerializableExtra("DATA") != null) {
            orderData = Gson().fromJson<Data>(intent.getStringExtra("DATA"), Data::class.java)
            Log.d("DATA", "=====>" + orderData)

        }

    }

    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
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
        binding.tvReason.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.txtReason.typeface = AppUtils.getREG(activity)
        binding.btnSubmit.typeface = AppUtils.getREG(activity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressed()
            }

            R.id.btn_submit -> {
                if (binding.txtReason.text.toString().trim().isEmpty()) {
                    Toast.makeText(
                        activity, resources.getString(R.string.please_enter_reason),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    refundApiCalling()
                }
            }
        }
    }

    private fun cancelOrderApi(refund_id: String) {
        if (Connectivity.isConnected(activity)) {
           var pass_amount= ((orderData.ordercalculation!!.get(refundOrderIndex!!)!!.final_amount!!).toDouble()
            + orderData.ordercalculation!!.get(refundOrderIndex!!)!!.admin_commission!!).toInt()
            if (refund_percentage.equals("50")){
                pass_amount = pass_amount/2
            }
            Log.e("TAG", "refundApiCalling: pass_amount"+pass_amount )

            val params = HashMap<String, Any?>()
            params.put(UserParams.charge_refund_id, "" + refund_id.toString())
            params.put(UserParams.refund_amount, "" + pass_amount)
            params.put(UserParams.order_id, "" + orderData.orderId)
            params.put(UserParams.order_item, "" + orderData.ordercalculation!!.get(refundOrderIndex!!)!!.id)
            params.put(
                UserParams.cancellation_reason,
                "" + binding.txtReason.text.toString()
            )
            params.put(UserParams.refund_percentage, "" + refund_percentage)
            val body: RequestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                JSONObject(params).toString()
            )
            viewModel.cancelOrderApi(
                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                body
            )
        }
    }

    //set view model
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, factory).get(CancelOrderViewModel::class.java)
        binding.viewmodel = viewModel
    }

    //set observer
    private fun setupObservers() {
        viewModel.cancelOrderCallResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d(
                            "Response",
                            "====== cancelOrderCallResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { canceilOrderResponse ->
                            if (canceilOrderResponse.code == 200) {
                                refundOrderIndex = refundOrderIndex!! + 1
                                refundApiCalling()
                                //cancelOrderDialoge(canceilOrderResponse.message)
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + canceilOrderResponse.message,
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

    private fun cancelOrderDialoge(strMessage: String?) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialoge_custom_single_button)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = lp
        val txtOkay: AppCompatTextView = dialog.findViewById(R.id.txtOkay)
        val txtMessage: AppCompatTextView = dialog.findViewById(R.id.txtMessage)
        txtMessage.text = strMessage.toString()
        txtOkay.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(activity, DashboardActivity::class.java))
            AppUtils.startFromRightToLeft(activity)
        }
        dialog.show()
    }

    fun refundApiCalling() {
        if (refundOrderIndex!! < orderData.ordercalculation!!.size){
            strTotalAmountCancellatoin =((orderData.ordercalculation!!.get(refundOrderIndex!!)!!.final_amount!!).toDouble() +
                    orderData.ordercalculation!!.get(refundOrderIndex!!)!!.admin_commission!!).toInt()
            if (refund_percentage.equals("50")){
                strTotalAmountCancellatoin = strTotalAmountCancellatoin!! /2
            }
            Log.e("TAG", "refundApiCalling: strTotalAmountCancellatoin"+strTotalAmountCancellatoin )
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            //your codes here
        }
        // Cancellation code
        showProgressDialog(activity)
        val params = HashMap<String, Any?>()
        params.put(UserParams.charge_id, "" + orderData.chargeId.toString())
        params.put(UserParams.amount, "" + strTotalAmountCancellatoin!!.toDouble().toInt())
        params.put(UserParams.reason, "" + binding.txtReason.text.toString())
        params.put(UserParams.currency, "OMR")
        params.put(UserParams.description, "" + binding.txtReason.text.toString())
        Log.d("params", "==== params ====>" + params.toString())


        val body: RequestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(params).toString()
        )
        val call: Call<RefundApiResponse?>? =
            ApiClientTAP.getClient()!!.create(ApiInterfaceTAP::class.java)
                .refunds(RestConstant.BEARER + "sk_live_XvzEc2hmTkdAj749OlFrboey", body)
                //.refunds(RestConstant.BEARER + "sk_test_BC6JGNwZns2jXMrqhOTFz8kW", body)


//                .refunds(RestConstant.BEARER + "sk_test_s6PKXrZqQVe2F8RJloAbaTcu", body)//marketplace
        call!!.enqueue(object : Callback<RefundApiResponse?> {
            override fun onResponse(
                call: Call<RefundApiResponse?>,
                response: Response<RefundApiResponse?>
            ) {
                hideProgressDialog()
                Log.d(
                    "Cancelorder",
                    "==== onResponse Success ====>" + Gson().toJson(response.body()!!)
                )
                cancelOrderApi(response.body()!!.id.toString())
            }

            override fun onFailure(call: Call<RefundApiResponse?>, t: Throwable) {
                hideProgressDialog()
                Log.d("Cancelorder", "==== onResponse onFailure ====>" + t.localizedMessage)
                Toast.makeText(
                    activity,
                    "" + t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        }else{
            cancelOrderDialoge("Refund Done Successfully")
        }
    }
}