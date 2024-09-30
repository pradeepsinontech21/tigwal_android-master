package com.tigwal.data.api

import com.tigwal.data.model.search.SearchListResponse
import com.tigwal.app.rest.RestConstant
import com.tigwal.data.model.DeleteUserResponse
import com.tigwal.data.model.addtocart.AddToCartResponse
import com.tigwal.data.model.banners.BannersResponse
import com.tigwal.data.model.cancel_order.CancelOrderResponse
import com.tigwal.data.model.cancel_order.RefundApiResponse
import com.tigwal.data.model.cms_page.CMSResponse
import com.tigwal.data.model.cart_item.CartItemResponse
import com.tigwal.data.model.cart_item.UpdateCartItemResponse
import com.tigwal.data.model.chat.ChatDetailsResponse
import com.tigwal.data.model.chat.SendChatResponse
import com.tigwal.data.model.chatlist.ChatListResponse
import com.tigwal.data.model.check_email_mobile_exist.CheckEmailMobileExitsResponse
import com.tigwal.data.model.forgotpassword.ForgotPasswordResponse
import com.tigwal.data.model.getTimeSlot.GetTimeSlotResposne
import com.tigwal.data.model.getcategory.CategoryResponse
import com.tigwal.data.model.listCart.CartListResponse
import com.tigwal.data.model.listOrder.ListOrderResponse
import com.tigwal.data.model.login.LoginResponse
import com.tigwal.data.model.notification_enable.NotificationUpdateResponse
import com.tigwal.data.model.notification_list.NotificationListResponse
import com.tigwal.data.model.order_detail.OrderDetailResponse
import com.tigwal.data.model.otp.SendOtpResponse
import com.tigwal.data.model.product_detail.ProductDetailResponse
import com.tigwal.data.model.product_listing.ProductListResponse
import com.tigwal.data.model.rating.RatingResponse
import com.tigwal.data.model.recommendation.RecommendationResponse
import com.tigwal.data.model.resetpassword.ResetPasswordResponse
import com.tigwal.data.model.splash.AppInstallationResponse
import com.tigwal.data.model.tap_payment.TapPaymentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST(RestConstant.APP_INSTALLATION)
    suspend fun appInstallation(@FieldMap params: HashMap<String, String?>): Response<AppInstallationResponse>

    @FormUrlEncoded
    @POST(RestConstant.login)
    suspend fun login(@FieldMap params: HashMap<String, String?>): Response<LoginResponse>

    @FormUrlEncoded
    @POST(RestConstant.logout)
    suspend fun logout(
        @Header("Authorization") authToken: String,
        @FieldMap params: HashMap<String, String?>
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST(RestConstant.forgot_password)
    suspend fun forgot_password(@FieldMap params: HashMap<String, String?>): Response<ForgotPasswordResponse>

    @FormUrlEncoded
    @POST(RestConstant.reset_password)
    suspend fun reset_password(@FieldMap params: HashMap<String, String?>): Response<ResetPasswordResponse>

    @FormUrlEncoded
    @POST(RestConstant.signup)
    suspend fun signup(@FieldMap params: HashMap<String, String?>): Response<LoginResponse>

    @FormUrlEncoded
    @POST(RestConstant.send_otp)
    suspend fun send_otp(@FieldMap params: HashMap<String, String?>): Response<SendOtpResponse>

    @FormUrlEncoded
    @POST(RestConstant.check_social_id_exists)
    suspend fun check_social_id_exists(@FieldMap params: HashMap<String, String?>): Response<LoginResponse>

    @Headers("Content-Type:application/json")
    @GET(RestConstant.referesh_user)
    suspend fun referesh_user(@Header("Authorization") token: String): Response<LoginResponse>

    @Multipart
    @POST(RestConstant.update_profile)
    suspend fun update_profile(
        @Header("Authorization") token: String,
        @PartMap params: HashMap<String, RequestBody?>,
        @Part image: MultipartBody.Part?
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST(RestConstant.enable_notification)
    suspend fun enable_notification(
        @Header("Authorization") token: String,
        @FieldMap params: HashMap<String, String?>
    ): Response<NotificationUpdateResponse>

    @FormUrlEncoded
    @POST(RestConstant.change_password)
    suspend fun change_password(
        @Header("Authorization") token: String,
        @FieldMap params: HashMap<String, String?>
    ): Response<NotificationUpdateResponse>

    @Headers("Content-Type:application/json")
    @GET(RestConstant.get_category)
    suspend fun getCategory(@Header("Authorization") token: String): Response<CategoryResponse>

    @Headers("Content-Type:application/json")
    @GET(RestConstant.get_sub_category)
    suspend fun getSubCategory(
        @Header("Authorization") token: String,
        @Query("category_id") category_id: String
    ): Response<CategoryResponse>

    @Headers("Content-Type:application/json")
    @GET(RestConstant.get_pages)
    suspend fun get_cms_pages(
        @Header("Authorization") token: String,
        @Path("slug") slug: String
    ): Response<CMSResponse>

    @Headers("Content-Type:application/json")
    @GET(RestConstant.get_category_locations)
    suspend fun get_category_locations(
        @Header("Authorization") token: String,
        @Query("id") id: String,
        @Query("vendor_id") vendor_id: String,
        @Query("price_order_By") price_order_By: String,
        @Query("per_page") per_page: String,
        @Query("page") page: String
    ): Response<ProductListResponse>

    @Headers("Content-Type:application/json")
    @GET(RestConstant.get_location_details)
    suspend fun get_location_details(
        @Header("Authorization") token: String,
        @Path("slug") slug: String
    ): Response<ProductDetailResponse>

    @Headers("Content-Type:application/json")
    @POST(RestConstant.addCart)
    suspend fun addCart(
        @Header("Authorization") token: String,
        @Body params: HashMap<String, String?>
    ): Response<AddToCartResponse>

    @Headers("Content-Type:application/json")
    @GET(RestConstant.listCart)
    suspend fun listCart(
        @Header("Authorization") token: String
    ): Response<CartListResponse>

    @Headers("Content-Type:application/json")
    @POST(RestConstant.deleteCart)
    suspend fun deleteCart(
        @Header("Authorization") token: String,
        @Path("delete_id") delete_id: String,
        @Body params: RequestBody
    ): Response<CartItemResponse>

    @Headers("Content-Type:application/json")
    @POST(RestConstant.updateCart)
    suspend fun updateCart(
        @Header("Authorization") token: String,
        @Path("cart_item_id") cart_item_id: String,
        @Body params: RequestBody
    ): Response<UpdateCartItemResponse>

    @Headers("Content-Type:application/json")
    @GET(RestConstant.getTapPayment)
    suspend fun getTapPayment(
        @Header("Authorization") token: String,
        @Path("charges_id") charges_id: String
    ): Response<TapPaymentResponse>

//    @Headers("Content-Type:application/json")
//    @POST(RestConstant.createOrder)
//    suspend fun createOrder(
//        @Header("Authorization") token: String,
//        @Body params: RequestBody,
//        @Body body: RequestBody
//    ): Response<TapPaymentResponse>
    @Multipart
    @POST(RestConstant.createOrder)
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @PartMap params: HashMap<String, RequestBody?>
    ): Response<TapPaymentResponse>

    @Headers("Content-Type:application/json")
    @GET(RestConstant.get_banners)
    suspend fun get_banners(
        @Header("Authorization") token: String,
    ): Response<BannersResponse>

    @GET(RestConstant.listOrder)
    suspend fun listOrder(
        @Header("Authorization") token: String,
    ): Response<ListOrderResponse>

    @GET(RestConstant.getOrder)
    suspend fun getOrder(
        @Header("Authorization") token: String,
        @Path("order_id") order_id: String
    ): Response<OrderDetailResponse>

    @GET(RestConstant.notifications)
    suspend fun notifications(
        @Header("Authorization") token: String,
    ): Response<NotificationListResponse>

    @Headers("Content-Type:application/json")
    @POST(RestConstant.getSlots)
    suspend fun getSlots(
        @Header("Authorization") token: String,
        @Body params: RequestBody
    ): Response<GetTimeSlotResposne>

    @Headers("Content-Type:application/json")
    @POST(RestConstant.CancelOrder)
    suspend fun CancelOrder(
        @Header("Authorization") token: String,
        @Body params: RequestBody
    ): Response<CancelOrderResponse>

//    @Headers("Content-Type:application/json")
//    @POST(RestConstant.refunds)
//    suspend fun refunds(
//        @Header("Authorization") token: String,
//        @Body params: RequestBody
//    ): Response<RefundApiResponse>

    @GET(RestConstant.RecommendsList)
    suspend fun RecommendsList(
        @Header("Authorization") token: String,
    ): Response<RecommendationResponse>


    @Headers("Content-Type:application/json")
    @POST(RestConstant.SearchLocation)
    suspend fun SearchLocation(
        @Header("Authorization") token: String,
        @Body params: RequestBody
    ): Response<SearchListResponse>


    @FormUrlEncoded
    @POST(RestConstant.SendChat)
    suspend fun SendChat(
        @Header("Authorization") token: String,
        @FieldMap params: HashMap<String, String?>
    ): Response<SendChatResponse>

    @FormUrlEncoded
    @POST(RestConstant.ChatDetails)
    suspend fun ChatDetails(
        @Header("Authorization") token: String,
        @FieldMap params: HashMap<String, String?>
    ): Response<ChatDetailsResponse>

    @FormUrlEncoded
    @POST(RestConstant.addRating)
    suspend fun addRating(
        @Header("Authorization") token: String,
        @FieldMap params: HashMap<String, String?>
    ): Response<RatingResponse>

    @GET(RestConstant.ChatList)
    suspend fun ChatList(
        @Header("Authorization") token: String,
    ): Response<ChatListResponse>


    @FormUrlEncoded
    @POST(RestConstant.check_email_mobile_exists_or_not)
    suspend fun check_email_mobile_exists_or_not(@FieldMap params: HashMap<String, String?>
    ): Response<CheckEmailMobileExitsResponse>

    @GET(RestConstant.DeleteUser)
    suspend fun DeleteUser(
        @Header("Authorization") token: String,
    ): Response<DeleteUserResponse>

}