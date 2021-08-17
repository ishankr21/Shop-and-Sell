package com.example.shopandsell.Models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart_Item(

    val user_id:String="",
    val product_owner_id:String="",
    val product_id:String="",
    val title:String="",
    val price:String="",
    val image:String="",
    val cart_quantity:String="",
    var stock_quantity:String="",
    var id:String=""
) :Parcelable