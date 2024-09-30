package com.tigwal.app.rest

object RestConstant {

    // LOCAL ->
  //   const val BASE_URLS = "https://tigwal.sinontechs.com/api/v1/"

   //  LIVE ->
    const val BASE_URLS = "https://www.tigwal.net/api/v1/"

    const val BEARER = "Bearer "
    const val APP_INSTALLATION = "app_installation"
    const val login = "login"
    const val logout = "logout"
    const val forgot_password = "forgot_password"
    const val reset_password = "reset_password"
    const val send_otp = "send_otp"
    const val signup = "signup"
    const val check_social_id_exists = "check_social_id_exists"
    const val referesh_user = "referesh_user"
    const val update_profile = "update_profile"
    const val enable_notification = "update_notification_flag"
    const val change_password = "change_password"
    const val get_category = "get_category"
    const val get_sub_category = "get_sub_category"
    const val get_pages = "get_pages/{slug}"
    const val get_category_locations = "get_category_locations"
    const val get_location_details = "get_location_details/{slug}"
    const val addCart = "addCart"
    const val listCart = "listCart"
    const val deleteCart = "deleteCart/{delete_id}"
    const val updateCart = "updateCart/{cart_item_id}"
    const val getTapPayment = "getTab/{charges_id}"
    const val createOrder = "createOrder"
    const val get_banners = "get_banners"
    const val listOrder = "listOrder"
    const val getOrder = "getOrder/{order_id}"
    const val notifications = "notifications"
    const val getSlots = "getSlots"
    const val CancelOrder = "CancelOrder"
    const val refunds = "refunds"
    const val RecommendsList = "RecommendsList"
    const val SearchLocation = "SearchLocation"
    const val SendChat = "SendChat"
    const val ChatDetails = "ChatDetails"
    const val addRating = "addRating"
    const val ChatList = "ChatList"
    const val check_email_mobile_exists_or_not = "check_email_mobile_exists_or_not"
    const val DeleteUser = "DeleteUser"


    //Global Variable
    const val DEVICE_TYPE = "A"
    const val IS_LOGIN = "isLogin"
    const val DEVICE_Id = "user_device_id"
    const val deviceToken = "deviceToken"
    const val USER_NAME = "user_name"
    const val USER_EMAIL = "user_email"
    const val USER_COUNTRY_CODE = "user_country_code"
    const val userType = "userType"
    const val login_type = "login_type"
    const val udiid = "udiid"
    const val USER_CONTACT_NUMBER = "user_contact_number"
    const val countryshortname = "countryshortname"
    const val IS_NOTIFICATION = "IS_NOTIFICATION"
    const val USER_IMAGE = "user_image"
    const val USER_ID = "user_id"
    const val LANGUAGE = "LANGUAGE"
    const val locationID = "locationID"
    var AUTH_TOKEN = "AUTH_TOKEN"
    var addPhoto: String = ""
    var takePhoto: String = ""
    var chooseLiberary: String = ""
    var cancel: String = ""
    var socialTokenId = ""
    var socialTokenLoginType = ""
    var socialName = ""
    var socialEmail = ""
    var ongoingMenuType = "0"  // 0 for ON-GOING  and 1 for PAST ORDERS
    var productFilter = ""  // 0 for Low Cost   and 1 for High Cost
    var ContinueShopping = "0"  // 0 for Hide   and 1 for Visible
    var FIREBASE_PUSH_NOTIFICATION = "notification_action"  // 0 for Hide   and 1 for Visible

}