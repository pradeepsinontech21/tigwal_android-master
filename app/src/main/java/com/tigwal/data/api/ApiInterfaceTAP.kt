package com.tigwal.data.api

import com.tigwal.app.rest.RestConstant
import com.tigwal.data.model.cancel_order.RefundApiResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiInterfaceTAP {
    @Headers("Content-Type:application/json")
    @POST(RestConstant.refunds)
    fun refunds( @Header("Authorization") token: String,
                 @Body params: RequestBody): Call<RefundApiResponse?>?

//    @Headers("Content-Type:application/json")
//    @POST(RestConstant.refunds)
//    suspend fun refunds(
//        @Header("Authorization") token: String,
//        @Body params: RequestBody
//    ): Response<RefundApiResponse>

}