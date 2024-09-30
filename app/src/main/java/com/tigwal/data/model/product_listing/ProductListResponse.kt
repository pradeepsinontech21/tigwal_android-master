package com.tigwal.data.model.product_listing

import com.google.gson.annotations.SerializedName

data class ProductListResponse(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("message")
    val message: String? = "",

    @field:SerializedName("status")
    val status: Boolean? = null
)

data class DataItem(

    @field:SerializedName("date")
    val date: Any? = null,

    @field:SerializedName("loc_address")
    val locAddress: String? = "",

    @field:SerializedName("description")
    val description: String? = "",

    @field:SerializedName("discount")
    val discount: String? = "",

    @field:SerializedName("created_at")
    val createdAt: String? = "",

    @field:SerializedName("loc_lat")
    val locLat: String? = "",

    @field:SerializedName("is_deleted")
    val isDeleted: String? = "",

    @field:SerializedName("category_id")
    val categoryId: Int? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = "",

    @field:SerializedName("price")
    val price: String? = "",

    @field:SerializedName("loc_name")
    val locName: String? = "",

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("remain_qty")
    val remainQty: Any? = null,

    @field:SerializedName("images")
    val images: List<ImagesItem?>? = null,

    @field:SerializedName("is_admin_approve")
    val isAdminApprove: String? = "",

    @field:SerializedName("total_qty")
    val totalQty: Int? = null,

    @field:SerializedName("end_time")
    val endTime: Any? = null,

    @field:SerializedName("average_rating")
    var averageRating: Double? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("users")
    val users: Users? = null,

    @field:SerializedName("start_time")
    val startTime: Any? = null,

    @field:SerializedName("approved_by")
    val approvedBy: Int? = null,

    @field:SerializedName("slots")
    val slots: List<SlotsItem?>? = null,

    @field:SerializedName("loc_long")
    val locLong: String? = "",

    @field:SerializedName("vendor_id")
    val vendorId: Int? = null,

    @field:SerializedName("ride_time")
    val rideTime: Int? = null,

    @field:SerializedName("status")
    val status: String? = ""
)

data class ImagesItem(

    @field:SerializedName("image")
    val image: String? = "",

    @field:SerializedName("approved_by")
    val approvedBy: Any? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = "",

    @field:SerializedName("is_approved")
    val isApproved: String? = "",

    @field:SerializedName("created_at")
    val createdAt: String? = "",

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("loc_id")
    val locId: Int? = null
)

data class SlotsItem(

    @field:SerializedName("start_time")
    val startTime: String? = "",

    @field:SerializedName("updated_at")
    val updatedAt: String? = "",

    @field:SerializedName("qty")
    val qty: Any? = null,

    @field:SerializedName("end_time")
    val endTime: String? = "",

    @field:SerializedName("created_at")
    val createdAt: String? = "",

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("loc_id")
    val locId: Int? = null,

    @field:SerializedName("day_id")
    val dayId: Any? = null
)

data class Users(

    @field:SerializedName("end_date")
    val endDate: String? = "",

    @field:SerializedName("google2fa_secret")
    val google2faSecret: String? = "",

    @field:SerializedName("login_type")
    val loginType: String? = "",

    @field:SerializedName("google_verify")
    val googleVerify: String? = "",

    @field:SerializedName("lats")
    val lats: Any? = null,

    @field:SerializedName("mobile_no")
    val mobileNo: String? = "",

    @field:SerializedName("created_at")
    val createdAt: String? = "",

    @field:SerializedName("facebook_token")
    val facebookToken: Any? = null,

    @field:SerializedName("longs")
    val longs: Any? = null,

    @field:SerializedName("user_type")
    val userType: String? = "",

    @field:SerializedName("updated_at")
    val updatedAt: String? = "",

    @field:SerializedName("apple_token")
    val appleToken: Any? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("remember_token")
    val rememberToken: Any? = null,

    @field:SerializedName("first_name")
    val firstName: String? = "",

    @field:SerializedName("email")
    val email: String? = "",

    @field:SerializedName("start_date")
    val startDate: String? = "",

    @field:SerializedName("enable_notification")
    val enableNotification: String? = "",

    @field:SerializedName("image")
    val image: Any? = null,

    @field:SerializedName("address")
    val address: Any? = null,

    @field:SerializedName("is_verify")
    val isVerify: String? = "",

    @field:SerializedName("last_name")
    val lastName: String? = "",

    @field:SerializedName("created_by")
    val createdBy: Any? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("country_code")
    val countryCode: String? = "",

    @field:SerializedName("country_short_name")
    val countryShortName: Any? = null,

    @field:SerializedName("device_token")
    val deviceToken: Any? = null,

    @field:SerializedName("updated_by")
    val updatedBy: Any? = null,

    @field:SerializedName("google_token")
    val googleToken: Any? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class Data(

    @field:SerializedName("first_page_url")
    val firstPageUrl: String? = "",

    @field:SerializedName("path")
    val path: String? = "",

    @field:SerializedName("per_page")
    val perPage: String? = "",

    @field:SerializedName("total")
    val total: Int? = null,

    @field:SerializedName("data")
    val data: List<DataItem?>? = null,

    @field:SerializedName("last_page")
    val lastPage: Int? = null,

    @field:SerializedName("last_page_url")
    val lastPageUrl: String? = "",

    @field:SerializedName("next_page_url")
    val nextPageUrl: Any? = null,

    @field:SerializedName("from")
    val from: Int? = null,

    @field:SerializedName("to")
    val to: Int? = null,

    @field:SerializedName("prev_page_url")
    val prevPageUrl: Any? = null,

    @field:SerializedName("current_page")
    val currentPage: Int? = null
)
