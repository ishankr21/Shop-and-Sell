package com.example.shopandsell.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shopandsell.Models.Cart_Item
import com.example.shopandsell.dao.cart_item_dao

@Database(entities = [Cart_Item::class ], version = 1)
abstract class Database : RoomDatabase() {

    abstract val cartDao: cart_item_dao


    companion object{
        @Volatile
        private var INSTANCE : com.example.shopandsell.database.Database? = null

        fun getInstance(context: Context) : com.example.shopandsell.database.Database {

            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    com.example.shopandsell.database.Database::class.java,
                    "database"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }
        }

        fun initDatabase(context: Context): com.example.shopandsell.database.Database {
            return getInstance(context)
        }
    }
}