package com.example.shopandsell.Models

data class PincodeData(

    var Message:String="",
    var Status:String="",
    var PostOffice:ArrayList<PostOffice> = arrayListOf()

)
