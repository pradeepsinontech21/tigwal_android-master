package com.tigwal.data.model.tap_payment
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class CheckSubscriptionResponse {
    @SerializedName("code")
    @Expose
    var code: String? = ""

    @SerializedName("status")
    @Expose
    var status: String? = ""

    @SerializedName("message")
    @Expose
    var message: String? = ""

    @SerializedName("is_expire")
    @Expose
    var isExpire: Boolean? = null


}