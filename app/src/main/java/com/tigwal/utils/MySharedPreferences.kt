package com.tigwal.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tigwal.Application.Companion.application
import com.tigwal.app.rest.RestConstant

@SuppressLint("CommitPrefEdits")
class MySharedPreferences private constructor(context: Context?, gson: Gson) {
    private val SP_NAME = "p6_pref"
    private val sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor
    var gson: Gson


    var isLogin: Boolean
        get() = sharedPreferences.getBoolean(RestConstant.IS_LOGIN, false)
        set(isLogin) {
            editor.putBoolean(RestConstant.IS_LOGIN, isLogin).apply()
        }


    var authToken: String?
        get() = sharedPreferences.getString(RestConstant.AUTH_TOKEN, "")
        set(authToken1) {
            editor.putString(RestConstant.AUTH_TOKEN, authToken1).apply()
        }

    var deviceId: String?
        get() = sharedPreferences.getString(RestConstant.DEVICE_Id, "")
        set(deviceid) {
            editor.putString(RestConstant.DEVICE_Id, deviceid).apply()
        }


    var deviceToken: String?
        get() = sharedPreferences.getString(RestConstant.deviceToken, "")
        set(deviceToken) {
            editor.putString(RestConstant.deviceToken, deviceToken).apply()
        }


    var userName: String?
        get() = sharedPreferences.getString(RestConstant.USER_NAME, "")
        set(userName) {
            editor.putString(RestConstant.USER_NAME, userName).apply()
        }

    var email: String?
        get() = sharedPreferences.getString(RestConstant.USER_EMAIL, "")
        set(email) {
            editor.putString(RestConstant.USER_EMAIL, email).apply()
        }

    var countrycode: String?
        get() = sharedPreferences.getString(RestConstant.USER_COUNTRY_CODE, "+968")
        set(countrycode) {
            editor.putString(RestConstant.USER_COUNTRY_CODE, countrycode).apply()
        }
    var userType: String?
        get() = sharedPreferences.getString(RestConstant.userType, "")
        set(userType) {
            editor.putString(RestConstant.userType, userType).apply()
        }
    var loginType: String?
        get() = sharedPreferences.getString(RestConstant.login_type, "")
        set(id) {
            editor.putString(RestConstant.login_type, id).apply()
        }
    var countryshortname: String?
        get() = sharedPreferences.getString(RestConstant.countryshortname, "IN")
        set(countryName) {
            editor.putString(RestConstant.countryshortname, countryName).apply()
        }

    var userImage: String?
        get() = sharedPreferences.getString(RestConstant.USER_IMAGE, "")
        set(userImage) {
            editor.putString(RestConstant.USER_IMAGE, userImage).apply()
        }

    var isNotification: String?
        get() = sharedPreferences.getString(RestConstant.IS_NOTIFICATION, "")
        set(isNotification1) {
            editor.putString(RestConstant.IS_NOTIFICATION, isNotification1).apply()
        }

    var phoneNumber: String?
        get() = sharedPreferences.getString(RestConstant.USER_CONTACT_NUMBER, "")
        set(phoneNumber) {
            editor.putString(RestConstant.USER_CONTACT_NUMBER, phoneNumber).apply()
        }

    var userId: String?
        get() = sharedPreferences.getString(RestConstant.USER_ID, "")
        set(id) {
            editor.putString(RestConstant.USER_ID, id).apply()
        }

    var language: String?
        get() = sharedPreferences.getString(RestConstant.LANGUAGE, "en")
        set(language1) {
            editor.putString(RestConstant.LANGUAGE, language1).apply()
        }


    var deviceUDID: String?
        get() = sharedPreferences.getString(RestConstant.udiid, "")
        set(udiid1) {
            editor.putString(RestConstant.udiid, udiid1).apply()
        }

    var locationID: String?
        get() = sharedPreferences.getString(RestConstant.locationID, "")
        set(locationID) {
            editor.putString(RestConstant.locationID, locationID).apply()
        }

    fun clearPreferences() {
        editor.clear().apply()
    }

    fun <T> putObject(key: String?, value: T) {
        val editor = sharedPreferences.edit()
        editor.putString(key, gson.toJson(value))
        editor.apply()
    }

    fun <T> getObject(key: String?, clazz: Class<T>?): T {
        return gson.fromJson(getString(key, null), clazz)
    }


    fun <T> putList(key: String?, list: List<T>?) {
        val editor = sharedPreferences.edit()
        editor.putString(key, gson.toJson(list))
        editor.apply()
    }

    fun <T> getList(key: String?, clazz: Class<T>?): List<T> {
        val typeOfT = TypeToken.getParameterized(MutableList::class.java, clazz).type
        return gson.fromJson(getString(key, null), typeOfT)
    }

    fun <T> putArray(key: String?, arrays: Array<T>?) {
        val editor = sharedPreferences.edit()
        editor.putString(key, gson.toJson(arrays))
        editor.apply()
    }

    fun <T> getArray(key: String?, clazz: Class<Array<T>?>?): Array<T> {
        return gson.fromJson(getString(key, null), clazz)!!
    }

    fun removeKey(key: String?) {
        val editor = sharedPreferences.edit()
        if (editor != null) {
            editor.remove(key)
            editor.apply()
        }
    }

    fun getString(key: String?, defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    companion object {
        private var mySharedPreferences: MySharedPreferences? = null

        @JvmName("getMySharedPreferences1")
        fun getMySharedPreferences(): MySharedPreferences? {
            if (mySharedPreferences == null) {
                mySharedPreferences = MySharedPreferences(application, Gson())
            }
            return mySharedPreferences
        }
    }

    init {
        sharedPreferences = context!!.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        this.gson = gson
    }
}