package com.example.shopandsell.UI.A

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.shopandsell.R
import com.example.shopandsell.utli.Constants

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val sharedPreferences=getSharedPreferences(Constants.SHOPANDSELL_PREFERENCES,Context.MODE_PRIVATE)
        val username=sharedPreferences.getString(Constants.LOGGED_IN_UserNAME,"")!!
        val tv:TextView=findViewById(R.id.tvmain)
        tv.text=username
    }
}