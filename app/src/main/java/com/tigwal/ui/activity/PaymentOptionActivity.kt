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
import com.google.gson.Gson
import com.tigwal.R
import com.tigwal.app.rest.RestConstant
import com.tigwal.base.BaseActivity
import com.tigwal.data.api.Resource
import com.tigwal.data.api.UserParams
import com.tigwal.data.model.listCart.CartListItem
import com.tigwal.data.model.tap_payment.CartSubmitData
import com.tigwal.databinding.ActivityPaymentOptionBinding
import com.tigwal.ui.factory.PaymentOptionFactory
import com.tigwal.ui.view_model.payment_option.PaymentOptionViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.MySharedPreferences
import company.tap.gosellapi.GoSellSDK
import company.tap.gosellapi.internal.api.callbacks.GoSellError
import company.tap.gosellapi.internal.api.models.Authorize
import company.tap.gosellapi.internal.api.models.Charge
import company.tap.gosellapi.internal.api.models.PhoneNumber
import company.tap.gosellapi.internal.api.models.Token
import company.tap.gosellapi.open.controllers.SDKSession
import company.tap.gosellapi.open.controllers.ThemeObject
import company.tap.gosellapi.open.delegate.SessionDelegate
import company.tap.gosellapi.open.enums.AppearanceMode
import company.tap.gosellapi.open.enums.CardType
import company.tap.gosellapi.open.enums.TransactionMode
import company.tap.gosellapi.open.models.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class PaymentOptionActivity : BaseActivity(), KodeinAware, View.OnClickListener, SessionDelegate {
    private lateinit var binding: ActivityPaymentOptionBinding
    private lateinit var viewModel: PaymentOptionViewModel
    override val kodein: Kodein by kodein()
    private val factory: PaymentOptionFactory by instance()
    var totalPrice: Int = 0
    var totalQuntity: Int = 0
    var admin_commision_total_amt: Double = 0.0
//    var totalDiscount: Int = 0
    var totalDiscount: Double = 0.0
    var totalServiceTax: Double = 0.0
    var strChargesId: String = ""
    var totalCalculatedPrice: Double = 0.0
    var admin_commision_total_amt_desti: Double = 0.0
    var destination_same_qty_: Double = 0.0
    var strPaymentId: String = ""
    var strPaymentType: String = "2"  // 2 for Accepts  3 for Reject
    var arrayListProduct = ArrayList<CartListItem>()
    var createOrderDataTemp = ArrayList<CartSubmitData>()
    var arrayListProductFinal = ArrayList<CartListItem>()
    var strBookingDate: String = ""
    private lateinit var sdkSession: SDKSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_payment_option
        )
        isClick=false
        setupViewModel()
        setupObservers()
        getIntentData()
        clickListener()
        setFontTypeface()
        getCartData()
        sdkSession = SDKSession()
        binding.toolBar.txtHeaderTitle.text = resources.getString(R.string.payment_option)
    }


    private fun getCartData() {

        if (arrayListProduct != null && arrayListProduct.size > 0) {
            for (i in 0 until arrayListProduct.size) {
                // Quantity
                totalQuntity += arrayListProduct.get(i).qty!!.toInt()
                binding.txtNumberOfItem.text = "" + totalQuntity

                admin_commision_total_amt += arrayListProduct.get(i).admin_commision_total_amt!!.toDouble()


                // Price
                totalPrice += arrayListProduct[i].totalPrice.toDouble().toInt()
                binding.txtPrice.text =
                    AppUtils.fractionalPartValueFormate(
                        totalPrice.toString()
                    ).toString() .plus(" ")+  getString(R.string.currency_symbol)


                // Total Service Tax
                if (arrayListProduct[i].totalServiceTax != null && !arrayListProduct[i].totalServiceTax.equals(
                        ""
                    )
                ) {
                    totalServiceTax += arrayListProduct[i].totalServiceTax.toDouble()
                    //  totalServiceTax=t otalServiceTax*
                    binding.txtTaxes.text =
                        "+ " + AppUtils.fractionalPartValueFormate(
                            totalServiceTax.toString()
                        )
                            .toString() + " " + getString(R.string.currency_symbol)
                }
                if (arrayListProduct.get(i).bookDate != null && !arrayListProduct.get(i).bookDate.equals(
                        ""
                    )
                ) {

                    strBookingDate = arrayListProduct.get(i).bookDate!!

                }
                        // Discount
                if (arrayListProduct.get(i).totalDiscount != null && !arrayListProduct.get(i).totalDiscount.equals(
                        ""
                    )
                ) {

//                    totalDiscount += arrayListProduct.get(i).totalDiscount.toDouble().toInt()
                    totalDiscount += arrayListProduct.get(i).totalDiscount.toDouble()
                    binding.txtDiscount.setText(
                        "- " + AppUtils.fractionalPartValueFormate(
                            totalDiscount.toString()
                        ) + " " + getString(R.string.currency_symbol)
                    )
                }

                // Total Calculated Price
                totalCalculatedPrice += arrayListProduct.get(i).totalCalculatePrice.toDouble()
//                    .toInt()
                if (totalServiceTax > 0) {
                    totalCalculatedPrice += totalServiceTax
                    binding.txtTotalPrice.text =
                        getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
                            totalCalculatedPrice.toString()
                        )
                            .toString()
                } else {
                    binding.txtTotalPrice.text =
                        AppUtils.fractionalPartValueFormate(
                            totalCalculatedPrice.toString()
                        ).toString()  + " " +  getString(R.string.currency_symbol)
                }
            }
            if (totalDiscount > 0) {
                binding.llDiscount.visibility = View.VISIBLE
            } else {
                binding.llDiscount.visibility = View.GONE
            }
        }
    }

    override fun getIntentData() {
        if (intent.getSerializableExtra("CART_DATA") != null) {
            arrayListProduct = intent.getSerializableExtra("CART_DATA") as ArrayList<CartListItem>
            Log.d("arrayListProduct", "=====>" + Gson().toJson(arrayListProduct))
        }
        for (i in arrayListProduct){
            var createOrder = CartSubmitData()
            createOrder.vendor_id = i.vendor_id
            createOrder.destination_id = i.destination_id
            createOrder.product_amount = i.price.toString()
            createOrder.service_charge = i.totalServiceTax// will add right now empty
            createOrder.admin_commission = i.admin_commision_total// will add right now empty
            createOrder.final_amount = i.totalCalculatePrice// will add right now empty
            createOrder.location_id = i.locationId!!.toString()// will add right now empty
            createOrder.qty = i.qty.toString()// will add right now empty
            createOrderDataTemp.add(createOrder)}
        Log.e("TAG", "getIntentData:ca "+ Gson().toJson(createOrderDataTemp))
    }

    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)
        binding.btnPayNow.setOnClickListener(this)
        binding.txtCheck.setOnClickListener(this)
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.tvPaymentSummary.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.tvNoOfItems.typeface = AppUtils.getMIDIUM(activity)
        binding.txtNumberOfItem.typeface = AppUtils.getMIDIUM(activity)
        binding.tvPrice.typeface = AppUtils.getMIDIUM(activity)
        binding.txtPrice.typeface = AppUtils.getMIDIUM(activity)
        binding.tvTaxes.typeface = AppUtils.getMIDIUM(activity)
        binding.txtTaxes.typeface = AppUtils.getMIDIUM(activity)
        binding.tvDiscount.typeface = AppUtils.getMIDIUM(activity)
        binding.txtDiscount.typeface = AppUtils.getMIDIUM(activity)
        binding.tvTotalPrice.typeface = AppUtils.getMIDIUM(activity)
        binding.txtTotalPrice.typeface = AppUtils.getMIDIUM(activity)
        binding.btnPayNow.typeface = AppUtils.getMIDIUM(activity)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressed()
            }
            R.id.txt_check -> {
                if (binding.etCouponCode.text.toString().isEmpty()) {
                    Toast.makeText(
                        activity,
                        "" + resources.getString(R.string.please_enter_couponcode),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                }
            }

            R.id.btn_pay_now -> {
                if (isClick==false){
                if (totalCalculatedPrice > 0) {
                    startSDK()
                    isClick=true
                }}
            }
        }
    }
