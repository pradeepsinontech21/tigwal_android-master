package com.tigwal.data.model.otp

import com.google.gson.annotations.SerializedName

data class SendOtpResponse(

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

	@field:SerializedName("otp")
	val otp: Int? = null
)
