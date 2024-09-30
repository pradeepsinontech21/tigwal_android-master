package com.tigwal.data.repository

import com.tigwal.data.api.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body


class AppRepository {

    suspend fun appInstallation(params: HashMap<String, String?>) =
        RetrofitClient.apiInterface.appInstallation(params = params)

    suspend fun login(params: HashMap<String, String?>) =
        RetrofitClient.apiInterface.login(params = params)

    suspend fun logout(headertoken: String, params: HashMap<String, String?>) =
        RetrofitClient.apiInterface.logout(authToken = headertoken, params = params)

    suspend fun forgot_password(params: HashMap<String, String?>) =
        RetrofitClient.apiInterface.forgot_password(params = params)

    suspend fun reset_password(params: HashMap<String, String?>) =
        RetrofitClient.apiInterface.reset_password(params = params)

    suspend fun signup(params: HashMap<String, String?>) =
        RetrofitClient.apiInterface.signup(params = params)

    suspend fun send_otp(params: HashMap<String, String?>) =
        RetrofitClient.apiInterface.send_otp(params = params)

    suspend fun check_social_id_exists(params: HashMap<String, String?>) =
        RetrofitClient.apiInterface.check_social_id_exists(params = params)

    suspend fun referesh_user(token1: String) =
        RetrofitClient.apiInterface.referesh_user(token = token1)

    suspend fun update_profile(
        token1: String,
        params: HashMap<String, RequestBody?>,
        image: MultipartBody.Part?
    ) =
        RetrofitClient.apiInterface.update_profile(token = token1, params = params, image = image)

    suspend fun enable_notification(token1: String, params: HashMap<String, String?>) =
        RetrofitClient.apiInterface.enable_notification(token = token1, params = params)

    suspend fun change_password(token1: String, params: HashMap<String, String?>) =
        RetrofitClient.apiInterface.change_password(token = token1, params = params)

    suspend fun getCategory(token1: String) =
        RetrofitClient.apiInterface.getCategory(token = token1)

    suspend fun getSubCategory(token1: String, category_id1: String) =
        RetrofitClient.apiInterface.getSubCategory(token = token1, category_id = category_id1)

    suspend fun get_cms_pages(token1: String, slug1: String) =
        RetrofitClient.apiInterface.get_cms_pages(token = token1, slug = slug1)

    suspend fun get_category_locations(
        token1: String,
        id1: String,
        vendor_id1: String,
        price_order_By1: String,
        per_page1: String,
        page1: String
    ) =
        RetrofitClient.apiInterface.get_category_locations(
            token = token1,
            id = id1,
            vendor_id = vendor_id1,
            price_order_By = price_order_By1,
            per_page = per_page1,
            page = page1
        )

    suspend fun get_location_details(token1: String, slug1: String) =
        RetrofitClient.apiInterface.get_location_details(token = token1, slug = slug1)

    suspend fun addCart(token1: String, params1: java.util.HashMap<String, String?>) =
        RetrofitClient.apiInterface.addCart(token = token1, params = params1)

    suspend fun listCart(token1: String) =
        RetrofitClient.apiInterface.listCart(token = token1)

    suspend fun deleteCart(token1: String, delete_id1: String, @Body params1: RequestBody) =
        RetrofitClient.apiInterface.deleteCart(token = token1, delete_id = delete_id1,
            params = params1)

    suspend fun updateCart(token1: String, cart_item_id1: String, @Body params1: RequestBody) =
        RetrofitClient.apiInterface.updateCart(
            token = token1,
            cart_item_id = cart_item_id1,
            params = params1
        )

    suspend fun getTapPayment(token1: String, charges_id: String) =
        RetrofitClient.apiInterface.getTapPayment(
            token = token1,
            charges_id = charges_id,
        )

//    suspend fun createOrder(token1: String, @Body params1: RequestBody, @Body params2: RequestBody) =
//        RetrofitClient.apiInterface.createOrder(
//            token = token1,
//            params = params1,
//            body = params2,
//        )

    suspend fun createOrder(token1: String, @Body params1: java.util.HashMap<String, RequestBody?>) =
        RetrofitClient.apiInterface.createOrder(
            token = token1,
            params = params1,
        )


    suspend fun get_banners(token1: String) =
        RetrofitClient.apiInterface.get_banners(
            token = token1,
        )

    suspend fun listOrder(token1: String) =
        RetrofitClient.apiInterface.listOrder(
            token = token1,
        )

    suspend fun getOrder(token1: String, order_id: String) =
        RetrofitClient.apiInterface.getOrder(
            token = token1,
            order_id = order_id
        )

    suspend fun notifications(token1: String) =
        RetrofitClient.apiInterface.notifications(
            token = token1,
        )

    suspend fun getSlots(token1: String, @Body params1: RequestBody) =
        RetrofitClient.apiInterface.getSlots(
            token = token1,
            params = params1
        )

    suspend fun CancelOrder(token1: String, @Body params1: RequestBody) =
        RetrofitClient.apiInterface.CancelOrder(
            token = token1,
            params = params1
        )

//    suspend fun refunds(token1: String, @Body params1: RequestBody) =
//        RetrofitClient.apiInterface.refunds(
//            token = token1,
//            params = params1
//        )

    suspend fun RecommendsList(token1: String) =
        RetrofitClient.apiInterface.RecommendsList(
            token = token1,
        )

    suspend fun SearchLocation(token1: String, @Body params1: RequestBody) =
        RetrofitClient.apiInterface.SearchLocation(
            token = token1,
            params = params1
        )


    suspend fun SendChat(token1: String, params1: java.util.HashMap<String, String?>) =
        RetrofitClient.apiInterface.SendChat(
            token = token1,
            params = params1
        )


    suspend fun ChatDetails(token1: String, params1: java.util.HashMap<String, String?>) =
        RetrofitClient.apiInterface.ChatDetails(
            token = token1,
            params = params1
        )

    suspend fun addRating(token1: String, params1: java.util.HashMap<String, String?>) =
        RetrofitClient.apiInterface.addRating(
            token = token1,
            params = params1
        )

    suspend fun ChatList(token1: String) =
        RetrofitClient.apiInterface.ChatList(
            token = token1,
        )

    suspend fun check_email_mobile_exists_or_not(params1: java.util.HashMap<String, String?>
    ) =
        RetrofitClient.apiInterface.check_email_mobile_exists_or_not(params = params1)

    suspend fun DeleteUser(token1: String) =
        RetrofitClient.apiInterface.DeleteUser(
            token = token1,
        )

}
