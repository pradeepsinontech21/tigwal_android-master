package com.tigwal.data.model.getcategory

import com.google.gson.annotations.SerializedName

data class CategoryResponse(

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

	@field:SerializedName("category_image")
	val categoryImage: String? = "",

	@field:SerializedName("category_name")
	val categoryName: String? = "",

	@field:SerializedName("category_id")
	val categoryId: String? = "",

	@field:SerializedName("subcategory")
	val subcategory: String? = "",

	@field:SerializedName("category_name_ar")
	val category_name_ar: String? = "",



)
