package com.tigwal.data.model.login

import com.google.gson.annotations.SerializedName

class LoginResponse {

    @SerializedName("code")
    val code: Int? = null

    @SerializedName("data")
    val data: Data? = null

    @SerializedName("message")
    val message: String? = ""

    @SerializedName("status")
    val status: Boolean? = null

    class Data {

        @SerializedName("image")
        val image: String? = ""

        @SerializedName("country_short_name")
        val country_short_name: String? = ""

        @SerializedName("enable_notification")
        val enable_notification: String? = ""

        @SerializedName("imageurl")
        val imageurl: String? = ""


        @SerializedName("country_code")
        val countryCode: String? = ""

        @SerializedName("user_type")
        val userType: String? = ""

        @SerializedName("user_id")
        val userId: String? = ""

        @SerializedName("apple_token")
        val appleToken: String? = ""

        @SerializedName("login_type")
        val loginType: String? = ""

        @SerializedName("name")
        val name: String? = ""

        @SerializedName("mobile_no")
        val mobileNo: String? = ""

        @SerializedName("google_token")
        val googleToken: String? = ""

        @SerializedName("facebook_token")
        val facebookToken: String? = ""

        @SerializedName("email")
        val email: String? = ""

        @SerializedName("token")
        val token: String? = ""


        @SerializedName("address")
        val address: String? = ""

        @SerializedName("longs")
        val longs: String? = ""

        @SerializedName("lats")
        val lats: String? = ""

        @SerializedName("country")
        val country: String? = ""

        @SerializedName("birth_date")
        val birth_date: String? = ""

        @SerializedName("gender")
        val gender: String? = ""


    }
}


