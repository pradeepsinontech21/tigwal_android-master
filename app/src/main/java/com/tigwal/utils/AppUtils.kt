package com.tigwal.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import com.tigwal.Application
import com.tigwal.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object AppUtils {

    private const val APP_TAG = "CliniBook"
    var font_BOLD: Typeface? = null
    var font_EXTRABOLD: Typeface? = null
    var font_LIGHT: Typeface? = null
    var font_MEDIUM: Typeface? = null
    var font_REG: Typeface? = null

    fun getBOLD(c: Context): Typeface? {
        font_BOLD =
            Typeface.createFromAsset(c.assets, "fonts/poppins_bold.ttf")
        return font_BOLD
    }

    fun getBOLDMIDIUM(c: Context): Typeface? {
        font_EXTRABOLD =
            Typeface.createFromAsset(c.assets, "fonts/poppins_bold_midium_new.ttf")
        return font_EXTRABOLD
    }

    fun getBOLDNEW(c: Context): Typeface? {
        font_EXTRABOLD =
            Typeface.createFromAsset(c.assets, "fonts/poppins_bold_new.ttf")
        return font_EXTRABOLD
    }

    fun getLIGHT(c: Context): Typeface? {
        font_LIGHT =
            Typeface.createFromAsset(c.assets, "fonts/poppins_light.ttf")
        return font_LIGHT
    }

    fun getREG(c: Context): Typeface? {
        font_REG =
            Typeface.createFromAsset(c.assets, "fonts/poppins_regular.ttf")
        return font_REG
    }

    fun getMIDIUM(c: Context): Typeface? {
        font_MEDIUM =
            Typeface.createFromAsset(c.assets, "fonts/poppins_medium.ttf")
        return font_MEDIUM
    }

    fun getSEMIBOLD(c: Context): Typeface? {
        font_MEDIUM =
            Typeface.createFromAsset(c.assets, "fonts/poppins_semibold.ttf")
        return font_MEDIUM
    }

    fun logString(message: String?): Int {
        return Log.i(APP_TAG, message!!)
    }

    fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun isZipCodeValid(zipCode: String?): Boolean {
        val expression = "[0-9]{5}(?:-[0-9]{4})?\$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(zipCode)
        return matcher.matches()
    }

    fun isEmpty(str: CharSequence?): Boolean {
        return str == null || str.length == 0
    }

    fun isNameValid(name: String?): Boolean {
        val expression = "^[a-zA-Z]+\$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(name)
        return matcher.matches()
    }

    fun getRequestBody(value: String?): RequestBody? {
        return value!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    }

    @JvmStatic
    fun isValidPassword(password: String?): Boolean {
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        val pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun hasInternetConnection(application: android.app.Application): Boolean {
        val connectivityManager = application.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }


    fun isPhoneValid(phone: String?): Boolean {
        val expression = "^+(?:[0-9] ?){7,9}[0-9]\$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(phone)
        return matcher.matches()
    }


    @JvmStatic
    fun getText(textView: TextView): String {
        return textView.text.toString().trim { it <= ' ' }
    }

    fun showToast(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun startFromRightToLeft(context: Context) {
        (context as Activity).overridePendingTransition(R.anim.trans_left_in, R.anim.no_animation)
    }

    @JvmStatic
    fun finishFromLeftToRight(context: Context) {
        (context as Activity).overridePendingTransition(
            R.anim.trans_right_in,
            R.anim.trans_right_out
        )
    }


    @JvmStatic
    fun startFromBottomToUp(context: Context) {
        (context as Activity).overridePendingTransition(
            R.anim.slide_in_bottom,
            R.anim.slide_out_bottom
        )
    }

    @JvmStatic
    fun startFromUpToBottom(context: Context) {
        (context as Activity).overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top)
    }


    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    /*  *//*   public static RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }*/
    @JvmStatic
    fun hideSoftKeyboard(activity: Activity) {
        val focusedView = activity.currentFocus
        if (focusedView != null) {
            val inputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(focusedView.windowToken, 0)
        }
    }

    fun showAlertDialog(context: Context, title: String?, message: String?) {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(context.getString(R.string.ok)) { dialog, which -> dialog.dismiss() }
        builder.show()
    }

    @JvmStatic
    fun roundTwoDecimals(d: Double): Double {
        val twoDForm = DecimalFormat("#.##")
        return java.lang.Double.valueOf(twoDForm.format(d))
    }

    @JvmStatic
    fun isMyServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
        val manager = (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun capitalize(capString: String): String? {
        val capBuffer = StringBuffer()
        val capMatcher: Matcher =
            Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString)
        while (capMatcher.find()) {
            capMatcher.appendReplacement(
                capBuffer,
                capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase()
            )
        }
        return capMatcher.appendTail(capBuffer).toString()
    }

    @JvmStatic
    fun timeSlotChange(time: String): String? {
        val inputPattern = "HH:mm:ss"
        val outputPattern = "HH:mm"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = ""
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }

    @JvmStatic
    fun timeSlotChangeNew(time: String): String? {
        val inputPattern = "HH:mm:ss"
        val outputPattern = "hh:mm a"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = ""
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }

    @JvmStatic
    fun isConnectedToInternet(context: Context?): Boolean {
        val cm =
            Application.application?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var netInfo: NetworkInfo? = null
        if (cm != null) {
            netInfo = cm.activeNetworkInfo
        }
        return netInfo != null && netInfo.isConnected && netInfo.isAvailable
    }


    fun convertTime(time: String): String {
        val _24HourSDF = SimpleDateFormat("HH:mm:ss", Locale.US)
        val _12HourSDF = SimpleDateFormat("hh:mm a", Locale.US)
        var _12HourDt: Date? = null
        try {
            _12HourDt = _12HourSDF.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return _24HourSDF.format(_12HourDt)
    }


    data class TapCurrencyResponse(
        val live_mode: String,
        val api_version: String,
        val feature_version: String,
        val created: Int,
        val id: String,
        val from: Amount,
        val to: List<Amount>,
        val by: String
    )

    data class Amount(
        val currency: String,
        val value: String
    )

    data class AmountDcc(
        val currency: String,
        val dcc_rate: String
    )

    data class TapCurrencyRequest(
        val from: Amount,
        val to: List<AmountDcc>,
        val by: String = "PROVIDER"
    )

//    enum class TAP_SUPPORTED_CURRENCIES(val currrency: String) {
//        UAE_Dirham("AED"),
//        Bahraini_Dinar("BHD"),
//        Egyptian_Pound("EGP"),
//        Euro("EUR"),
//        UK_Pound_Sterling("GBP"),
//        Kuwaiti_Dinar("KWD"),
//        Omani_Riyal("OMR"),
//        Qatari_Riyal("QAR"),
//        Saudi_Riyal("SAR"),
//        US_Dollar("USD")
//    }

    fun addMonth(date: Date?, i: Int): Date? {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.MONTH, i)
        return cal.time
    }

    @JvmStatic
    fun dateFormate(time: String): String? {
        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = "dd-MMM-yyyy"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = ""
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }

    @JvmStatic
    fun dateFormateNew(time: String): String? {
        val inputPattern = "yyyy-MM-dd"
        val outputPattern = "dd-MMM-yyyy"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = ""
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }

    @JvmStatic
    fun timeFormate(time: String): String? {

        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = "HH:mm"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = ""
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }

    fun fractionalPartValueFormate(distanceInMiles: String): CharSequence? {
        return String.format("%.2f", distanceInMiles.toDouble())
    }


    fun convertUTC2LocalDateTime(datetime: String): String
    {
        var convertedDateTime: String = ""
        val utcFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        utcFormatter.timeZone = TimeZone.getTimeZone("UTC")
        var gpsUTCDate: Date? = null //from  ww  w.j  a va 2 s  . c  o  m

        try {
            gpsUTCDate = utcFormatter.parse(datetime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val localFormatter = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        localFormatter.timeZone = TimeZone.getDefault()
        assert(gpsUTCDate != null)
        convertedDateTime = localFormatter.format(gpsUTCDate!!.time)
        return convertedDateTime
    }

    fun getDateFormate(date1: String?): String? {
        val inFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date: Date? = null
        try {
            date = inFormat.parse(date1)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val outFormat = SimpleDateFormat("dd MMM yyyy")
        return outFormat.format(date)
    }

    @Throws(ParseException::class)
    fun formatToYesterdayOrToday(date: String?): String? {
        val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date)
        val calendar = Calendar.getInstance()
        calendar.time = dateTime
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DATE, -1)
        val timeFormatter: SimpleDateFormat = SimpleDateFormat("hh:mma")
        return if (calendar[Calendar.YEAR] === today[Calendar.YEAR] && calendar[Calendar.DAY_OF_YEAR] === today[Calendar.DAY_OF_YEAR]) {
// "Today " + timeFormatter.format(dateTime)
            "Today "
        } else if (calendar[Calendar.YEAR] === yesterday[Calendar.YEAR] && calendar[Calendar.DAY_OF_YEAR] === yesterday[Calendar.DAY_OF_YEAR]) {
// "Yesterday " + timeFormatter.format(dateTime)
            "Yesterday "
        } else {
            getDateFormate(date)
        }
    }

    fun languageIsRTL(context: Context?, view: View?) {
        val preferenceHelper = LanguagePreferences(context)
        if (preferenceHelper.isRTL) {
            if (view != null) {
                ViewCompat.setLayoutDirection(view, ViewCompat.LAYOUT_DIRECTION_RTL)
            }
        }
    }

    fun languageSelection(context: Context, lang: String?) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun getErrorMessage(responseBody: ResponseBody): String? {
//        var string = ""
//        try {
//            val jsonObject = JSONObject(responseBody.toString())
//            string = jsonObject.getString("message")
//
//        } catch (e: Exception)
//        {
//
//            string = e.message.toString()
//        }
//        return string
        var string = ""
        try {
            val jsonObject = JSONObject(responseBody.string())
            if (jsonObject.getString("status") == "ERROR") {
                string = jsonObject.getString("messages")
            } else {
                string = jsonObject.getString("message")
            }
        } catch (e: Exception) {
            string = e.message.toString()
        }
        return string

    }


}