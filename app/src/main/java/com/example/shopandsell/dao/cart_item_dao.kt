package com.example.shopandsell.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.shopandsell.Models.Cart_Item

@Dao
interface cart_item_dao {

    @Insert
    suspend fun insertIntoCart(cartItem: Cart_Item)

    @Query("Delete from cart_item where id=:cartId")
    suspend fun deleteFromCart(cartId:String)

    @Query("Delete from cart_item")
    suspend fun deleteAll()

    @Query("select * from cart_item")
     fun getAllCartItems():LiveData<MutableList<Cart_Item>>

}