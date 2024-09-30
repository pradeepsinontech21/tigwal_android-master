package com.tigwal.data.model.cancel_order

import com.google.gson.annotations.SerializedName

data class CancelOrderResponse(

	@field:SerializedName("code")
	val code: Int? = null,
	@field:SerializedName("data")
	val data: Data? = null,

//	@field:SerializedName("data")
//	val data: List<Any?>? = null,

	@field:SerializedName("message")
	val message: String? = "",

	@field:SerializedName("status")
	val status: Boolean? = null


)

data class Data(

	@field:SerializedName("title")
	val title: String? = "",

	@field:SerializedName("price")
	val price: String? = ""
)