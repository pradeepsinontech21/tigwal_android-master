package com.tigwal.data.model.otp

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VerifyOtpResponse {
    @SerializedName("code")
    @Expose
    var code: String? = ""

    @SerializedName("status")
    @Expose
    var status: String? = ""

    @SerializedName("message")
    @Expose
    var message: String? = ""
}