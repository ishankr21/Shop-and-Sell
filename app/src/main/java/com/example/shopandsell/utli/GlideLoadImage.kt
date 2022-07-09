package com.example.shopandsell.utli

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.shopandsell.R
import java.io.IOException


class GlideLoadImage(val context: Context) {
    
    fun loadUserPicture(imageURI: Any,imageview:ImageView)
    {
        try {
            Glide
                .with(context)
                .load(imageURI)
                .centerCrop()
                .placeholder(R.drawable.person_placeholder)
                .into(imageview)

        }
        catch(e:IOException)
        {
            e.printStackTrace()
        }
    }

    fun loadProductPicture(imageURI: Any,imageview:ImageView)
    {
        try {
            Glide
                .with(context)
                .load(imageURI)
                .centerCrop()
                .placeholder(R.drawable.person_placeholder)
                .into(imageview)

        }
        catch(e:IOException)
        {
            e.printStackTrace()
        }
    }
}