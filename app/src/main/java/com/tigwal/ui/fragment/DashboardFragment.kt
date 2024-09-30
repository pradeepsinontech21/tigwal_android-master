package com.tigwal.ui.fragment

//import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import android.Manifest
import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.tigwal.R
import com.tigwal.app.rest.RestConstant
import com.tigwal.base.BaseFragment
import com.tigwal.data.api.Resource
import com.tigwal.data.model.banners.BannerDataItem
import com.tigwal.data.model.getcategory.DataItem
import com.tigwal.databinding.FragmentDashboardBinding
import com.tigwal.ui.activity.*
import com.tigwal.ui.adapter.DashboardCategoryAdapter
import com.tigwal.ui.adapter.DashboardRecommendationAdapter
import com.tigwal.ui.view_model.home.DashboardViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.MySharedPreferences
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.io.IOException
import java.util.*
import javax.xml.datatype.DatatypeFactory


@SuppressLint("UseRequireInsteadOfGet")
class DashboardFragment : BaseFragment(), KodeinAware, View.OnClickListener {
    private val factory: DatatypeFactory by instance()
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var binding: FragmentDashboardBinding
    override val kodein: Kodein by closestKodein()
    var arrayListCategory = ArrayList<DataItem>()
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private val PLACE_PICKER_REQUEST_CODE = 159
    var str_latitude: String = "0.0"
    var str_longitude: String = "0.0"
    var strAddress: String = ""
    var currentPage: Int = 0
    var bannerlist = ArrayList<BannerDataItem>()

    companion object {
    }

    override fun getIntentData() {

    }

    override fun clickListener() {
        binding.toolBar.imgProfile.setOnClickListener(this)
        binding.toolBar.imgSetting.setOnClickListener(this)
        binding.txtViewAll.setOnClickListener(this)
        binding.imgCurrentLocation.setOnClickListener(this)
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLD(requireActivity())
        binding.txtAddress.typeface = AppUtils.getMIDIUM(requireActivity())
        binding.txtCategoryTitle.typeface = AppUtils.getBOLD(requireActivity())
        binding.txtViewAll.typeface = AppUtils.getMIDIUM(requireActivity())
        binding.txtRecommendation.typeface = AppUtils.getBOLD(requireActivity())
    }


