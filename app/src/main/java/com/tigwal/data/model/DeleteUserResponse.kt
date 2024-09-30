package com.tigwal.data.model

import com.google.gson.annotations.SerializedName
import com.tigwal.data.model.splash.Data

data class DeleteUserResponse(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: Any? = null,

    @field:SerializedName("message")
    val message: String? = "",

    @field:SerializedName("status")
    val status: Boolean? = null
)