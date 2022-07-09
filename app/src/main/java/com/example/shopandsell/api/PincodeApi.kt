package com.example.shopandsell.api

import `in`.indiahaat.beans.razorpay.RazorpayOrderRequest
import `in`.indiahaat.beans.razorpay.RazorpayOrderResponse
import com.example.shopandsell.Models.CaptchaCheckContent
import com.example.shopandsell.Models.CaptchaCheckResponse
import com.example.shopandsell.Models.CaptchaGetResponse
import com.example.shopandsell.Models.PincodeData
import retrofit2.Call
import retrofit2.http.*


interface PincodeApi {

    @GET(" ")
    fun check(

    ):Call<ArrayList<PincodeData>>

    @GET("generate/")
    fun getCaptcha():Call<CaptchaGetResponse>

    @Headers("Content-Type: application/json")
    @POST("verify/")
    fun checkCaptcha(
        @Body
        captchaCheckContent: CaptchaCheckContent
    ):Call<CaptchaCheckResponse>

    @Headers("content-type: application/json")
    @POST("/v1/orders")
    fun getOrderDetail(@Body order: RazorpayOrderRequest, @Header("Authorization") auth: String): Call<RazorpayOrderResponse>

}