package com.example.shopandsell.api

import com.example.shopandsell.Models.PincodeData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface PincodeApi {

    @GET(" ")
    fun check(

    ):Call<ArrayList<PincodeData>>

}