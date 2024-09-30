package com.tigwal.data.model.forgotpassword

import com.google.gson.annotations.SerializedName

data class ForgotPasswordResponse(

	@SerializedName("code")
	val code: Int? = null,

	@SerializedName("data")
	val data: Data? = null,

	@SerializedName("message")
	val message: String? = "",

	@SerializedName("status")
	val status: Boolean? = null
)

data class Data(

	@SerializedName("otp")
	val otp: Int? = null
)
