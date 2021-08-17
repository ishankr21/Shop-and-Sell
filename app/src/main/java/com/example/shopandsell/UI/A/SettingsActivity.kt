package com.example.shopandsell.UI.A

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.User
import com.example.shopandsell.R
import com.example.shopandsell.databinding.ActivityLoginBinding
import com.example.shopandsell.databinding.ActivitySettingsBinding
import com.example.shopandsell.utli.Constants
import com.example.shopandsell.utli.GlideLoadImage
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : BaseActivity(){
    private lateinit var binding: ActivitySettingsBinding
    lateinit var userDetails:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.SettingsToolbar)
        val actionBar=supportActionBar
        binding.userProfileImage.clipToOutline=true
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        binding.SettingsToolbar.setNavigationOnClickListener { onBackPressed() }
        getUserDetailsinSettings()
        binding.btnLOGOUT.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent= Intent(this,LoginActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        binding.SettingsbtnEdit.setOnClickListener {
            val intent=Intent(this,UserProflieActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,userDetails)
            startActivity(intent)
        }
        binding.selectAddresses.setOnClickListener {
            val intent=Intent(this,AddressActivity::class.java)

            startActivity(intent)
        }
    }
    fun getUserDetailsinSettings()
    {
        showProgressDialog()
        FirestoreClass().getUserDetails(this)
    }
    fun onUserReceived(user:User)
    {
        hideProgressBar()
        userDetails=user
       GlideLoadImage(this).loadUserPicture(user.image,binding.userProfileImage)
        binding.settingTextViewName.text=user.firstName+" "+user.lastName
        binding.settingTextViewEmailID.text=user.email
        binding.settingTextViewMobileNumber.text=user.mobile.toString()


    }
}