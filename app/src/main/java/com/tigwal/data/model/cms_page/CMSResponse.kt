package com.tigwal.data.model.cms_page

import com.google.gson.annotations.SerializedName

data class CMSResponse(


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
    @field:SerializedName("updated_at")
    val updatedAt: String? = "",

    @field:SerializedName("created_at")
    val created_at: String? = "",

    @field:SerializedName("description")
    val description: String? = "",

    @field:SerializedName("slug")
    val slug: String? = "",

    @field:SerializedName("title")
    val title: String? = "",

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("ar_title")
    val ar_title: String? = "",

    @field:SerializedName("ar_description")
    val ar_description: String? = "",
)
