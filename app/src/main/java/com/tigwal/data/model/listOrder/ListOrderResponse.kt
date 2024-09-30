package com.tigwal.data.model.listOrder

import com.google.gson.annotations.SerializedName

data class ListOrderResponse(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("message")
    val message: String? = "",

    @field:SerializedName("status")
    val status: Boolean? = null
)

data class Locations(

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
    val locLat: Any? = null,

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

    @field:SerializedName("location_created")
    val locationCreated: String? = "",

    @field:SerializedName("is_admin_approve")
    val isAdminApprove: String? = "",

    @field:SerializedName("total_qty")
    val totalQty: Int? = null,

    @field:SerializedName("end_time")
    val endTime: Any? = null,

    @field:SerializedName("average_rating")
    val averageRating: Any? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("start_time")
    val startTime: Any? = null,

    @field:SerializedName("approved_by")
    val approvedBy: Int? = null,

    @field:SerializedName("loc_long")
    val locLong: Any? = null,

    @field:SerializedName("vendor_id")
    val vendorId: Int? = null,

    @field:SerializedName("ride_time")
    val rideTime: Int? = null,

    @field:SerializedName("status")
    val status: String? = ""
)

data class Data(

    @field:SerializedName("orders")
    val orders: ArrayList<OrdersItem?>? = null,

    @field:SerializedName("total_orders")
    val totalOrders: Int? = null
)

data class OrderitemsItem(

    @field:SerializedName("notes")
    val notes: Any? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = "",

    @field:SerializedName("book_date")
    val bookDate: String? = "",

    @field:SerializedName("location_id")
    val locationId: Int? = null,

    @field:SerializedName("slots")
    val slots: Slots? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = "",

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("slot_id")
    val slotId: Int? = null,

    @field:SerializedName("price")
    val price: Int? = null,

    @field:SerializedName("qty")
    val qty: Int? = null,

    @field:SerializedName("locations")
    val locations: Locations? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("order_id")
    val orderId: String? = ""
)

data class Slots(

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

data class User(

    @field:SerializedName("end_date")
    val endDate: Any? = null,

    @field:SerializedName("google2fa_secret")
    val google2faSecret: Any? = null,

    @field:SerializedName("login_type")
    val loginType: String? = "",

    @field:SerializedName("google_verify")
    val googleVerify: Any? = null,

    @field:SerializedName("lats")
    val lats: Double? = null,

    @field:SerializedName("mobile_no")
    val mobileNo: String? = "",

    @field:SerializedName("created_at")
    val createdAt: String? = "",

    @field:SerializedName("facebook_token")
    val facebookToken: Any? = null,

    @field:SerializedName("longs")
    val longs: Double? = null,

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
    val startDate: Any? = null,

    @field:SerializedName("enable_notification")
    val enableNotification: String? = "",

    @field:SerializedName("image")
    val image: String? = "",

    @field:SerializedName("address")
    val address: String? = "",

    @field:SerializedName("is_verify")
    val isVerify: String? = "",

    @field:SerializedName("last_name")
    val lastName: Any? = null,

    @field:SerializedName("created_by")
    val createdBy: Any? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("country_code")
    val countryCode: String? = "",

    @field:SerializedName("country_short_name")
    val countryShortName: String? = "",

    @field:SerializedName("device_token")
    val deviceToken: Any? = null,

    @field:SerializedName("updated_by")
    val updatedBy: Any? = null,

    @field:SerializedName("google_token")
    val googleToken: Any? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class OrdersItem(

    @field:SerializedName("charge_refund_id")
    val chargeRefundId: Any? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = "",

    @field:SerializedName("type")
    val type: String? = "",

    @field:SerializedName("offer_id")
    val offerId: Any? = null,

    @field:SerializedName("orderitems")
    val orderitems: List<OrderitemsItem?>? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = "",

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("total_amount")
    val totalAmount: String? = "",

    @field:SerializedName("payment_id")
    val paymentId: String? = "",

    @field:SerializedName("charge_id")
    val chargeId: String? = "",

    @field:SerializedName("refund_amount")
    val refundAmount: Any? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("order_id")
    val orderId: String? = "",

    @field:SerializedName("user")
    val user: User? = null,

    @field:SerializedName("status")
    val status: String? = "",

    @field:SerializedName("order_created")
    val order_created: String? = "",
    @field:SerializedName("order_status")
    val order_status: String? = ""

)
