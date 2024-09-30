package com.tigwal.data.model.tap_payment

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class SubscriptionPlanResponse {
    @SerializedName("code")
    @Expose
    var code: String? = ""

    @SerializedName("status")
    @Expose
    var status: String? = ""

    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null

    @SerializedName("message")
    @Expose
    var message: String? = ""


    class Datum {
        @SerializedName("id")
        @Expose
        var id: String? = ""

        @SerializedName("plan_name")
        @Expose
        var planName: String? = ""

        @SerializedName("product_limit")
        @Expose
        var productLimit: String? = ""

        @SerializedName("purchase_order_limit")
        @Expose
        var purchaseOrderLimit: String? = ""

        @SerializedName("fee")
        @Expose
        var fee: String? = ""

        @SerializedName("status")
        @Expose
        var status: String? = ""

        @SerializedName("created_date")
        @Expose
        var createdDate: String? = ""


        @SerializedName("conversion_amount")
        @Expose
        var conversion_amount: String? = ""



    }
}