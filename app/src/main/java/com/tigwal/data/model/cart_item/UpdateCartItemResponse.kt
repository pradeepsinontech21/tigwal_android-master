package com.tigwal.data.model.cart_item

import com.google.gson.annotations.SerializedName

data class UpdateCartItemResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = "",

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class Data(

	@field:SerializedName("updated_at")
	val updatedAt: String? = "",

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("slot_id")
	val slotId: Int? = null,

	@field:SerializedName("price")
	val price: String? = "",

	@field:SerializedName("qty")
	val qty: String? = "",

	@field:SerializedName("created_at")
	val createdAt: String? = "",

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("book_date")
	val bookDate: String? = "",

	@field:SerializedName("location_id")
	val locationId: Int? = null
)
