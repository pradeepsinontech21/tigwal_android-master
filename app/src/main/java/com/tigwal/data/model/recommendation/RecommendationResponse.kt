package com.tigwal.data.model.recommendation

import com.google.gson.annotations.SerializedName

data class RecommendationResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<DataItem?>? = null,

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

	@field:SerializedName("images")
	val images: List<ImagesItem?>? = null,

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

	@field:SerializedName("slots")
	val slots: List<SlotsItem?>? = null,

	@field:SerializedName("loc_long")
	val locLong: Any? = null,

	@field:SerializedName("vendor_id")
	val vendorId: Int? = null,

	@field:SerializedName("ride_time")
	val rideTime: Int? = null,

	@field:SerializedName("status")
	val status: String? = ""
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
