package com.tigwal.data.model.notification_list

import com.google.gson.annotations.SerializedName

data class NotificationListResponse(

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


    @field:SerializedName("created_at")
    val createdAt: String? = "",

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("is_read")
    val isRead: String? = "",

    @field:SerializedName("message")
    val message: String? = "",

    @field:SerializedName("notification_type")
    val notificationType: String? = "",

    @field:SerializedName("notification_created")
    val notification_created: String? = "",

    @field:SerializedName("status")
    val status: String? = "",


    @field:SerializedName("title")
    val title: String? = "",

    @field:SerializedName("updated_at")
    val updatedAt: String? = "",


    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("user_type")
    val userType: String? = "",

    )
