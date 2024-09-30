package com.tigwal.data.model.notification_enable

import com.google.gson.annotations.SerializedName

data class NotificationUpdateResponse(

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
	val any: Any? = null
)
