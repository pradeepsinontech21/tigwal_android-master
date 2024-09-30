package com.tigwal.data.model.tap_payment

import com.google.gson.annotations.SerializedName

data class TapPaymentResponse(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("message")
    val message: String? = "",

    @field:SerializedName("status")
    val status: Boolean? = null
)
data class CartSubmitData(

    @field:SerializedName("vendor_id")
    var vendor_id :  String="",

    @field:SerializedName("destination_id")
    var destination_id: String="",

    @field:SerializedName("product_amount")
    var product_amount: String="",

    @field:SerializedName("service_charge")
    var service_charge: String="",

    @field:SerializedName("admin_commission")
    var admin_commission: String="",

    @field:SerializedName("final_amount")
    var final_amount: String="",

    @field:SerializedName("location_id")
    var location_id: String="",

    @field:SerializedName("qty")
    var qty: String="",
)
data class Gateway(

    @field:SerializedName("response")
    val response: Response? = null
)

data class Source(

    @field:SerializedName("payment_type")
    val paymentType: String? = "",

    @field:SerializedName("channel")
    val channel: String? = "",

    @field:SerializedName("id")
    val id: String? = "",

    @field:SerializedName("type")
    val type: String? = "",

    @field:SerializedName("payment_method")
    val paymentMethod: String? = "",

    @field:SerializedName("object")
    val object11: String? = ""
)

data class Merchant(

    @field:SerializedName("country")
    val country: String? = "",

    @field:SerializedName("currency")
    val currency: String? = "",

    @field:SerializedName("id")
    val id: String? = ""
)

data class Data(

    @field:SerializedName("metadata")
    val metadata: Metadata? = null,

    @field:SerializedName("threeDSecure")
    val threeDSecure: Boolean? = null,

    @field:SerializedName("description")
    val description: String? = "",

    @field:SerializedName("merchant_id")
    val merchantId: String? = "",

    @field:SerializedName("source")
    val source: Source? = null,

    @field:SerializedName("statement_descriptor")
    val statementDescriptor: String? = "",

    @field:SerializedName("reference")
    val reference: Reference? = null,

    @field:SerializedName("post")
    val post: Post? = null,

    @field:SerializedName("currency")
    val currency: String? = "",

    @field:SerializedName("id")
    val id: String? = "",

    @field:SerializedName("redirect")
    val redirect: Redirect? = null,

    @field:SerializedName("amount")
    val amount: Double? = null,

    @field:SerializedName("product")
    val product: String? = "",

    @field:SerializedName("auto_reversed")
    val autoReversed: Boolean? = null,

    @field:SerializedName("save_card")
    val saveCard: Boolean? = null,

    @field:SerializedName("method")
    val method: String? = "",

    @field:SerializedName("merchant")
    val merchant: Merchant? = null,

    @field:SerializedName("card_threeDSecure")
    val cardThreeDSecure: Boolean? = null,

    @field:SerializedName("api_version")
    val apiVersion: String? = "",

    @field:SerializedName("live_mode")
    val liveMode: Boolean? = null,

    @field:SerializedName("response")
    val response: Response? = null,

    @field:SerializedName("activities")
    val activities: List<ActivitiesItem?>? = null,

    @field:SerializedName("receipt")
    val receipt: Receipt? = null,

    @field:SerializedName("transaction")
    val transaction: Transaction? = null,

    @field:SerializedName("gateway")
    val gateway: Gateway? = null,

    @field:SerializedName("object")
    val object11: String? = "",

    @field:SerializedName("status")
    val status: String? = "",

    @field:SerializedName("customer")
    val customer: Customer? = null
)

data class Post(

    @field:SerializedName("url")
    val url: String? = "",

    @field:SerializedName("status")
    val status: String? = ""
)

data class Response(

    @field:SerializedName("code")
    val code: String? = "",

    @field:SerializedName("message")
    val message: String? = ""
)

data class Metadata(

    @field:SerializedName("udf1")
    val udf1: String? = "",

    @field:SerializedName("udf2")
    val udf2: String? = ""
)

data class Receipt(

    @field:SerializedName("sms")
    val sms: Boolean? = null,

    @field:SerializedName("id")
    val id: String? = "",

    @field:SerializedName("email")
    val email: Boolean? = null
)

data class Phone(

    @field:SerializedName("country_code")
    val countryCode: String? = "",

    @field:SerializedName("number")
    val number: String? = ""
)

data class Redirect(

    @field:SerializedName("url")
    val url: String? = "",

    @field:SerializedName("status")
    val status: String? = ""
)

data class ActivitiesItem(

    @field:SerializedName("amount")
    val amount: Double? = null,

    @field:SerializedName("created")
    val created: Long? = null,

    @field:SerializedName("currency")
    val currency: String? = "",

    @field:SerializedName("id")
    val id: String? = "",

    @field:SerializedName("remarks")
    val remarks: String? = "",

    @field:SerializedName("object")
    val object11: String? = "",

    @field:SerializedName("status")
    val status: String? = ""
)

data class Expiry(

    @field:SerializedName("period")
    val period: Int? = null,

    @field:SerializedName("type")
    val type: String? = ""
)

data class Customer(

    @field:SerializedName("phone")
    val phone: Phone? = null,

    @field:SerializedName("last_name")
    val lastName: String? = "",

    @field:SerializedName("id")
    val id: String? = "",

    @field:SerializedName("first_name")
    val firstName: String? = "",

    @field:SerializedName("email")
    val email: String? = ""
)

data class Reference(

    @field:SerializedName("payment")
    val payment: String? = "",

    @field:SerializedName("track")
    val track: String? = "",

    @field:SerializedName("acquirer")
    val acquirer: String? = "",

    @field:SerializedName("gateway")
    val gateway: String? = "",

    @field:SerializedName("transaction")
    val transaction: String? = "",

    @field:SerializedName("order")
    val order: String? = ""
)

data class Transaction(

    @field:SerializedName("amount")
    val amount: Double? = null,

    @field:SerializedName("authorization_id")
    val authorizationId: String? = "",

    @field:SerializedName("timezone")
    val timezone: String? = "",

    @field:SerializedName("created")
    val created: String? = "",

    @field:SerializedName("asynchronous")
    val asynchronous: Boolean? = null,

    @field:SerializedName("currency")
    val currency: String? = "",

    @field:SerializedName("expiry")
    val expiry: Expiry? = null
)
