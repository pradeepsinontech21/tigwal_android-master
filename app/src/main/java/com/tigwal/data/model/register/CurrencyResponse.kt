package com.tigwal.data.model.register

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CurrencyResponse {
    @SerializedName("code")
    @Expose
    var code: String? = ""

    @SerializedName("status")
    @Expose
    var status: String? = ""

    @SerializedName("data")
    @Expose
    var data: ArrayList<Datum>? = null

    @SerializedName("message")
    @Expose
    var message: String? = ""

    class Datum {
        @SerializedName("id")
        @Expose
        var id: String? = ""

        @SerializedName("country_name")
        @Expose
        var countryName: String? = ""

        @SerializedName("currency_name")
        @Expose
        var currencyName: String? = ""

        @SerializedName("currency_short_name")
        @Expose
        var currencyShortName: String? = ""

        @SerializedName("currency_symbol")
        @Expose
        var currencySymbol: String? = ""

        @SerializedName("currency_status")
        @Expose
        var currencyStatus: String? = ""

        @SerializedName("createdAt")
        @Expose
        var createdAt: String? = ""

        @SerializedName("updatedAt")
        @Expose
        var updatedAt: String? = ""

        @SerializedName("CURRENCY_NAME_1")
        @Expose
        var CURRENCY_NAME_1: String? = ""

        @SerializedName("currencyfull")
        @Expose
        var currencyfull: String? = ""
    }
}