package com.tigwal.data.model.register

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class RegisterResponse {

    @SerializedName("code")
    @Expose
    var code: String? = ""

    @SerializedName("status")
    @Expose
    var status: String? = ""

    @SerializedName("data")
    @Expose
    var data: Data? = null

    @SerializedName("message")
    @Expose
    var message: String? = ""

    class Data {
        //        @SerializedName("user_id")
//        @Expose
//        var userId: Int? = null
//
//        @SerializedName("user_device_id")
//        @Expose
//        var userDeviceId: String? = ""
//
//        @SerializedName("fcm_token")
//        @Expose
//        var fcmToken: String? = ""
//
//        @SerializedName("currency_symbol")
//        @Expose
//        var currency_symbol: String? = ""
//
//        @SerializedName("login_option")
//        @Expose
//        var login_option: String? = ""
//
//        @SerializedName("access_token")
//        @Expose
//        var accessToken: String? = ""
//
//        @SerializedName("name")
//        @Expose
//        var name: String? = ""
//
//        @SerializedName("mobile")
//        @Expose
//        var mobile: String? = ""
//
//        @SerializedName("country_code")
//        @Expose
//        var countryCode: String? = ""
//
//        @SerializedName("email")
//        @Expose
//        var email: String? = ""
//
//        @SerializedName("language")
//        @Expose
//        var language: String? = ""
//
//        @SerializedName("image")
//        @Expose
//        var image: String? = ""
//
//        @SerializedName("currency")
//        @Expose
//        var currency: String? = ""
//
//        @SerializedName("area")
//        @Expose
//        var area: String? = ""
//
//        @SerializedName("block")
//        @Expose
//        var block: String? = ""
//
//        @SerializedName("street")
//        @Expose
//        var street: String? = ""
//
//        @SerializedName("lan")
//        @Expose
//        var lan: String? = ""
//
//        @SerializedName("house_number")
//        @Expose
//        var houseNumber: String? = ""
//
//        @SerializedName("floor")
//        @Expose
//        var floor: String? = ""
//
//        @SerializedName("apartment_number")
//        @Expose
//        var apartmentNumber: String? = ""
//
//        @SerializedName("postal_code")
//        @Expose
//        var postalCode: String? = ""
//
//        @SerializedName("landmark")
//        @Expose
//        var landmark: String? = ""
//
//        @SerializedName("city")
//        @Expose
//        var city: String? = ""
//
//        @SerializedName("state")
//        @Expose
//        var state: String? = ""
//
//        @SerializedName("country")
//        @Expose
//        var country: String? = ""
//
//        @SerializedName("latitude")
//        @Expose
//        var latitude: String? = ""
//
//        @SerializedName("longitude")
//        @Expose
//        var longitude: String? = ""
//
//        @SerializedName("email_state")
//        @Expose
//        var emailState: String? = ""
//
//        @SerializedName("whatsapp_state")
//        @Expose
//        var whatsappState: String? = ""
//
//        @SerializedName("app_state")
//        @Expose
//        var appState: String? = ""
//
//        @SerializedName("metrics_unit")
//        @Expose
//        var metricsUnit: String? = ""
//    }

        @SerializedName("id")
        @Expose
        private var id: String? = ""

        @SerializedName("register_type")
        @Expose
        private var registerType: String? = ""

        @SerializedName("login_option")
        @Expose
        private var loginOption: String? = ""

        @SerializedName("name")
        @Expose
        private var name: String? = ""

        @SerializedName("email")
        @Expose
        private var email: String? = ""

        @SerializedName("phonecode")
        @Expose
        private var phonecode: String? = ""

        @SerializedName("phone_number")
        @Expose
        private var phoneNumber: String? = ""

        @SerializedName("profile_pic")
        @Expose
        private var profilePic: String? = ""

        @SerializedName("status")
        @Expose
        private var status: String? = ""

        @SerializedName("language")
        @Expose
        private var language: String? = ""

        @SerializedName("currency")
        @Expose
        private var currency: String? = ""

        @SerializedName("currency_symbol")
        @Expose
        private var currencySymbol: String? = ""

        @SerializedName("google_token")
        @Expose
        private var googleToken: String? = ""

        @SerializedName("facebook_token")
        @Expose
        private var facebookToken: String? = ""

        @SerializedName("twitter_token")
        @Expose
        private var twitterToken: String? = ""

        @SerializedName("appleid_token")
        @Expose
        private var appleidToken: String? = ""

        @SerializedName("modifed_date")
        @Expose
        private var modifedDate: String? = ""

        @SerializedName("created_date")
        @Expose
        private var createdDate: String? = ""

        @SerializedName("area")
        @Expose
        private var area: String? = ""

        @SerializedName("block")
        @Expose
        private var block: String? = ""

        @SerializedName("street")
        @Expose
        private var street: String? = ""

        @SerializedName("lane")
        @Expose
        private var lane: String? = ""

        @SerializedName("house_number")
        @Expose
        private var houseNumber: String? = ""

        @SerializedName("floor")
        @Expose
        private var floor: String? = ""

        @SerializedName("apartment_number")
        @Expose
        private var apartmentNumber: String? = ""

        @SerializedName("postal_code")
        @Expose
        private var postalCode: String? = ""

        @SerializedName("landmark")
        @Expose
        private var landmark: String? = ""

        @SerializedName("city")
        @Expose
        private var city: String? = ""

        @SerializedName("state")
        @Expose
        private var state: String? = ""

        @SerializedName("country")
        @Expose
        private var country: String? = ""

        @SerializedName("latitude")
        @Expose
        private var latitude: String? = ""

        @SerializedName("longitude")
        @Expose
        private var longitude: String? = ""

        @SerializedName("device_token")
        @Expose
        private var deviceToken: String? = ""

        @SerializedName("fcm_token")
        @Expose
        private var fcmToken: String? = ""

        @SerializedName("access_token")
        @Expose
        private var accessToken: String? = ""

        @SerializedName("whatsapp_notification")
        @Expose
        private var whatsappNotification: String? = ""

        @SerializedName("email_notification")
        @Expose
        private var emailNotification: String? = ""

        @SerializedName("app_notification")
        @Expose
        private var appNotification: String? = ""

        @SerializedName("matrix_unit")
        @Expose
        private var matrixUnit: String? = ""

        fun getId(): String? {
            return id
        }

        fun setId(id: String?) {
            this.id = id
        }

        fun getRegisterType(): String? {
            return registerType
        }

        fun setRegisterType(registerType: String?) {
            this.registerType = registerType
        }

        fun getLoginOption(): String? {
            return loginOption
        }

        fun setLoginOption(loginOption: String?) {
            this.loginOption = loginOption
        }

        fun getName(): String? {
            return name
        }

        fun setName(name: String?) {
            this.name = name
        }

        fun getEmail(): String? {
            return email
        }

        fun setEmail(email: String?) {
            this.email = email
        }

        fun getPhonecode(): String? {
            return phonecode
        }

        fun setPhonecode(phonecode: String?) {
            this.phonecode = phonecode
        }

        fun getPhoneNumber(): String? {
            return phoneNumber
        }

        fun setPhoneNumber(phoneNumber: String?) {
            this.phoneNumber = phoneNumber
        }

        fun getProfilePic(): String? {
            return profilePic
        }

        fun setProfilePic(profilePic: String?) {
            this.profilePic = profilePic
        }

        fun getStatus(): String? {
            return status
        }

        fun setStatus(status: String?) {
            this.status = status
        }

        fun getLanguage(): String? {
            return language
        }

        fun setLanguage(language: String?) {
            this.language = language
        }

        fun getCurrency(): String? {
            return currency
        }

        fun setCurrency(currency: String?) {
            this.currency = currency
        }

        fun getCurrencySymbol(): String? {
            return currencySymbol
        }

        fun setCurrencySymbol(currencySymbol: String?) {
            this.currencySymbol = currencySymbol
        }

        fun getGoogleToken(): String? {
            return googleToken
        }

        fun setGoogleToken(googleToken: String?) {
            this.googleToken = googleToken
        }

        fun getFacebookToken(): String? {
            return facebookToken
        }

        fun setFacebookToken(facebookToken: String?) {
            this.facebookToken = facebookToken
        }

        fun getTwitterToken(): String? {
            return twitterToken
        }

        fun setTwitterToken(twitterToken: String?) {
            this.twitterToken = twitterToken
        }

        fun getAppleidToken(): String? {
            return appleidToken
        }

        fun setAppleidToken(appleidToken: String?) {
            this.appleidToken = appleidToken
        }

        fun getModifedDate(): String? {
            return modifedDate
        }

        fun setModifedDate(modifedDate: String?) {
            this.modifedDate = modifedDate
        }

        fun getCreatedDate(): String? {
            return createdDate
        }

        fun setCreatedDate(createdDate: String?) {
            this.createdDate = createdDate
        }

        fun getArea(): String? {
            return area
        }

        fun setArea(area: String?) {
            this.area = area
        }

        fun getBlock(): String? {
            return block
        }

        fun setBlock(block: String?) {
            this.block = block
        }

        fun getStreet(): String? {
            return street
        }

        fun setStreet(street: String?) {
            this.street = street
        }

        fun getLane(): String? {
            return lane
        }

        fun setLane(lane: String?) {
            this.lane = lane
        }

        fun getHouseNumber(): String? {
            return houseNumber
        }

        fun setHouseNumber(houseNumber: String?) {
            this.houseNumber = houseNumber
        }

        fun getFloor(): String? {
            return floor
        }

        fun setFloor(floor: String?) {
            this.floor = floor
        }

        fun getApartmentNumber(): String? {
            return apartmentNumber
        }

        fun setApartmentNumber(apartmentNumber: String?) {
            this.apartmentNumber = apartmentNumber
        }

        fun getPostalCode(): String? {
            return postalCode
        }

        fun setPostalCode(postalCode: String?) {
            this.postalCode = postalCode
        }

        fun getLandmark(): String? {
            return landmark
        }

        fun setLandmark(landmark: String?) {
            this.landmark = landmark
        }

        fun getCity(): String? {
            return city
        }

        fun setCity(city: String?) {
            this.city = city
        }

        fun getState(): String? {
            return state
        }

        fun setState(state: String?) {
            this.state = state
        }

        fun getCountry(): String? {
            return country
        }

        fun setCountry(country: String?) {
            this.country = country
        }

        fun getLatitude(): String? {
            return latitude
        }

        fun setLatitude(latitude: String?) {
            this.latitude = latitude
        }

        fun getLongitude(): String? {
            return longitude
        }

        fun setLongitude(longitude: String?) {
            this.longitude = longitude
        }

        fun getDeviceToken(): String? {
            return deviceToken
        }

        fun setDeviceToken(deviceToken: String?) {
            this.deviceToken = deviceToken
        }

        fun getFcmToken(): String? {
            return fcmToken
        }

        fun setFcmToken(fcmToken: String?) {
            this.fcmToken = fcmToken
        }

        fun getAccessToken(): String? {
            return accessToken
        }

        fun setAccessToken(accessToken: String?) {
            this.accessToken = accessToken
        }

        fun getWhatsappNotification(): String? {
            return whatsappNotification
        }

        fun setWhatsappNotification(whatsappNotification: String?) {
            this.whatsappNotification = whatsappNotification
        }

        fun getEmailNotification(): String? {
            return emailNotification
        }

        fun setEmailNotification(emailNotification: String?) {
            this.emailNotification = emailNotification
        }

        fun getAppNotification(): String? {
            return appNotification
        }

        fun setAppNotification(appNotification: String?) {
            this.appNotification = appNotification
        }

        fun getMatrixUnit(): String? {
            return matrixUnit
        }

        fun setMatrixUnit(matrixUnit: String?) {
            this.matrixUnit = matrixUnit
        }

        @SerializedName("currency_short_name")
        @Expose
        private var currency_short_name: String? = ""



        fun getcurrency_short_name(): String? {
            return currency_short_name
        }

        fun setcurrency_short_name(currency_short_name: String?) {
            this.currency_short_name = currency_short_name
        }



    }
}
