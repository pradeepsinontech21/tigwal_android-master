package com.tigwal.ui.activity

import android.Manifest
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.hbb20.CountryCodePicker.OnCountryChangeListener
import com.tigwal.R
import com.tigwal.app.rest.RestConstant
import com.tigwal.base.BaseActivity
import com.tigwal.data.api.Resource
import com.tigwal.data.api.UserParams
import com.tigwal.databinding.ActivityProfileBinding
import com.tigwal.ui.factory.ProfileViewFactory
import com.tigwal.ui.view_model.profile.ProfileViewModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.Connectivity
import com.tigwal.utils.LanguagePreferences
import com.tigwal.utils.MySharedPreferences
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.RequestBody
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : BaseActivity(), KodeinAware, View.OnClickListener {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel
    override val kodein: Kodein by kodein()
    private val factory: ProfileViewFactory by instance()
    private var userChoosenTask = " "
    private var picturePath: String? = ""
    var file_upload: File? = null
    var filename: String? = ""
    var imageUri: Uri? = null
    private val PLACE_PICKER_REQUEST_CODE = 159
    var str_latitude: String = ""
    var str_longitude: String = ""
    var strAddress: String = ""
    var strCountryCode: String = ""
    var strcountryShortName: String = ""
    var strBookingDate: String = ""
    var selectedGender: String = ""
    var strselectedCountry: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_profile
        )
        setupViewModel()
        setupObservers()
        getIntentData()
        clickListener()
        setFontTypeface()
        binding.toolBar.txtHeaderTitle.setText(resources.getString(R.string.profile))
        binding.toolBar.ivEdit.isVisible = true

        if (Connectivity.isConnected(activity)) {
            MySharedPreferences.getMySharedPreferences()!!.authToken?.let {
                viewModel.getProfileAPi(
                    it
                )
            }
        } else {
            Toast.makeText(
                activity,
                "" + resources.getString(R.string.noInternetConnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val PICK_IMAGE_REQUEST_CODE = 1000
        const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 1001
        private const val PERMISSION_CODE = 1000
        private const val PICK_CAMERA_REQUEST = 1001
    }


    override fun getIntentData() {
    }

    @SuppressLint("Range")
    override fun clickListener() {
        binding.toolBar.ivBack.setOnClickListener(this)
        binding.toolBar.ivEdit.setOnClickListener(this)
        binding.btnUpdate.setOnClickListener(this)
        binding.ivUploadImg.setOnClickListener(this)
        binding.txtAddress.setOnClickListener(this)
        binding.txtCountry.setOnClickListener(this)
        binding.imgProfilePic.setOnClickListener(this)
        binding.btnDeleteUser.setOnClickListener(this)
        binding.rlBirthdate.setOnClickListener(this)
        binding.rlGender.setOnClickListener(this)

        binding.countrypicker.setOnCountryChangeListener(OnCountryChangeListener
        {
            binding.txtSelectedCountryCode.visibility = View.GONE
            binding.countrypicker.alpha = 255F
        })
    }

    override fun setFontTypeface() {
        binding.toolBar.txtHeaderTitle.typeface = AppUtils.getBOLDMIDIUM(activity)
        binding.etProfileFullName.typeface = AppUtils.getLIGHT(activity)
        binding.etProfileEmail.typeface = AppUtils.getLIGHT(activity)
        binding.etProfileMobile.typeface = AppUtils.getLIGHT(activity)
        binding.txtSelectedCountryCode.typeface = AppUtils.getLIGHT(activity)
        binding.txtAddress.typeface = AppUtils.getLIGHT(activity)
        binding.btnUpdate.typeface = AppUtils.getLIGHT(activity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                onBackPressed()
            }

            R.id.img_profilePic -> {
                popupView(v)
            }

            R.id.btnDeleteUser -> {
                dialogeDeleteUserApp()
            }

            R.id.iv_edit -> {
                binding.etProfileFullName.isEnabled = true
                binding.etProfileEmail.isEnabled = false
                binding.etProfileMobile.isEnabled = true
                binding.txtAddress.isEnabled = true
                binding.countrypicker.setCcpClickable(true)
                binding.ivUploadImg.isVisible = true
                binding.btnUpdate.isVisible = true
                binding.toolBar.ivEdit.isVisible = false

                binding.rlBirthdate.isEnabled = true
                binding.txtDate.isEnabled = true
                binding.rlGender.isEnabled = true
                binding.txtGender.isEnabled = true

                binding.rlCountry.isEnabled = true
                binding.txtCountry.isEnabled = true


            }

            R.id.btn_update -> {

                if (binding.etProfileFullName.text.toString().equals("")) {
                    Toast.makeText(
                        activity,
                        "" + resources.getString(R.string.name_is_required),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (binding.etProfileEmail.text.toString().equals("")) {
                    Toast.makeText(
                        activity,
                        "" + resources.getString(R.string.please_enter_email),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (!AppUtils.isEmailValid(binding.etProfileEmail.text.toString())) {
                    Toast.makeText(
                        applicationContext,
                        "" + resources.getString(R.string.please_enter_valid_email),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (binding.etProfileMobile.text.toString().equals("")) {
                    Toast.makeText(
                        activity,
                        "" + resources.getString(R.string.please_enter_mobile),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (binding.txtAddress.text.toString().equals("")) {
                    Toast.makeText(
                        activity,
                        "" + resources.getString(R.string.address_is_required),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    hideKeyboard(activity)

                    strcountryShortName = binding.countrypicker.selectedCountryNameCode.toString()
                    strCountryCode = binding.countrypicker.selectedCountryCodeWithPlus.toString()

                    val params = HashMap<String, RequestBody?>()
                    params[UserParams.name] =
                        AppUtils.getRequestBody(binding.etProfileFullName.text.toString())
                    params[UserParams.email] =
                        AppUtils.getRequestBody(binding.etProfileEmail.text.toString())
                    params[UserParams.country_short_name] =
                        AppUtils.getRequestBody(strcountryShortName)
                    params[UserParams.country_code] = AppUtils.getRequestBody(strCountryCode)
                    params[UserParams.mobile_no] =
                        AppUtils.getRequestBody(binding.etProfileMobile.text.toString())
                    params[UserParams.address] =
                        AppUtils.getRequestBody(binding.txtAddress.text.toString())
                    params[UserParams.lats] =
                        AppUtils.getRequestBody("" + str_latitude)
                    params[UserParams.longs] =
                        AppUtils.getRequestBody("" + str_longitude)
                    params[UserParams.birth_date] =
                        AppUtils.getRequestBody("" + strBookingDate)
                    params[UserParams.country] =
                        AppUtils.getRequestBody("" + strselectedCountry)
                    params[UserParams.gender] =
                        AppUtils.getRequestBody("" + selectedGender)
                    binding.etProfileFullName.text!!.clear()
                    binding.etProfileEmail.text!!.clear()
                    binding.etProfileMobile.text!!.clear()
                    strcountryShortName = ""
                    binding.txtAddress.text = ""
                    strCountryCode = ""
                    viewModel.updateProfileAPi(
                        MySharedPreferences.getMySharedPreferences()!!.authToken!!,
                        params, picturePath
                    )
                }
            }

            R.id.iv_uploadImg -> {
                selectImage()
            }

            R.id.txtCountry -> {
                placePicker()
            }

            R.id.txtAddress -> {
                placePicker()
            }

            R.id.rlBirthdate -> {
                datePickerDialogFinal()
            }

            R.id.rlGender -> {
                val gender = arrayOf<CharSequence>("Male", "Female", "Other")
                val alert = AlertDialog.Builder(activity)
                val title = alert.setTitle("Select Gender")
                var strSelectedPos = -1
                if (selectedGender == "Male") {
                    strSelectedPos = 0
                } else if (selectedGender == "Female") {
                    strSelectedPos = 1
                } else if (selectedGender == "Other") {
                    strSelectedPos = 2
                }
                alert.setSingleChoiceItems(
                    gender,
                    strSelectedPos,
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog!!.dismiss()
                            if (gender[which] === "Male") {
                                selectedGender = "Male"
                                binding.txtGender.text = selectedGender
                            } else if (gender[which] === "Female") {
                                selectedGender = "Female"
                                binding.txtGender.text = selectedGender
                            } else if (gender[which] === "Other") {
                                selectedGender = "Other"
                                binding.txtGender.text = selectedGender
                            }
                        }
                    })
                alert.show()
            }
        }
    }


    private var expiryselectedDate = ""
    private var calendar = Calendar.getInstance()
    fun datePickerDialogFinal() {
        val datePickerDialog = activity?.let {
            DatePickerDialog(
                it, R.style.datepicker,
                { datePicker, year, month, day ->
                    val userAge: Calendar = GregorianCalendar(year, month, day)
                    val minAdultAge: Calendar = GregorianCalendar()
                    minAdultAge.add(Calendar.YEAR, -18)
                    if (minAdultAge.before(userAge)) {
                        Toast.makeText(
                            activity,
                            "" + resources.getString(R.string.validate_birthdate),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val simpledateformat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                        val newDate = Calendar.getInstance()
                        newDate[year, month] = day
                        expiryselectedDate = simpledateformat.format(newDate.time)
                        binding.txtDate.setText(expiryselectedDate)
                        strBookingDate = expiryselectedDate
                    }

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(
                    Calendar.DAY_OF_MONTH
                )
            )

        }
        datePickerDialog?.datePicker?.maxDate = calendar.timeInMillis
        datePickerDialog?.show()
    }


    //set view model
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
        binding.viewmodel = viewModel
    }

    //set observer
    private fun setupObservers() {
        viewModel.profileApiResponse.observe(
            this
        ) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressDialog()
                    Log.d(
                        "Response", "====== profileApiResponse ====> " + Gson().toJson(response)
                    )

                    response.data?.let { loginResponse ->
                        if (loginResponse.code == 200) {
                            if (loginResponse.status == true) {

                                if (loginResponse.data!!.name != null && !loginResponse.data!!.name.equals(
                                        ""
                                    )
                                ) {
                                    binding.etProfileFullName.setText("" + loginResponse.data!!.name)
                                    binding.txtNameTitle.text = "" + AppUtils.capitalize(
                                        loginResponse.data!!.name.toString()
                                    )
                                }




                                if (loginResponse.data!!.mobileNo != null && !loginResponse.data!!.mobileNo.equals(
                                        ""
                                    )
                                ) {
                                    binding.etProfileMobile.setText("" + loginResponse.data!!.mobileNo)
                                }

                                if (loginResponse.data!!.email != null && !loginResponse.data!!.email.equals(
                                        ""
                                    )
                                ) {
                                    binding.etProfileEmail.setText("" + loginResponse.data!!.email)
                                    binding.txtEmailTitle.setText("" + loginResponse.data!!.email)
                                }

                                if (loginResponse.data!!.countryCode != null && !loginResponse.data!!.countryCode.equals(
                                        ""
                                    )
                                ) {
                                    binding.txtSelectedCountryCode.setText("" + loginResponse.data!!.countryCode)
                                    strcountryShortName =
                                        "" + loginResponse.data!!.country_short_name
                                    strCountryCode = "" + loginResponse.data!!.countryCode

                                }

                                if (loginResponse.data!!.address != null && !loginResponse.data!!.address.equals(
                                        ""
                                    )
                                ) {
                                    binding.txtAddress.setText("" + loginResponse.data!!.address)
                                    str_latitude = loginResponse.data!!.lats!!
                                    str_longitude = loginResponse.data!!.longs!!

                                    if (loginResponse.data!!.country != null) {
                                        strselectedCountry = loginResponse.data!!.country!!
                                        binding.txtCountry.setText(strselectedCountry)
                                    }
                                }

                                if (loginResponse.data!!.birth_date != null && !loginResponse.data!!.birth_date.equals(
                                        ""
                                    )
                                ) {
                                    strBookingDate = loginResponse.data!!.birth_date!!
                                    binding.txtDate.setText(strBookingDate)
                                }

                                if (loginResponse.data!!.gender != null && !loginResponse.data!!.gender.equals(
                                        ""
                                    )
                                ) {
                                    selectedGender = loginResponse.data!!.gender!!
                                    binding.txtGender.setText(selectedGender)
                                }

                                MySharedPreferences.getMySharedPreferences()!!.isNotification =
                                    loginResponse.data?.enable_notification
                                if (loginResponse.data!!.imageurl != null && !loginResponse.data!!.imageurl.equals(
                                        ""
                                    )
                                ) {
                                    Glide.with(Objects.requireNonNull(activity)!!)
                                        .load(loginResponse.data!!.imageurl)
                                        .placeholder(R.drawable.default_profile).fitCenter()
                                        .into(binding.imgProfilePic)
                                    MySharedPreferences.getMySharedPreferences()!!.userImage =
                                        loginResponse.data!!.imageurl
                                }

                                binding.rlBirthdate.isEnabled = false
                                binding.txtDate.isEnabled = false
                                binding.rlGender.isEnabled = false
                                binding.txtGender.isEnabled = false
                                binding.rlCountry.isEnabled = false
                                binding.txtCountry.isEnabled = false

                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + loginResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                activity,
                                "" + loginResponse.message,
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

        viewModel.updateProfileApiResponse.observe(
            this
        ) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressDialog()
                    Log.d(
                        "Response", "====== profileApiResponse ====> " + Gson().toJson(response)
                    )

                    response.data?.let { loginResponse ->
                        if (loginResponse.code == 200) {
                            if (loginResponse.status == true) {
                                Toast.makeText(
                                    activity,
                                    "" + resources.getString(R.string.update_profile_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.etProfileFullName.isEnabled = false
                                binding.etProfileEmail.isEnabled = false
                                binding.etProfileMobile.isEnabled = false
                                binding.countrypicker.setCcpClickable(false)
                                binding.txtAddress.isEnabled = false
                                binding.ivUploadImg.isVisible = false
                                binding.btnUpdate.isVisible = false
                                binding.toolBar.ivEdit.isVisible = true


                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + loginResponse.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                activity,
                                "" + loginResponse.message,
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

        viewModel.deleteUserApiCallResponse.observe(
            this,
            { response ->
                when (response) {
                    is Resource.Success -> {
                        hideProgressDialog()
                        Log.d(
                            "Response",
                            "====== deleteUserApiCallResponse ====> " + Gson().toJson(response)
                        )
                        response.data?.let { chatlistResponse ->
                            if (chatlistResponse.code == 200) {
                                if (chatlistResponse.status == true) {
                                    Toast.makeText(
                                        activity,
                                        "" + chatlistResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    MySharedPreferences.getMySharedPreferences()!!
                                        .clearPreferences()
                                    var appPreferences = LanguagePreferences(activity)
                                    appPreferences.clearAll()
                                    val mySharedPreferences =
                                        MySharedPreferences.getMySharedPreferences()
                                    mySharedPreferences!!.isLogin = false
                                    val sharedPreferences: SharedPreferences =
                                        activity!!.getSharedPreferences(
                                            "p6_pref",
                                            Context.MODE_PRIVATE
                                        )
                                    sharedPreferences.edit().clear()
                                    sharedPreferences.edit().commit();
                                    sharedPreferences.edit().apply()
                                    appPreferences.clearAll()
                                    // FB
                                    LoginManager.getInstance().logOut()
                                    // Google
                                    val gso =
                                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                            .build()
                                    val googleSignInClient = GoogleSignIn.getClient(activity!!, gso)
                                    googleSignInClient.signOut()
                                    startActivity(Intent(activity, SplashActivity::class.java))
                                    AppUtils.startFromRightToLeft(activity!!)
                                    activity!!.finish()
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "" + chatlistResponse.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "" + chatlistResponse.message,
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
    }

    //image select for profile
    private fun selectImage() {

        RestConstant.takePhoto = resources.getString(R.string.camera)
        RestConstant.chooseLiberary = resources.getString(R.string.gallery)
        RestConstant.cancel = resources.getString(R.string.cancel)

        val items = arrayOf<CharSequence>(
            RestConstant.takePhoto,
            RestConstant.chooseLiberary,
            RestConstant.cancel
        )
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(RestConstant.addPhoto)
        builder.setItems(items) { dialog, item ->
            if (items[item] == RestConstant.takePhoto) {
                dialog.dismiss()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    userChoosenTask = RestConstant.takePhoto
                    if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(
                            activity!!,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_DENIED
                    ) {
                        val permission = arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        requestPermissions(permission, PERMISSION_CODE)
                    } else {
                        openCamera()
                    }
                } else {
                    openCamera()
                }
            } else if (items[item] == RestConstant.chooseLiberary) {
                dialog.dismiss()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    userChoosenTask = RestConstant.chooseLiberary
                    pickImage()
                } else {
                    pickImage()
                }
            } else if (items[item] == RestConstant.cancel) {
                dialog.dismiss()
            }
        }
        builder.show()
    }


    private fun openCamera() {

        val state = Environment.getExternalStorageState()
        var folder: File? = null
        if (state.contains(Environment.MEDIA_MOUNTED)) {
            folder = File(
                Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + "/" +
                        resources.getString(R.string.app_name)
            )
        } else {
            folder = File(
                Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + "/" + resources.getString(R.string.app_name)
            )
        }

        var success = true
        if (!folder.exists()) {
            success = folder.mkdirs()
        }
        filename = folder.absolutePath + "/" + System.currentTimeMillis() + ".jpg"
        imageUri = filename?.let { File(it) }?.let {
            FileProvider.getUriForFile(
                activity, "com.tigwal" + ".provider",
                it
            )
        }
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            imageUri
        )
        val chooserIntent = Intent.createChooser(takePictureIntent, "Capture Image")
        startActivityForResult(chooserIntent, PICK_CAMERA_REQUEST)
    }


    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
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

    //pickimage
    private fun pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if(ActivityCompat.checkSelfPermission(
                    activity,
                    READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ){
                val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                Intent.ACTION_OPEN_DOCUMENT_TREE
                startActivityForResult(i, PICK_IMAGE_REQUEST_CODE)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ActivityCompat.checkSelfPermission(
                activity,
                READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
                    ){
                val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                Intent.ACTION_OPEN_DOCUMENT_TREE
                startActivityForResult(i, PICK_IMAGE_REQUEST_CODE)
            }

        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                Intent.ACTION_OPEN_DOCUMENT_TREE
                startActivityForResult(i, PICK_IMAGE_REQUEST_CODE)
            }

        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(READ_MEDIA_IMAGES),
                    READ_EXTERNAL_STORAGE_REQUEST_CODE
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(READ_MEDIA_IMAGES),
                    READ_EXTERNAL_STORAGE_REQUEST_CODE
                )

            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_REQUEST_CODE
                )
            }

        }
    }

    //get result of image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (filename != null && filename?.let { File(it).exists() } == true) {
                file_upload = filename?.let { File(it) }
                picturePath = filename.toString()
                Glide.with(Objects.requireNonNull(activity)).load(picturePath)
                    .placeholder(R.drawable.img_placeholder).fitCenter()
                    .into(binding.imgProfilePic)
                Log.d("picturePath", "========= picturePath ======== >$picturePath")
            } else {
                Toast.makeText(
                    activity,
                    "" + resources.getString(R.string.some_error_ocurred),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (requestCode == PICK_IMAGE_REQUEST_CODE && data != null && data.data != null) {
            val uri = data.data
            val projection =
                arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor =
                uri?.let {
                    Objects.requireNonNull(activity).contentResolver
                        .query(it, projection, null, null, null)
                }!!
            Objects.requireNonNull(cursor).moveToFirst()
            val columnIndex = cursor.getColumnIndex(projection[0])
            picturePath = cursor.getString(columnIndex) // returns null
            Glide.with(Objects.requireNonNull(activity)).load(picturePath)
                .placeholder(R.drawable.img_placeholder).fitCenter()
                .into(binding.imgProfilePic)
            cursor.close()
            Log.d("picturePath", "========= picturePath ======== >$picturePath")
        } else if (requestCode == PLACE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val place: Place? = data?.let { Autocomplete.getPlaceFromIntent(it) }
            str_latitude = place!!.latLng?.latitude.toString()
            str_longitude = place.latLng?.longitude.toString()
            strAddress = place.address!!
            binding.txtAddress.text = strAddress

            val gcd = Geocoder(activity, Locale.getDefault())
            val addresses: MutableList<Address>? =
                gcd.getFromLocation(str_latitude.toDouble(), str_longitude.toDouble(), 1)
            if (addresses!!.size > 0) {
                strselectedCountry =
                    (addresses[0].countryName ?: binding.txtCountry.setText(
                        strselectedCountry
                    )).toString()

            }
        }
    }

    private fun placePicker() {
        if (!Places.isInitialized()) {
            Places.initialize(activity, resources.getString(R.string.GOOGLE_ANDROID_PLACE_API_KEY))
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
        startActivityForResult(intent, PLACE_PICKER_REQUEST_CODE)
    }

    //popup window

    private fun popupView(view: View?) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View =
            inflater.inflate(R.layout.popup_window, null)
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT
        val focusable = true
        var popupWindow = PopupWindow(popupView, width, height, focusable)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        val ivclose: AppCompatImageView =
            popupView.findViewById(R.id.ivclose) as AppCompatImageView
        val ivPreviewImage: AppCompatImageView =
            popupView.findViewById(R.id.ivPreviewImage) as AppCompatImageView
        val ivPreviewRoundUserImage: CircleImageView =
            popupView.findViewById(R.id.ivPreviewRoundUserImage) as CircleImageView
        ivPreviewRoundUserImage.visibility = View.VISIBLE
        Glide.with(Objects.requireNonNull(activity)!!)
            .load(MySharedPreferences.getMySharedPreferences()!!.userImage)
            .placeholder(R.drawable.img_placeholder).fitCenter()
            .into(ivPreviewRoundUserImage)
        ivclose.setOnClickListener()
        {
            popupWindow.dismiss()
        }
    }


    private fun dialogeDeleteUserApp() {
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
        txtMessage.text = resources.getString(R.string.delete_msg_account)
        txtYes.setOnClickListener {
            dialog.dismiss()
            if (Connectivity.isConnected(activity)) {
                viewModel.deleteUserApi(
                    RestConstant.BEARER + MySharedPreferences.getMySharedPreferences()?.authToken
                )
            } else {
                Toast.makeText(
                    applicationContext,
                    "" + resources.getString(R.string.noInternetConnection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        txtNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}