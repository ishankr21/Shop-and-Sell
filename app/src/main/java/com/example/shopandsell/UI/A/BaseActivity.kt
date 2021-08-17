package com.example.shopandsell.UI.A

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.shopandsell.R
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {

    private lateinit var mProgressDialog:Dialog
    var DoubleBackPressed:Boolean=false
    fun showErrorSnackBar(message:String, errorMessage:Boolean)
    {
        val snackbar=Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
        val snackBarView=snackbar.view
        if(errorMessage)
        {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.Red
                )
            )
        }
        else
        {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.Green
                )
            )
        }
        snackbar.show()
    }
    fun showProgressDialog()
    {
        mProgressDialog= Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.setCancelable(false) //can't just click on it's left and cancel it
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }
    fun hideProgressBar()
    {
        mProgressDialog.dismiss()
    }
    fun doubleBackPressed()
    {
        if(DoubleBackPressed)
        {
            super.onBackPressed()
            return
        }
        this.DoubleBackPressed=true
        Toast.makeText(this,"PressBack Again to exit", Toast.LENGTH_LONG).show()
        @Suppress("DEPRECATION")
        Handler().postDelayed({DoubleBackPressed=false},2000)
    }
}