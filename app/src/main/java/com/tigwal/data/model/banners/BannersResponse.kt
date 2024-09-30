package com.tigwal.data.model.banners

import com.google.gson.annotations.SerializedName

data class BannersResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: ArrayList<BannerDataItem?>? = null,

	@field:SerializedName("message")
	val message: String? = "",

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class BannerDataItem(

	@field:SerializedName("offer")
	val offer: String? = "",

	@field:SerializedName("approved_by")
	val approvedBy: Any? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = "",

	@field:SerializedName("payment_id")
	val paymentId: String? = "",

	@field:SerializedName("vendor_id")
	val vendorId: Int? = null,

	@field:SerializedName("payment_status")
	val paymentStatus: String? = "",

	@field:SerializedName("is_approved")
	val isApproved: String? = "",

	@field:SerializedName("created_at")
	val createdAt: String? = "",

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("banner_image")
	val bannerImage: String? = "",

	@field:SerializedName("approved_date")
	val approvedDate: Any? = null,

	@field:SerializedName("offer_id")
	val offerId: Int? = null
)
