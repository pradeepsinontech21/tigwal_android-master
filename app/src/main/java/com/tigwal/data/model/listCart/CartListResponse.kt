package com.tigwal.data.model.listCart

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CartListResponse(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: CartDataItem? = null,

//    @field:SerializedName("data")
//    val data: List<CartListItem?>? = null,

    @field:SerializedName("message")
    val message: String? = "",

    @field:SerializedName("status")
    val status: Boolean? = null
)

data class Location(

    @field:SerializedName("date")
    val date: Any? = null,

    @field:SerializedName("location_close")
    val location_close: String? = "",

    @field:SerializedName("location_created")
    val location_created: String? = "",


    @field:SerializedName("added_by")
    val added_by: String? = "",

    @field:SerializedName("modifed_by")
    val modifed_by: String? = "",

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

    @field:SerializedName("users")
    val users: Users? = null,

    @field:SerializedName("start_time")
    val startTime: Any? = null,

    @field:SerializedName("approved_by")
    val approvedBy: Int? = null,

    @field:SerializedName("loc_long")
    val locLong: String? = "",

    @field:SerializedName("vendor_id")
    val vendorId: Int? = null,

    @field:SerializedName("ride_time")
    val rideTime: Int? = null,

    @field:SerializedName("status")
    val status: String? = "",

    @field:SerializedName("images")
    val images: List<CartImagesItem?>? = null,

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

data class Users(

    @field:SerializedName("country_code")
    val countryCode: String? = "",

    @field:SerializedName("country_short_name")
    val countryShortName: Any? = null,

    @field:SerializedName("mobile_no")
    val mobileNo: String? = "",

    @field:SerializedName("last_name")
    val lastName: String? = "",

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("first_name")
    val firstName: String? = "",

    @field:SerializedName("destination_id")
    val destination_id: String? = "",

    @field:SerializedName("bussiness_id")
    val bussiness_id: String? = "",

    @field:SerializedName("email")
    val email: String? = ""
)


data class CartImagesItem(
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("loc_id")
    val loc_id: Int? = null,

    @field:SerializedName("image")
    val image: String? = "",

    @field:SerializedName("is_approved")
    val is_approved: String? = "",

    @field:SerializedName("approved_by")
    val approved_by: String? = "",

    @field:SerializedName("created_at")
    val created_at: String? = "",

    @field:SerializedName("updated_at")
    val updated_at: String? = "",
)


data class CartDataItem(
    @field:SerializedName("cart")
    val cartdata: List<CartListItem?>? = null,

    @field:SerializedName("admin_commision")
    val admin_commision: String? = "",
)

data class CartListItem(

    var cartItemNotes: String = "",
    var totalCalculatePrice: String = "0",
    var totalDiscount: String = "0",
    var totalPrice: String = "0",
    var totalServiceTax: String = "0",
    var admin_commision: String = "0",
    var admin_commision_total: String = "0",
    var destination_id: String = "0",
    var admin_commision_total_amt: String = "0",
    var destination_same_qty: String = "0",
    var vendor_id: String = "0",


    @field:SerializedName("slots")
    val slots: Slots? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = "",



    @field:SerializedName("service_tax")
    val service_tax: String? = "",


    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("slot_id")
    val slotId: Int? = null,

    @field:SerializedName("price")
    val price: String? = "",

    @field:SerializedName("qty")
    var qty: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = "",

    @field:SerializedName("location")
    val location: Location? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("book_date")
    val bookDate: String? = "",

    @field:SerializedName("notes")
    val notes: String? = "",

    @field:SerializedName("location_id")
    val locationId: Int? = null

) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        null,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        null,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cartItemNotes)
        parcel.writeString(totalCalculatePrice)
        parcel.writeString(totalDiscount)
        parcel.writeString(totalPrice)
        parcel.writeString(totalServiceTax)
        parcel.writeString(admin_commision)
        parcel.writeString(admin_commision_total)
        parcel.writeString(destination_id)
        parcel.writeString(admin_commision_total_amt)
        parcel.writeString(destination_same_qty)
        parcel.writeString(vendor_id)
        parcel.writeString(updatedAt)
        parcel.writeString(service_tax)
        parcel.writeValue(userId)
        parcel.writeValue(slotId)
        parcel.writeString(price)
        parcel.writeValue(qty)
        parcel.writeString(createdAt)
        parcel.writeValue(id)
        parcel.writeString(bookDate)
        parcel.writeString(notes)
        parcel.writeValue(locationId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartListItem> {
        override fun createFromParcel(parcel: Parcel): CartListItem {
            return CartListItem(parcel)
        }

        override fun newArray(size: Int): Array<CartListItem?> {
            return arrayOfNulls(size)
        }
    }
}
