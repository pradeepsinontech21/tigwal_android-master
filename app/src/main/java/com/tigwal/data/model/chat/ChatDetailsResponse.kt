package com.tigwal.data.model.chat

import com.google.gson.annotations.SerializedName

data class ChatDetailsResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = "",

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class ChatItem(

	@field:SerializedName("is_read")
	val isRead: String? = "",

	@field:SerializedName("other_media")
	val otherMedia: Any? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = "",

	@field:SerializedName("receiver_id")
	val receiverId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = "",

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: String? = "",

	@field:SerializedName("message")
	val message: String? = "",

	@field:SerializedName("sender_id")
	val senderId: Int? = null,

	@field:SerializedName("chat_created")
	val chatCreated: String? = ""
)

data class Data(

	@field:SerializedName("chat")
	val chat: ArrayList<ChatItem?>? = null,

	@field:SerializedName("userprofile")
	val userprofile: Userprofile? = null
)

data class Userprofile(

	@field:SerializedName("enable_notification")
	val enableNotification: String? = "",

	@field:SerializedName("image")
	val image: String? = "",

	@field:SerializedName("address")
	val address: String? = "",

	@field:SerializedName("login_type")
	val loginType: String? = "",

	@field:SerializedName("lats")
	val lats: String? = "",

	@field:SerializedName("mobile_no")
	val mobileNo: String? = "",

	@field:SerializedName("facebook_token")
	val facebookToken: String? = "",

	@field:SerializedName("country_code")
	val countryCode: String? = "",

	@field:SerializedName("longs")
	val longs: String? = "",

	@field:SerializedName("country_short_name")
	val countryShortName: String? = "",

	@field:SerializedName("user_type")
	val userType: String? = "",

	@field:SerializedName("user_id")
	val userId: String? = "",

	@field:SerializedName("apple_token")
	val appleToken: String? = "",

	@field:SerializedName("imageurl")
	val imageurl: String? = "",

	@field:SerializedName("name")
	val name: String? = "",

	@field:SerializedName("google_token")
	val googleToken: String? = "",

	@field:SerializedName("email")
	val email: String? = ""
)