var isClick : Boolean=false
    //set view model
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, factory).get(PaymentOptionViewModel::class.java)
        binding.viewmodel = viewModel
    }

    //set observer
    private fun setupObservers() {
        viewModel.tapPaymentApiResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d(
                            "Response",
                            "====== tapPaymentApiResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { paymentResponse ->

                            if (paymentResponse.data!!.status.equals("CAPTURED")) {
                                strPaymentType = "2" // Success
                                strPaymentId =
                                    paymentResponse.data!!.reference!!.payment!!.toString()

                                if (Connectivity.isConnected(activity)) {

//                                    val params = HashMap<String, Any?>()
//                                    params.put(
//                                        UserParams.payment_id,
//                                        "" + strPaymentId
//                                    )
//                                    params.put(UserParams.charge_id, "" + strChargesId)
//                                    params.put(UserParams.book_date,"" +strBookingDate)
//                                    params.put(
//                                        UserParams.status,
//                                        "" + strPaymentType
//                                    )
//                                    params.put(
//                                        UserParams.total_amount,
//                                        "" + paymentResponse.data!!.amount.toString()
//                                    )
//
//                                    params.put(
//                                        UserParams.total_service_tax,
//                                        "" + totalServiceTax.toString()
//                                    )
//                                    val body: RequestBody = RequestBody.create(
//                                        "application/json; charset=utf-8".toMediaTypeOrNull(),
//                                        JSONObject(params).toString()
//                                    )
//                                    Log.d(
//                                        "Response",
//                                        "====== tapPaymentApiResponse ====>drg " + Gson().toJson(response)
//                                    )
//                                    val body1: RequestBody = Gson().toJson(createOrderDataTemp)
//                                        .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
//
//                                    viewModel.createOrderApi(
//                                        RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
//                                        body,body1
//                                    )
                                    val params = HashMap<String, RequestBody?>()
                                    params[UserParams.payment_id] = AppUtils.getRequestBody(strPaymentId)
                                    params[UserParams.charge_id] = AppUtils.getRequestBody(strChargesId)
                                    params[UserParams.status] = AppUtils.getRequestBody(strPaymentType)
                                    params[UserParams.total_amount] = AppUtils.getRequestBody(paymentResponse.data!!.amount.toString())
                                    params[UserParams.book_date] = AppUtils.getRequestBody(strBookingDate)

                                    params[UserParams.total_service_tax] = AppUtils.getRequestBody(totalServiceTax.toString())

                                    
                                    for (i in arrayListProduct.indices){

                                        params["order_calculation[" + i + "][vendor_id]"] = AppUtils.getRequestBody(arrayListProduct[i].vendor_id)
                                        params["order_calculation[" + i + "][destination_id]"] = AppUtils.getRequestBody(arrayListProduct[i].destination_id)
                                        params["order_calculation[" + i + "][product_amount]"] = AppUtils.getRequestBody(arrayListProduct[i].price.toString())
                                        params["order_calculation[" + i + "][service_charge]"] = AppUtils.getRequestBody(arrayListProduct[i].totalServiceTax)// will add right now empty
                                        params["order_calculation[" + i + "][admin_commission]"] = AppUtils.getRequestBody(arrayListProduct[i].admin_commision_total)// will add right now empty
                                        params["order_calculation[" + i + "][final_amount]"] = AppUtils.getRequestBody(arrayListProduct[i].admin_commision_total_amt)// will add right now empty
                                        params["order_calculation[" + i + "][location_id]"] = AppUtils.getRequestBody(arrayListProduct[i].locationId!!.toString())// will add right now empty
                                        params["order_calculation[" + i + "][qty]"] = AppUtils.getRequestBody(arrayListProduct[i].qty.toString())// will add right now empty

                                    }

                                    Log.d("TAG", "Create order Param: "+params)

                                    Log.d(
                                        "Response",
                                        "====== tapPaymentApiResponse ====>drg " + Gson().toJson(
                                            response
                                        )
                                    )

                                    viewModel.createOrderApi(
                                        RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                                        params
                                    )






                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + resources.getString(R.string.noInternetConnection),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d(
                                        "Response",
                                        "====== tapPaymentApiResponse ====>as " + Gson().toJson(response)
                                    )
                                }
                            } else if (paymentResponse.data!!.status.equals("CANCELLED")) {
                                Toast.makeText(
                                    activity,
                                    "" + response.data.data!!.response!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d(
                                    "Response",
                                    "====== tapPaymentApiResponse ====>asdf " + Gson().toJson(response)
                                )
                                onBackPressed()
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + response.data.data!!.response!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d(
                                    "TAP",
                                    "=====NEW =====>ga" + Gson().toJson(response.data.data!!.response!!)
                                )
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

        viewModel.createOrderResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d(
                            "Response",
                            "====== createOrderResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { createOrderResponse ->
                            if (createOrderResponse.code == 200)
                                if (createOrderResponse.status == true) {
                                    Toast.makeText(
                                        activity,
                                        "" + createOrderResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(
                                        Intent(
                                            activity,
                                            OrderReceiveActivity::class.java
                                        )
                                            .putExtra(
                                                "ORDER_ID",
                                                createOrderResponse.data!!.id.toString()
                                            )
                                    )
                                    AppUtils.startFromRightToLeft(activity)
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + createOrderResponse.message,
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

    // Tap Payment
    @RequiresApi(Build.VERSION_CODES.M)
    fun startSDK() {
        configureApp()
        configureSDKSession()
        configureSDKThemeObject()
        configureSDKMode()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun configureApp() {
        GoSellSDK.init(
            activity,
            "sk_live_XvzEc2hmTkdAj749OlFrboey",
 //           "sk_test_BC6JGNwZns2jXMrqhOTFz8kW",
//            "sk_test_s6PKXrZqQVe2F8RJloAbaTcu",//marketplace
            "com.tigwal"
        )

        GoSellSDK.setLocale("en") //  language to be set by merchant
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun configureSDKThemeObject() {
        ThemeObject.getInstance()
            .setAppearanceMode(AppearanceMode.FULLSCREEN_MODE)
            .setSdkLanguage("en")
//            .setHeaderTextColor(getResources().getColor(R.color.black1))
            .setHeaderTextSize(17)
//            .setHeaderBackgroundColor(R.color.french_gray_new)
            .setCardInputTextColor(R.color.black)
//            .setCardInputInvalidTextColor(R.color.red)
//            .setCardInputPlaceholderTextColor(R.color.gray)
//            .setSaveCardSwitchOffThumbTint(R.color.french_gray_new)
//            .setSaveCardSwitchOnThumbTint(R.color.vibrant_green)
//            .setSaveCardSwitchOffTrackTint(R.color.french_gray)
//            .setSaveCardSwitchOnTrackTint(R.color.vibrant_green_pressed)
//            .setPayButtonResourceId(R.drawable.btn_pay_selector) //btn_pay_merchant_selector
            .setPayButtonDisabledTitleColor(R.color.white)
            .setPayButtonEnabledTitleColor(R.color.white)
            .setPayButtonTextSize(14)
            .setPayButtonLoaderVisible(true)
            .setPayButtonSecurityIconVisible(true).dialogTextSize = 17 // *Optional*

        /*   .setDialogTextColor(R.color.black1)*/
    }

    fun configureSDKSession() {
        // if (!this::sdkSession.isInitialized) sdkSession = SDKSession()
        sdkSession = SDKSession() // added by sanjay working
        sdkSession.addSessionDelegate(this) //* Required *
        sdkSession.instantiatePaymentDataSource() //* Required *

        var updatedCurrencyCode = "omr"
        sdkSession.setCustomer(getCustomer()) //* Required *
        sdkSession.setAmount(BigDecimal(totalCalculatedPrice)) //* Required *
        sdkSession.setPaymentItems(java.util.ArrayList<PaymentItem>()) // * Optional * you can pass empty array list
        sdkSession.setPaymentItems(java.util.ArrayList<PaymentItem>()) // * Optional * you can pass empty array list
        sdkSession.setPaymentType("WEB,CARD") //** Merchant can pass paymentType
        sdkSession.setTransactionCurrency(TapCurrency(updatedCurrencyCode)) //* Required *
        sdkSession.setTaxes(java.util.ArrayList<Tax>()) //* Optional * you can pass empty array list
        sdkSession.setShipping(java.util.ArrayList<Shipping>()) //* Optional * you can pass empty array list
        sdkSession.setPostURL("") //* Optional *
        sdkSession.setPaymentDescription("") //* Optional *
        sdkSession.setPaymentMetadata(HashMap<String, String>()) //* Optional * you can pass empty array hash map
        sdkSession.setPaymentReference(null) //* Optional * you can pass null
        sdkSession.setPaymentStatementDescriptor("") //* Optional *
        sdkSession.isUserAllowedToSaveCard(true) //* Required * you can pass boolean
        sdkSession.isRequires3DSecure(true)
        sdkSession.setDestination(getDestination())
        sdkSession.setReceiptSettings(
            Receipt(
                false,
                false
            )
        )
        sdkSession.setAuthorizeAction(null) // * Optional * you can pass AuthorizeAction object or null
//        sdkSession.setDestination(null) // * Optional * you can pass Destinations object or null
        sdkSession.setMerchantID(null) // * Optional * you can pass merchant id or null
        sdkSession.setCardType(CardType.ALL) //
        // * Optional * you can pass which cardType[CREDIT/DEBIT] you want.By default it loads all available cards for Merchant.
        try {
            Log.e("TAG", "configureSDKSession:sdkSession "+Gson().toJson(sdkSession) )
        } catch (e: Exception) {
        }
    }

    private fun startSDKWithUI() {


        //   sdkSession.transactionMode = TransactionMode.PURCHASE //* Required * // old
        sdkSession.transactionMode = TransactionMode.PURCHASE
        sdkSession.start(activity);
        hideProgressDialog()
    }

    override fun paymentSucceed(charge: Charge) {
        strChargesId = charge.id.toString()
        if (Connectivity.isConnected(activity)) {
            viewModel.tapPaymentApi(
                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                charge.id.toString()
            )
        } else {
            Toast.makeText(
                activity,
                "" + resources.getString(R.string.noInternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    override fun paymentFailed(charge: Charge?) {
        strChargesId = charge!!.id.toString()
        if (Connectivity.isConnected(activity)) {
            viewModel.tapPaymentApi(
                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                charge.id.toString()
            )
        } else {
            Toast.makeText(
                activity,
                "" + resources.getString(R.string.noInternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun authorizationSucceed(authorize: Authorize) {
        Log.d("**___ Tigwal ___**", "authorizationSucceed")
    }

    override fun authorizationFailed(authorize: Authorize?) {
        Log.d("**___ Tigwal ___**", "authorizationFailed")
    }

    override fun cardSaved(charge: Charge) {
        Log.d("**___ Tigwal ___**", "cardSaved")
    }

    override fun cardSavingFailed(charge: Charge) {
        Log.d("**___ Tigwal ___**", "cardSavingFailed")
    }

    override fun cardTokenizedSuccessfully(token: Token) {
        Log.d("**___ Tigwal ___**", "cardTokenizedSuccessfully")
    }


    override fun savedCardsList(cardsList: CardsList) {
        Log.d("**___ Tigwal ___**", "savedCardsList")
    }

    override fun sdkError(goSellError: GoSellError?) {
        Toast.makeText(
            activity,
            "" + goSellError?.errorMessage,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun sessionIsStarting() {
        Log.d("**___ Tigwal ___**", "sessionIsStarting")
    }

    override fun sessionHasStarted() {
        Log.d("**___ Tigwal ___**", "sessionHasStarted")
    }

    override fun sessionCancelled() {
        Log.d("**___ Tigwal ___**", "sessionCancelled")
    }

    override fun sessionFailedToStart() {
        Log.d("**___ Tigwal ___**", "sessionFailedToStart")
    }

    override fun invalidCardDetails() {
        Log.d("**___ Tigwal ___**", "invalidCardDetails")
    }

    override fun backendUnknownError(message: String?) {
        Log.d("**___ Tigwal ___**", "backendUnknownError")
    }

    override fun invalidTransactionMode() {
        Log.d("**___ Tigwal ___**", "invalidTransactionMode")
    }

    override fun invalidCustomerID() {
        Log.d("**___ Tigwal ___**", "invalidCustomerID")
    }

    override fun userEnabledSaveCardOption(saveCardEnabled: Boolean) {
        Log.d("**___ Tigwal ___**", "userEnabledSaveCardOption")
    }


    fun configureSDKMode() {
        startSDKWithUI()
    }

    fun getCustomer(): Customer? {
        // MySharedPreferences.getMySharedPreferences()!!.countrycode = "968"
        // MySharedPreferences.getMySharedPreferences()!!.phoneNumber = "9876541215"
        // MySharedPreferences.getMySharedPreferences()!!.email = "kalpesh@gmail.com"
        // MySharedPreferences.getMySharedPreferences()!!.userName = "kalpesh"

        MySharedPreferences.getMySharedPreferences().let {
            var srtCountrycode: String =
                MySharedPreferences.getMySharedPreferences()!!.countrycode.toString()
            srtCountrycode = srtCountrycode.replace("+", "")
            val phoneNumber = PhoneNumber(
                srtCountrycode,
                MySharedPreferences.getMySharedPreferences()!!.phoneNumber
            )
            var stremail: String = it?.email!!
            if (!stremail.equals("") && stremail != null) {
                stremail = stremail;
            } else {
                stremail = "";
            }
            return Customer.CustomerBuilder(null)
                .email(stremail)
                .firstName(it?.userName)
                .lastName(it?.userName).metadata("")
                .phone(
                    PhoneNumber(
                        phoneNumber.countryCode,
                        phoneNumber.number
                    )
                )
                .build()
        }
    }


    fun getDestination(): Destinations? {
        val destinations = ArrayList<Destination>()

        for (i in 0 until arrayListProduct!!.size) {
            if (i == 0) {
                arrayListProductFinal.add(arrayListProduct.get(i))
            } else {
                var int: Int = 0
                var amt: Int = 0
                for (j in 0 until arrayListProductFinal!!.size) {
                    if (arrayListProductFinal.get(j).destination_id.equals(arrayListProduct.get(i).destination_id)) {
                        admin_commision_total_amt_desti = arrayListProductFinal.get(j).admin_commision_total_amt.toDouble()
                        admin_commision_total_amt_desti += arrayListProduct.get(i).admin_commision_total_amt.toDouble()
                        arrayListProductFinal.get(j).admin_commision_total_amt =
                            admin_commision_total_amt_desti.toString()

                        destination_same_qty_ = arrayListProductFinal.get(j).destination_same_qty.toDouble()
                        destination_same_qty_ += arrayListProduct.get(i).destination_same_qty.toDouble()
                        arrayListProductFinal.get(j).destination_same_qty =
                            destination_same_qty_.toString()


                        int = 1
                        break
                    }
                }
                if (int == 0) {
                    arrayListProductFinal.add(arrayListProduct.get(i))
                }
            }
        }
        Log.e("TAG", "i:arrayListProductFinal " + Gson().toJson(arrayListProductFinal))
        for (i in 0 until arrayListProductFinal!!.size) {
            destinations.add(
                Destination(
                    arrayListProductFinal.get(i).destination_id,  /// destination unique identifier
                    BigDecimal(arrayListProductFinal.get(i).admin_commision_total_amt),  // Amount to be transferred to the destination account
                    TapCurrency(resources.getString(R.string.currency_symbol_s)),  //currency code (three digit ISO format)
                    "Tigwal Service Purcahse",  //Description about the transfer
                    "" //Merchant reference number to the destination
                )
            )
        }
        Log.e("TAG", "i:dest " + Gson().toJson(Destinations(
            BigDecimal(0),  // total amount, transferred to the destination account
            TapCurrency(resources.getString(R.string.currency_symbol_s)),  // transfer currency code
            arrayListProductFinal.size,  //number of destinations trabsfer involved
            destinations
        )))
        return Destinations(
            BigDecimal(0),  // total amount, transferred to the destination account
            TapCurrency(resources.getString(R.string.currency_symbol_s)),  // transfer currency code
            arrayListProductFinal.size,  //number of destinations trabsfer involved
            destinations
        ) //List of destinations object
    }
}