package com.example.shopandsell.utli

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class MyEditText(context: Context,attributes: AttributeSet):AppCompatEditText(context,attributes) {
    init {
        applyFont();
    }

    fun applyFont()
    {
        val typeface:Typeface= Typeface.createFromAsset(context.assets,"Roboto-Regular.ttf")

        setTypeface(typeface)
    }
}