    @SuppressLint("Range")
    override fun onCreateFragmentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)

        setupViewModel()
        setupObservers()
        getIntentData()
        clickListener()
        binding.txtSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId === EditorInfo.IME_ACTION_DONE) {
                if (binding.txtSearch.text.toString().trim().isEmpty()) {
                    Toast.makeText(
                        activity,
                        "" + resources.getString(R.string.enter_search_keyword),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    startActivity(
                        Intent(activity, SearchProductListActivity::class.java)
                            .putExtra("LATITUDE", str_latitude)
                            .putExtra("LONGITUDE", str_longitude)
                            .putExtra("SEARCH", "" + binding.txtSearch.text.toString())
                    )
                    AppUtils.startFromRightToLeft(activity!!)
                }
                return@setOnEditorActionListener true
            }
            false
        }


        mFusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }
        // Category
        if (Connectivity.isConnected(activity)) {
            dashboardViewModel.getCategoryApi(
                RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken
            )
            dashboardViewModel.bannerApi(RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken)
            dashboardViewModel.recommendationApi(RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken)
        } else {
            Toast.makeText(
                activity,
                "" + resources.getString(R.string.noInternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }

        if (!MySharedPreferences.getMySharedPreferences()?.userId.equals("")) {
            binding.toolBar.imgProfile.visibility = View.VISIBLE
            binding.toolBar.imgSetting.visibility = View.GONE
        } else {
            binding.toolBar.imgProfile.visibility = View.GONE
            binding.toolBar.imgSetting.visibility = View.VISIBLE
        }

        getLastLocation()

        return binding.getRoot()
    }

    //click
    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.imgProfile -> {
                startActivity(
                    Intent(activity, ProfileActivity::class.java)
                )
                AppUtils.startFromRightToLeft(activity!!)
            }

            R.id.imgSetting -> {
                pushFragment(
                    SettingFragment()
                )
            }

            R.id.imgCurrentLocation -> {
                dialogLocation()
            }

            R.id.txtViewAll -> {
                startActivity(
                    Intent(activity, ViewAllCategoryActivity::class.java)
                )
                AppUtils.startFromRightToLeft(activity!!)
            }
        }
    }

    private fun placePicker() {
        if (!Places.isInitialized()) {
            Places.initialize(
                activity,
                resources.getString(R.string.GOOGLE_ANDROID_PLACE_API_KEY)
            )

            Log.e(
                "Place api not initlized", Places.initialize(
                    activity,
                    resources.getString(R.string.GOOGLE_ANDROID_PLACE_API_KEY)
                ).toString()
            )
        }
        val fields: List<Place.Field> = Arrays.asList<Place.Field>(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS,
            Place.Field.TYPES
        )
        val intent: Intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields
        )
            .build(activity)


        Log.e("Place api not int", intent.type.toString())
        startActivityForResult(intent, PLACE_PICKER_REQUEST_CODE)
    }

    //set view model
    private fun setupViewModel() {
        when {
            activity != null -> {
                dashboardViewModel = ViewModelProviders.of(activity as FragmentActivity)
                    .get(DashboardViewModel::class.java) // you can either pass activity object
                binding.viewmodel = dashboardViewModel
            }

            else -> {
                dashboardViewModel = ViewModelProviders.of(this)
                    .get(DashboardViewModel::class.java) // or pass fragment object, both are not possible at same time.
                binding.viewmodel = dashboardViewModel
            }
        }
    }

    // Banner Adapter
    inner class BannerAdapter(
        private val context: Context,
        private val urlList: ArrayList<BannerDataItem>
    ) : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflater =
                context.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view: View? = null
            view = inflater.inflate(R.layout.row_banner_imgage, container, false)
            container.addView(view)
            val imgBanner: AppCompatImageView = view!!.findViewById(R.id.imgBanner)

            if (urlList.get(position)!!.bannerImage != null && !urlList.get(position)!!.bannerImage.equals(
                    ""
                )
            ) {
                Glide.with(Objects.requireNonNull(activity)!!)
                    .load(Uri.parse(urlList.get(position)!!.bannerImage))
                    .placeholder(R.drawable.img_placeholder).fitCenter()
                    .into(imgBanner)
            }
            if (urlList.get(position).vendorId!! > 0) {
                imgBanner.setOnClickListener {
                    startActivity(
                        Intent(activity, ProductListActivity::class.java)
                            .putExtra("SubCategoryID", "")
                            .putExtra("VENDOR_ID", urlList.get(position)!!.vendorId.toString())
                    )
                }
            } else {
//                Toast.makeText(activity!!,"No action available",Toast.LENGTH_LONG).show()
            }
            AppUtils.startFromRightToLeft(activity!!)


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

    fun getImage(imageName: String?): Int {
        return resources.getIdentifier(imageName, "drawable", activity!!.packageName)
    }

    //set observer
    private fun setupObservers() {

        dashboardViewModel.getCategoryApiResponse.observe(
            this
        ) { response ->
            when (response) {
                is Resource.Success -> {
//                        (activity as DashboardActivity?)!!.rlProgressView.visibility = View.GONE
                    Log.d(
                        "Response",
                        "====== getCategoryApiResponse ====> " + Gson().toJson(response)
                    )
                    response.data?.let { categoryResponse ->
                        if (categoryResponse.code == 200) {
                            if (categoryResponse.status == true) {
                                if (categoryResponse.data != null && categoryResponse.data!!.size > 0) {
                                    binding.recyclerCategory.visibility = View.VISIBLE
                                    binding.txtCategoryTitle.visibility = View.VISIBLE
                                    binding.txtViewAll.visibility = View.VISIBLE
                                    arrayListCategory.clear()
                                    for (i in 0..categoryResponse.data!!.size) {
                                        if (i <= 2) {
                                            arrayListCategory.add(
                                                categoryResponse.data.get(
                                                    i
                                                )!!
                                            )
                                        }
                                    }
                                    val adapter = DashboardCategoryAdapter(
                                        arrayListCategory
                                    )
                                    val recycleLayoutManager = GridLayoutManager(
                                        activity,
                                        2,
                                        GridLayoutManager.VERTICAL,
                                        false
                                    )
                                    recycleLayoutManager.spanSizeLookup =
                                        object : SpanSizeLookup() {
                                            override fun getSpanSize(position: Int): Int {
                                                return if (arrayListCategory.size % 2 === 0) {
                                                    1
                                                } else {
                                                    if (position == arrayListCategory.size - 1) 2 else 1
                                                }
                                            }
                                        }
                                    binding.recyclerCategory.itemAnimator =
                                        DefaultItemAnimator()
                                    binding.recyclerCategory.layoutManager =
                                        recycleLayoutManager
                                    binding.recyclerCategory.adapter = adapter
                                    adapter.setOnItemClickLister(object :
                                        DashboardCategoryAdapter.OnItemClickLister {
                                        override fun itemClicked(
                                            view: View?,
                                            position: Int
                                        ) {
                                            if (!arrayListCategory.get(position).subcategory.equals(
                                                    "0"
                                                )
                                            ) {
                                                startActivity(
                                                    Intent(
                                                        activity,
                                                        SubCategoryActivity::class.java
                                                    ).putExtra(
                                                        "SubCategoryID",
                                                        arrayListCategory.get(position).categoryId
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
                                                            arrayListCategory.get(position).categoryId
                                                        )
                                                        .putExtra("VENDOR_ID", "")

                                                )
                                                AppUtils.startFromRightToLeft(activity!!)
                                            }
                                        }
                                    })
                                } else {
                                    binding.recyclerCategory.visibility = View.GONE
                                    binding.txtCategoryTitle.visibility = View.GONE
                                    binding.txtViewAll.visibility = View.GONE
                                }
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
//                        (activity as DashboardActivity?)!!.rlProgressView.visibility = View.GONE
                    Toast.makeText(
                        activity,
                        "" + response.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Resource.Loading -> {
//                        (activity as DashboardActivity?)!!.rlProgressView.visibility =
//                            View.VISIBLE
                }
            }
        }

        dashboardViewModel.bannerApiResponse.observe(
            this
        ) { response ->
            when (response) {
                is Resource.Success -> {
//                    (activity as DashboardActivity?)!!.rlProgressView.visibility = View.GONE
                    Log.d(
                        "Response",
                        "====== bannerApiResponse ====> " + Gson().toJson(response)
                    )
                    response.data?.let { bannerResponse ->
                        if (bannerResponse.code == 200) {
                            if (bannerResponse.data != null && bannerResponse.data!!.size > 0) {
                                bannerlist.clear()
                                try {
                                    for (i in 0..bannerResponse.data!!.size) {
                                        bannerlist.add(
                                            bannerResponse.data.get(
                                                i
                                            )!!
                                        )
                                    }


                                } catch (e: Exception) {
                                    Log.e("error", e.toString())
                                }
                                setStaticBanner()
                                setBannerAdaapter(bannerlist)

                            } else {
                                bannerlist.clear()
                                setStaticBanner()
                                setBannerAdaapter(bannerlist)
                                binding.sliderView.pager.visibility = View.GONE
                                binding.sliderView.dot3.visibility = View.GONE
                                binding.sliderView.llSearchView.visibility = View.VISIBLE
                                binding.sliderView.imgDummyAds.visibility = View.VISIBLE
                            }


                        } else {
                            Toast.makeText(
                                activity,
                                "" + bannerResponse.message,
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
//                    (activity as DashboardActivity?)!!.rlProgressView.visibility =
                    View.VISIBLE
                }
            }
        }

        dashboardViewModel.recommendationApiResponse.observe(
            this
        ) { response ->
            when (response) {
                is Resource.Success -> {
//                        (activity as DashboardActivity?)!!.rlProgressView.visibility = View.GONE
                    Log.d(
                        "Response",
                        "====== recommendation api ====> " + Gson().toJson(response)
                    )
                    response.data?.let { recommendationResponse ->
                        if (recommendationResponse.code == 200) {
                            if (recommendationResponse.data != null && recommendationResponse.data!!.size > 0) {
                                binding.recyclerRecommendations.visibility = View.VISIBLE
                                binding.txtRecommendation.visibility = View.VISIBLE
                                val adapterProduct =
                                    DashboardRecommendationAdapter(
                                        recommendationResponse.data
                                    )
                                val mLayoutManagerProduct: RecyclerView.LayoutManager =
                                    LinearLayoutManager(
                                        activity,
                                        RecyclerView.HORIZONTAL,
                                        false
                                    )
                                binding.recyclerRecommendations.itemAnimator =
                                    DefaultItemAnimator()
                                binding.recyclerRecommendations.layoutManager =
                                    mLayoutManagerProduct
                                binding.recyclerRecommendations.adapter = adapterProduct
                                binding.txtRecommendation.visibility = View.VISIBLE
                                adapterProduct.setOnItemClickLister(object :
                                    DashboardRecommendationAdapter.OnItemClickLister {

                                    override fun itemClicked(
                                        view: View?,
                                        position: Int,
                                        data: com.tigwal.data.model.recommendation.DataItem
                                    ) {


                                        startActivity(
                                            Intent(
                                                activity,
                                                ProductDetailActivity::class.java
                                            )
                                                .putExtra("Location_ID", "" + data.id)
                                        )
                                        AppUtils.startFromRightToLeft(activity!!)
                                    }
                                })
                            } else {
                                binding.recyclerRecommendations.visibility = View.GONE
                                binding.txtRecommendation.visibility = View.GONE
//                                    binding.sliderView.llSearchView.visibility = View.GONE
                            }

                        } else {
                            Toast.makeText(
                                activity,
                                "" + recommendationResponse.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                is Resource.Error -> {
//                        (activity as DashboardActivity?)!!.rlProgressView.visibility = View.GONE
                    Toast.makeText(
                        activity,
                        "" + response.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Resource.Loading -> {
//                        (activity as DashboardActivity?)!!.rlProgressView.visibility =
                    View.VISIBLE
                }
            }
        }
    }

    private fun setStaticBanner() {
        bannerlist.add(
            BannerDataItem(
                bannerImage = "file:///android_asset/images/img_banner_dummy.jpeg",
                vendorId = 0
            )
        )
        bannerlist.add(
            BannerDataItem(
                bannerImage = "file:///android_asset/images/tigwal_banner_second.jpeg",
                vendorId = 0
            )
        )
    }

    private fun setBannerAdaapter(bannerlist: ArrayList<BannerDataItem>) {
        binding.sliderView.llSearchView.visibility = View.VISIBLE
        val pagerAdapter: PagerAdapter =
            BannerAdapter(activity!!, bannerlist)
        binding.sliderView.pager!!.setAdapter(pagerAdapter)
        binding.sliderView.dot3!!.setViewPager(binding.sliderView.pager!!)
        binding.sliderView.pager!!.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                i: Int,
                v: Float,
                i1: Int
            ) {
                currentPage = i
            }

            override fun onPageSelected(i: Int) {
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })
        // Timer..
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                try {
                    activity!!.runOnUiThread(Runnable {
                        if (activity != null) {
                            if (currentPage >= bannerlist!!.size) {
                                currentPage = 0
                            } else {
                                binding.sliderView.pager.setCurrentItem(
                                    currentPage++,
                                    false
                                )
                            }
                        }
                    })
                } catch (e: Exception) {

                }
            }
        }, 0, 2000)

    }


    //get result
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.e("result code", resultCode.toString())

        if (requestCode === PLACE_PICKER_REQUEST_CODE && resultCode === Activity.RESULT_OK) {
            val place: Place = Autocomplete.getPlaceFromIntent(data)
            str_latitude = place.getLatLng().latitude.toString()
            str_longitude = place.getLatLng().longitude.toString()
            strAddress = place.getAddress()
            binding.txtAddress.setText(strAddress);
            binding.imgAddress.visibility = View.VISIBLE
        }


    }

    @SuppressLint("SetTextI18n")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        activity!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        activity!!,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions()
                    return
                }
                mFusedLocationClient!!.lastLocation
                    .addOnCompleteListener { task ->
                        val location = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            val addresses: List<Address>
                            val geocoder: Geocoder? =
                                context?.let { Geocoder(it, Locale.getDefault()) }
                            try {
                                try {
                                    addresses = geocoder!!.getFromLocation(
                                        location.latitude,
                                        location.longitude,
                                        1
                                    )!!
                                    if (addresses != null) {
                                        val address =
                                            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                        binding.txtAddress.text = "" + address
                                        binding.imgAddress.visibility = View.VISIBLE
                                    } else {
                                        binding.txtAddress.text = ""
                                        binding.imgAddress.visibility = View.VISIBLE
                                    }
                                } catch (e: Exception) {

                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context!!,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context!!, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val permission = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        requestPermissions(permission, 44)
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager =
            context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            44 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    getLastLocation()
                } else {
                    Toast.makeText(
                        activity,
                        "" + resources.getString(R.string.permision_denied),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private
    val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
        }
    }

    override fun onResume() {
        super.onResume()
        if (MySharedPreferences.getMySharedPreferences()!!.userImage != null && !MySharedPreferences.getMySharedPreferences()!!.userImage.equals(
                ""
            )
        ) {
            Glide.with(Objects.requireNonNull(activity)!!)
                .load(MySharedPreferences.getMySharedPreferences()!!.userImage)
                .placeholder(R.drawable.img_placeholder).fitCenter()
                .into(binding.toolBar.imgProfile)
        }
    }

    private fun dialogLocation() {
        val dialog = BottomSheetDialog(activity!!, R.style.BottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.bottom_location_dialoge, null)
        val tvTitle: AppCompatTextView = view.findViewById(R.id.tvTitle)
        val txtCurrentLocation: AppCompatTextView = view.findViewById(R.id.txtCurrentLocation)
        val txtSelectLocation: AppCompatTextView = view.findViewById(R.id.txtSelectLocation)
//        imgClose.visibility = View.GONE
//        imgClose.setOnClickListener {
//            dialog.dismiss()
//        }
        dialog.setContentView(view)
        txtCurrentLocation.setOnClickListener {
            dialog.dismiss()
            getLastLocation()
        }

        txtSelectLocation.setOnClickListener {
            dialog.dismiss()
            placePicker()
        }
        dialog.show()
    }


}