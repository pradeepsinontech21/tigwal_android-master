package com.tigwal.ui.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.tigwal.R
import com.tigwal.app.rest.RestConstant
import com.tigwal.base.BaseActivity
import com.tigwal.data.api.Resource
import com.tigwal.data.api.UserParams
import com.tigwal.data.model.addtocart.AddToCartResponse
import com.tigwal.data.model.getTimeSlot.DataItem
import com.tigwal.data.model.product_detail.Data
import com.tigwal.data.model.product_detail.RelatedDataItem
import com.tigwal.databinding.ActivityProductDetailBinding
import com.tigwal.ui.adapter.QuantityAdapter
import com.tigwal.ui.adapter.RecommendationAdapter
import com.tigwal.ui.adapter.TimeSlotAdapter
import com.tigwal.ui.factory.ProductDetailFactory
import com.tigwal.ui.fragment.CartFragment
import com.tigwal.ui.view_model.product_detail.ProductDetailViewModel
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
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


class ProductDetailActivity : BaseActivity(), KodeinAware, View.OnClickListener {
    var dayList:String=""
    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var viewModel: ProductDetailViewModel
    override val kodein: Kodein by kodein()
    private val factory: ProductDetailFactory by instance()
    var locationID: String? = ""
    var urlList = ArrayList<com.tigwal.data.model.product_detail.ImagesItem?>()
    var arrayListRecommendation = ArrayList<RelatedDataItem?>()
    var strQuantity: Int = 1
    var strQuantityTotalLength: Int = 1
    var strBookingDate: String = ""
    var strTimeSlot: String = ""
    var strTotalPrice: Double = 0.0
    var strDiscount: Double = 0.0
    var datatimeSlotArray = ArrayList<DataItem?>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_product_detail
        )
        setupViewModel()
        setupObservers()
        getIntentData()
        clickListener()
        setFontTypeface()
        MySharedPreferences.getMySharedPreferences()!!.locationID = ""
        binding.toolBar.txtHeaderTitle.text = resources.getString(R.string.product_detail)
        if (Connectivity.isConnected(activity)) {
            viewModel.productDetailApi(
                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                locationID.toString()
            )
        }
        else {
            Toast.makeText(
                applicationContext,
                "" + resources.getString(R.string.noIternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }


       // timeSlotApi()

    }

    private fun timeSlotApi() {
        if (Connectivity.isConnected(activity)) {
            val params = HashMap<String, Any?>()
            params.put(
                UserParams.location_id,
                "" + locationID
            )
            params.put(UserParams.date, "" + strBookingDate)
            val body: RequestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                JSONObject(params).toString()
            )
            viewModel.getTimeSlotApi(
                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken,
                body
            )
        }
    }

    override fun getIntentData() {
        locationID = intent.getStringExtra("Location_ID")
    }

    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)
        binding.rlAddToCart.setOnClickListener(this)
        binding.txtBookingDate.setOnClickListener(this)
        binding.imgCalender.setOnClickListener(this)
        binding.llQuantity.setOnClickListener(this)
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.txtDate.typeface = AppUtils.getREG(activity)
        binding.txtTime.typeface = AppUtils.getREG(activity)
        binding.tvProductName.typeface = AppUtils.getSEMIBOLD(activity)
        binding.txtPrice.typeface = AppUtils.getMIDIUM(activity)
        binding.tvDescription.typeface = AppUtils.getMIDIUM(activity)
        binding.txtHeaderTitle1.typeface = AppUtils.getMIDIUM(activity)
        binding.tvRelatedProduct.typeface = AppUtils.getSEMIBOLD(activity)
        binding.btnSubmit.typeface = AppUtils.getREG(activity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressed()
            }

            R.id.imgCalender,
            R.id.txtBookingDate,
            -> {
                datePickerDialogFinal()
            }

            R.id.llQuantity -> {
                dialogQuantity()

            }
            R.id.rlAddToCart -> {
                if (Connectivity.isConnected(activity)) {
                    if (binding.txtBookingDate.text.isEmpty()) {
                        Toast.makeText(
                            activity,
                            "" + resources.getString(R.string.please_select_booking_date),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (strTimeSlot.equals("")) {
                        Toast.makeText(
                            activity,
                            "" + resources.getString(R.string.please_select_time_slot),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {

                        if (!MySharedPreferences.getMySharedPreferences()!!.userId.equals("")) {
                            val params = HashMap<String, String?>()
                            params[UserParams.location_id] = locationID
                            params[UserParams.slot_id] = strTimeSlot
                            params[UserParams.qty] = strQuantity.toString()
                            params[UserParams.price] = strTotalPrice.toString()
                            params[UserParams.book_date] = strBookingDate
                            viewModel.addToCartApi(
                                activity,
                                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()!!.authToken,
                                params
                            )
                        } else {

                            val dialog = Dialog(activity!!)
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog.setCancelable(true)
                            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                            dialog.setContentView(R.layout.dialoge_custom_two_button)
                            val lp = WindowManager.LayoutParams()
                            lp.copyFrom(dialog.window!!.attributes)
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                            dialog.window!!.attributes = lp
                            val txtNo: AppCompatTextView = dialog.findViewById(R.id.txtNo)
                            val txtYes: AppCompatTextView = dialog.findViewById(R.id.txtYes)
                            val txtMessage: AppCompatTextView = dialog.findViewById(R.id.txtMessage)
                            txtMessage.text = resources.getString(R.string.do_you_want_signin_cart)
                            txtYes.setOnClickListener {
                                MySharedPreferences.getMySharedPreferences()!!.locationID =
                                    locationID
                                dialog.dismiss()
                                startActivity(Intent(activity, LoginActivity::class.java))
                                AppUtils.startFromRightToLeft(activity)
                            }
                            txtNo.setOnClickListener {
                                dialog.dismiss()
                            }
                            dialog.show()
                        }
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "" + resources.getString(R.string.noInternetConnection),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }
    }

    //set view model
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, factory).get(ProductDetailViewModel::class.java)
        binding.viewmodel = viewModel
    }


    // Banner Adapter
    inner class ProductBannerAdapter(
        private val context: Context,
        private val urlList: ArrayList<com.tigwal.data.model.product_detail.ImagesItem?>,
    ) : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflater =
                context.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view: View? = null
            view = inflater.inflate(R.layout.row_product_banner_imgage, container, false)
            var imgBanner: AppCompatImageView = view.findViewById(R.id.imgBanner)

            if (urlList.get(position)!!.image != null && !urlList.get(position)!!.image.equals("")) {
                Glide.with(Objects.requireNonNull(activity)!!)
                    .load(urlList.get(position)!!.image)
                    .placeholder(R.drawable.img_placeholder).fitCenter()
                    .into(imgBanner)
            }
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return urlList.size
        }

        override fun isViewFromObject(view: View, o: Any): Boolean {
            return view === o
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }


    //set observer
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NewApi")
    private fun setupObservers() {
        viewModel.productDetailCallResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d(
                            "Response",
                            "====== productDetailCallResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { productResponse ->
                            if (productResponse.code == 200) {
                                if (productResponse.status == true) {
                                    productDetail(productResponse.data)
                                } else {
                                    if (MySharedPreferences.getMySharedPreferences()!!.language.equals(
                                            "ar"
                                        )
                                    ) {
                                        if (productResponse.message.equals("In the cart, you cannot add services of different dates.")) {
                                            Toast.makeText(
                                                activity,
                                                "" + resources.getString(R.string.two_date_cart),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                activity,
                                                "" + productResponse.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            activity,
                                            "" + productResponse.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {

                                Toast.makeText(
                                    activity,
                                    "" + productResponse.message,
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

        viewModel.addToCartCallResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d(
                            "Response",
                            "====== productDetailCallResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { productResponse ->
                            if (productResponse.code == 200) {
                                if (productResponse.status == true) {
                                    dialogeAddToCartApp(productResponse)
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + productResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + productResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    is Resource.Error -> {
                        hideProgressDialog()

                    }
                    is Resource.Loading -> {
                        showProgressDialog(activity!!)
                    }
                }
            })



        viewModel.getTimeSlotCallResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        Log.d(
                            "Response",
                            "====== getTimeSlotCallResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { timeSlot ->
                            if (timeSlot.code == 200) {
                                if (timeSlot.status == true) {
                                    if (timeSlot.data != null && timeSlot.data!!.size > 0)
                                    {
                                        datatimeSlotArray.clear()
                                        var d1: Date? = null
                                        try {
                                            d1 =
                                                SimpleDateFormat(
                                                    "yyyy-MM-dd",
                                                    Locale.ENGLISH
                                                ).parse(strBookingDate)
                                        } catch (e: ParseException) {
                                            e.printStackTrace()
                                        }
                                        val dtf = DateTimeFormatter.ofPattern(
                                            "dd/MM/yyyy",
                                            Locale.ENGLISH
                                        )
                                        val now = LocalDateTime.now()

                                        val sdformat =
                                            SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                                        var d2: Date? = null
                                        try {
                                            d2 = sdformat.parse(dtf.format(now))
                                        } catch (e: ParseException) {
                                            e.printStackTrace()
                                        }
                                        Log.d("RRR", "d1$d1")
                                        Log.d("RRR", "d2$d2")
                                        if (d1!!.compareTo(d2) == 0) {
                                            for (i in 0 until timeSlot.data!!.size) {
                                                val now = LocalTime.now()
                                                val startTime =
                                                    LocalTime.parse(timeSlot.data!!.get(i)!!.startTime)
                                                if (startTime.isBefore(now))
                                                {
                                                    Log.d("Time", "====== True isBefore =====> ")
                                                    datatimeSlotArray!!.add(timeSlot.data!!.get(i))
                                                } else {
                                                    Log.d("Time", "====== False s =====> ")
                                                    datatimeSlotArray!!.add(timeSlot.data!!.get(i))
                                                }
                                            }
                                        } else
                                        {
                                            datatimeSlotArray!!.addAll(timeSlot.data!!)
                                        }

                                        if (datatimeSlotArray != null && datatimeSlotArray.size > 0) {
                                            binding.txtTimeSlotNotFound.visibility = View.GONE
                                            binding.recyclerviewTimeSlot.visibility = View.VISIBLE
                                            val timeSlotAdapter =
                                                TimeSlotAdapter(datatimeSlotArray!!, strBookingDate)
                                            val mLayoutManager2: RecyclerView.LayoutManager =
                                                StaggeredGridLayoutManager(
                                                    3,
                                                    LinearLayoutManager.VERTICAL
                                                )
                                            binding.recyclerviewTimeSlot.itemAnimator =
                                                DefaultItemAnimator()
                                            binding.recyclerviewTimeSlot.layoutManager =
                                                mLayoutManager2
                                            binding.recyclerviewTimeSlot.adapter = timeSlotAdapter

                                            timeSlotAdapter.setOnItemClickLister(object :
                                                TimeSlotAdapter.OnItemClickLister {
                                                override fun itemClicked(
                                                    view: View?,
                                                    ProductPos: Int,
                                                    data: DataItem?,
                                                ) {
                                                    strTimeSlot = data!!.id.toString()
                                                }
                                            })
                                        } else {
                                            binding.txtTimeSlotNotFound.visibility = View.VISIBLE
                                            binding.recyclerviewTimeSlot.visibility = View.GONE
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + timeSlot.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + timeSlot.message,
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
    }
    fun findMatch(s: String, strings: List<String>): Boolean {
        return strings.any { s.contains(it) }
    }
    private fun productDetail(data: Data?) {
        if (data!!.location!!.location_created != null && !data!!.location!!.location_created.equals(
                ""
            )
        ) {
            binding.txtDate.setText(AppUtils.dateFormate(data!!.location!!.location_created.toString()))
        }

        if (data!!.location!!.totalQty != null && !data!!.location!!.totalQty!!.equals("")) {
            strQuantityTotalLength = data!!.location!!.totalQty!!.toInt()
        }

        if (data!!.location!!.discount != null && !data!!.location!!.discount!!.equals("") && !data!!.location!!.discount!!.equals(
                "0"
            )
        ) {
            binding.txtOfferRate.visibility = View.VISIBLE
            binding.txtOfferRate.setText(
                "(" + data!!.location!!.discount + "%" + ")"
            )
        } else {
            binding.txtOfferRate.visibility = View.GONE
            binding.txtPrice.visibility = View.GONE
            binding.tvSlash.visibility = View.GONE
        }


        if (data!!.location!!.location_close != null && !data!!.location!!.location_close.equals(""))
        {
            dayList= (listOf(data!!.location!!.location_close) as List<String>).toString().replace("[[","[") .replace("]]","]")
            Log.d("dayList","=======>"+dayList)

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            strBookingDate = sdf.format(Date())
            binding.txtBookingDate.text = strBookingDate
            val outFormat = SimpleDateFormat("EEEE")
            val goal = outFormat.format(Date().time)
            Log.e("TAG", "datePickerDialogFinal: "+goal )
            if (dayList.contains(goal))
            {
                timeSlotApi()
            }
            else {
                binding.txtTimeSlotNotFound.visibility = View.VISIBLE
                binding.recyclerviewTimeSlot.visibility = View.GONE
            }
        }

        if (data!!.location!!.price != null && !data!!.location!!.price.equals("")) {
            var priceTemp =
                getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(data!!.location!!.price.toString())
            val spannableString1 = SpannableString(priceTemp)
            spannableString1.setSpan(
                StrikethroughSpan(),
                0,
                priceTemp.length,
                0
            )
            binding.txtPrice.text = spannableString1
        }

        if (data!!.location!!.price != null && !data!!.location!!.price.equals("")) {
            strTotalPrice = strQuantity * data!!.location!!.price!!.toDouble()

            if (data!!.location!!.discount != null && !data!!.location!!.discount.equals("")) {
                // strDiscount = data!!.location!!.discount!!.toString().toDouble()
                strDiscount =
                    strTotalPrice.toDouble() * data.location!!.discount!!.toInt().toDouble()
                strDiscount = strDiscount / 100


                if (strDiscount > 0) {
                    binding.txtDiscountPrice.setText(
                        getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
                            ((data!!.location!!.price!!.toDouble() - strDiscount).toString())
                        )
                    )
                } else {
                    binding.txtDiscountPrice.setText(
                        getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
                            data!!.location!!.price!!
                        )
                    )
                }
            } else {
                binding.txtDiscountPrice.setText(
                    getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
                        data!!.location!!.price!!
                    )
                )
            }
        }

        if (data!!.location!!.locAddress != null && !data!!.location!!.locAddress.equals(
                ""
            )
        ) {
            binding.txtAddress.setText(AppUtils.capitalize(data!!.location!!.locAddress.toString()))
        }


        if (data?.location?.users?.firstName != null && !data?.location?.users?.firstName.toString().equals(
                ""
            )
        ) {
            binding.txtHeaderTitle1.setText(AppUtils.capitalize(data!!.location!!.users!!.firstName + " " + data!!.location!!.users!!.lastName))
        }

        if (data!!.location!!.description != null && !data!!.location!!.description.equals("")) {
            binding.tvDescription.setText(data!!.location!!.description)
        }

        if (data!!.location!!.location_created != null && !data!!.location!!.location_created.equals(
                ""
            )
        ) {
            binding.txtTime.setText(AppUtils.timeFormate(data!!.location!!.location_created.toString()))
        }

        if (data!!.location!!.locName != null && !data!!.location!!.locName.equals("")) {
            binding.tvProductName.setText(AppUtils.capitalize(data!!.location!!.locName.toString()))
        }

        if (data!!.location!!.images != null && data!!.location!!.images!!.size > 0) {
            urlList.clear()
            urlList = data!!.location!!.images!!
            binding.sliderView.llSearchView.visibility = View.VISIBLE
            binding.sliderView.rlBannerView.visibility = View.VISIBLE
            val pagerAdapter: PagerAdapter =
                ProductBannerAdapter(activity!!, urlList!!)
            binding.sliderView.pager!!.setAdapter(pagerAdapter)
            binding.sliderView.pager!!.addOnPageChangeListener(object :
                ViewPager.OnPageChangeListener {
                override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
                override fun onPageSelected(i: Int) {
                }

                override fun onPageScrollStateChanged(i: Int) {}
            })

            if (data!!.location!!.images!!.size > 1) {
                binding.sliderView.imgLeft.visibility = View.VISIBLE
                binding.sliderView.imgRight.visibility = View.VISIBLE
            } else {
                binding.sliderView.imgLeft.visibility = View.GONE
                binding.sliderView.imgRight.visibility = View.GONE
            }


            if (data!!.location!!.averageRating != null && data!!.location!!.averageRating!! > 0) {
                binding.sliderView.txtRateCount.setText("" + data!!.location!!.averageRating)
            } else {
                binding.sliderView.txtRateCount.setText("0")
            }

            binding.sliderView.imgLeft.setOnClickListener()
            {

                binding.sliderView.pager.setCurrentItem(binding.sliderView.pager.getCurrentItem() - 1);
            }

            binding.sliderView.imgRight.setOnClickListener()
            {
                binding.sliderView.pager.setCurrentItem(binding.sliderView.pager.getCurrentItem() + 1);
            }
        }

        if (data!!.relatedData != null && data!!.relatedData!!.size > 0) {
            arrayListRecommendation = data!!.relatedData!!
            val adapterProduct = RecommendationAdapter(
                arrayListRecommendation
            )
            val mLayoutManagerProduct: RecyclerView.LayoutManager =
                LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            binding.recyclerRecommendations.itemAnimator = DefaultItemAnimator()
            binding.recyclerRecommendations.layoutManager = mLayoutManagerProduct
            binding.recyclerRecommendations.adapter = adapterProduct
            adapterProduct.setOnItemClickLister(object :
                RecommendationAdapter.OnItemClickLister {
                override fun itemClicked(view: View?, position: Int, data: RelatedDataItem) {
                    startActivity(
                        Intent(activity, ProductDetailActivity::class.java)
                            .putExtra(
                                "Location_ID",
                                data.id.toString()
                            )
                    )
                    AppUtils.startFromRightToLeft(activity!!)
                }
            })
        }
    }

    private fun dialogeAddToCartApp(data: AddToCartResponse) {
        val dialog = Dialog(this)
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
        txtMessage.text = data.message
        txtOkay.setOnClickListener {
            dialog.dismiss()
            RestConstant.ContinueShopping = "1"
            pushActivityToFragment(CartFragment())
        }
        dialog.show()
    }

    private var expiryselectedDate = ""
    private var calendar = Calendar.getInstance()
    fun datePickerDialogFinal() {
        val datePickerDialog = activity?.let {
            DatePickerDialog(
                it, R.style.datepicker,
                { datePicker, year, month, day ->
                    val simpledateformat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    val newDate = Calendar.getInstance()
                    newDate[year, month] = day
                    expiryselectedDate = simpledateformat.format(newDate.time)
                    binding.txtBookingDate.setText(expiryselectedDate)
                    strBookingDate = expiryselectedDate
                    val outFormat = SimpleDateFormat("EEEE")
                    val goal = outFormat.format(newDate.time)
                    Log.e("TAG", "datePickerDialogFinal: "+goal )
                    Log.e("TAG", "datePickerDialogFinaldayList: "+dayList )
                    if (dayList.contains(goal))
                    { timeSlotApi()
                    }
                    else
                    {
                        binding.txtTimeSlotNotFound.visibility = View.VISIBLE
                        binding.recyclerviewTimeSlot.visibility = View.GONE
                    }

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(
                    Calendar.DAY_OF_MONTH
                )
            )
        }
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, +3)
        datePickerDialog?.datePicker?.minDate = calendar.timeInMillis

        datePickerDialog?.datePicker?.maxDate = cal.timeInMillis

        datePickerDialog?.show()
    }

    private fun dialogQuantity() {
        var arrayListQuantity = ArrayList<String>()
        arrayListQuantity.clear()
        for (i in 0 until strQuantityTotalLength) {
            arrayListQuantity.add("" + (i + 1))
        }
        val dialog = BottomSheetDialog(activity, R.style.BottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.dialgoe_quantity, null)
        val recyclerview: RecyclerView = view.findViewById(R.id.recyclerSelectCategory)
        val imgClose: AppCompatImageView = view.findViewById(R.id.imgCloseDialog)
        val tvTitle: AppCompatTextView = view.findViewById(R.id.tvTitle)
        val adapter = QuantityAdapter(arrayListQuantity)
        val mLayoutManager1: RecyclerView.LayoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerview.itemAnimator = DefaultItemAnimator()
        recyclerview.layoutManager = mLayoutManager1
        recyclerview.adapter = adapter
        adapter.setOnItemClickLister(object :
            QuantityAdapter.OnItemClickLister {
            override fun itemClicked(view: View?, position: Int) {
                binding.txtQuantity.text = arrayListQuantity[position]
                strQuantity = Integer.parseInt(binding.txtQuantity.text as String)
                dialog.cancel()
            }
        })

        imgClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    fun pushActivityToFragment(fragment: Fragment?) {
        if (fragment == null) return
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, fragment)
            .addToBackStack(null)
            .commit()
    }


}