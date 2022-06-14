package com.example.shopandsell.api

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance(baseUrl: String) {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: PincodeApi by lazy {
        retrofit.create(PincodeApi::class.java)
    }

}