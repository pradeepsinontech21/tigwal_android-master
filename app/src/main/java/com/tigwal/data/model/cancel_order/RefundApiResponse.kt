package com.tigwal.data.model.cancel_order

import com.google.gson.annotations.SerializedName

data class RefundApiResponse(

	@field:SerializedName("reason")
	val reason: String? = "",

	@field:SerializedName("amount")
	val amount: Double? = null,

	@field:SerializedName("product")
	val product: String? = "",

	@field:SerializedName("method")
	val method: String? = "",

	@field:SerializedName("created")
	val created: String? = "",

	@field:SerializedName("description")
	val description: String? = "",

	@field:SerializedName("merchant_id")
	val merchantId: String? = "",

	@field:SerializedName("api_version")
	val apiVersion: String? = "",

	@field:SerializedName("reference")
	val reference: Reference? = null,

	@field:SerializedName("live_mode")
	val liveMode: Boolean? = null,

	@field:SerializedName("charge_id")
	val chargeId: String? = "",

	@field:SerializedName("response")
	val response: Response? = null,

	@field:SerializedName("currency")
	val currency: String? = "",

	@field:SerializedName("id")
	val id: String? = "",

	@field:SerializedName("object")
	val object11: String? = "",

	@field:SerializedName("status")
	val status: String? = ""
)

data class Response(

	@field:SerializedName("code")
	val code: String? = "",

	@field:SerializedName("message")
	val message: String? = ""
)

data class Reference(

	@field:SerializedName("id")
	val id: String? = ""
)
