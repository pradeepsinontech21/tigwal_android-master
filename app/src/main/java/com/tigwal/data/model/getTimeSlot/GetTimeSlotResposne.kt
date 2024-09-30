package com.tigwal.data.model.getTimeSlot

import com.google.gson.annotations.SerializedName

data class GetTimeSlotResposne(

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

	@field:SerializedName("start_time")
	val startTime: String? = "",

	@field:SerializedName("updated_at")
	val updatedAt: String? = "",

	@field:SerializedName("qty")
	val qty: Any? = null,

	@field:SerializedName("total_qty")
	val totalQty: Int? = null,

	@field:SerializedName("end_time")
	val endTime: String? = "",

	@field:SerializedName("created_at")
	val createdAt: String? = "",

	@field:SerializedName("available_qty")
	val availableQty: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("boooking")
	val boooking: Int? = null,

	@field:SerializedName("loc_id")
	val locId: Int? = null,

	@field:SerializedName("day_id")
	val dayId: Any? = null
)
