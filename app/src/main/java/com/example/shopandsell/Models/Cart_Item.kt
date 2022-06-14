package com.example.shopandsell.Models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cart_item")
data class Cart_Item(

    val user_id:String="",
    val product_owner_id:String="",
    val product_id:String="",
    val title:String="",
    val price:String="",
    val image:String="",
    val cart_quantity:String="",
    var stock_quantity:String="",
    @PrimaryKey
    var id:String="",
    var category: String=""
) :Parcelable