package com.tigwal.data.model.chat

import com.google.gson.annotations.SerializedName

data class SendChatResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<Any?>? = null,

	@field:SerializedName("message")
	val message: String? = "",

	@field:SerializedName("status")
	val status: Boolean? = null
)
