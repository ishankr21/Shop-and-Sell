package com.example.shopandsell.UI.A

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.User
import com.example.shopandsell.databinding.ActivityUserProflieBinding
import com.example.shopandsell.utli.Constants
import com.example.shopandsell.utli.GlideLoadImage
import java.io.IOException

class UserProflieActivity : BaseActivity() {
    private lateinit var binding: ActivityUserProflieBinding
    private lateinit var userDetails:User
    var SelectedImageURI: Uri?=null
    var profileImageURL:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProflieBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS))
        {
            userDetails=intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!

        }
        binding.userProfileEdFirstName.setText(userDetails.firstName)

        binding.userProfileEdSecondName.setText(userDetails.lastName)
        binding.userProfileEdEmailID.isEnabled=false
        binding.userProfileEdEmailID.setText(userDetails.email)

        if(userDetails.profileCompleted==0)
        {
            binding.userProfileEdFirstName.isEnabled=false

            binding.userProfileEdSecondName.isEnabled=false


        }
        else
        {
            setSupportActionBar(binding.UserProfileToolbar)
            val actionBar=supportActionBar
            if(actionBar!=null)
            {
                actionBar.setDisplayHomeAsUpEnabled(true)


            }
            binding.UserProfileToolbar.setNavigationOnClickListener { onBackPressed() }
            GlideLoadImage(this).loadUserPicture(userDetails.image,binding.userProfileImage)
            if(userDetails.mobile!=0L)
            {
                binding.userProfileEdMobileNumber.setText(userDetails.mobile.toString())

            }
            if(userDetails.gender==Constants.MALE)
                binding.rbMale.isChecked=true
            else
                binding.rbFemale.isChecked=true

        }

        binding.userProfileImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            {
                 Constants.showImagePicker(this)

            }
            else
            {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ,
                    Constants.READ_EXTERNAL_STORAGE_PERMISSION_CODE
                )
            }
        }
        binding.btnSaveUserDetails.setOnClickListener {
            if(validateUserProfileDetails())
            {
                showProgressDialog()
                if(SelectedImageURI!=null)
                FirestoreClass().uploadImageToCloudStorage(this,SelectedImageURI,"user")
               else
                {
                        uploadUserDetails()
                }

            }
        }



    }
    private fun uploadUserDetails()
    {
        val userHashMap=HashMap<String,Any>()
        val firstName=binding.userProfileEdFirstName.text.toString().trim{ it <= ' '}
        if(firstName!=userDetails.firstName)
            userHashMap[Constants.FIRSTNAME]=firstName
        val lastName=binding.userProfileEdSecondName.text.toString().trim{ it <= ' '}
        if(lastName!=userDetails.lastName)
            userHashMap[Constants.LASTNAME]=lastName


        val mobileNumber=binding.userProfileEdMobileNumber.text.toString().trim{ it <= ' '}
        val gender= if(binding.rbMale.isChecked)
        {
            Constants.MALE
        }
        else
        {
            Constants.FEMALE
        }
        if(profileImageURL.isNotEmpty())
        {
            userHashMap[Constants.IMAGE]=profileImageURL
        }
        if(mobileNumber.isNotEmpty() && mobileNumber!=userDetails.mobile.toString())
        {
            userHashMap[Constants.MOBILE]=mobileNumber.toLong()

        }
        if(gender.isNotEmpty() && gender!=userDetails.gender)
        userHashMap[Constants.GENDER]=gender

        userHashMap[Constants.PROFILE_COMPLETED]=1

        FirestoreClass().updateUserProfileData(this,userHashMap)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==Constants.READ_EXTERNAL_STORAGE_PERMISSION_CODE)
        {
            if(grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED)

            {
                Constants.showImagePicker(this)
            }

        }
        else
        {
            showErrorSnackBar("Permission Denied Please Provide Permission From Settings!",true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK)
        {
            if(requestCode==Constants.READ_IMAGE_REQUEST_CODE)
            {
                if(data!=null)
                {
                    try {
                        SelectedImageURI=data.data!!
                        GlideLoadImage(this).loadUserPicture(SelectedImageURI!!,binding.userProfileImage)
                    }catch (e:IOException)
                    {
                        e.printStackTrace()
                        Toast.makeText(this,"Some Error occured",Toast.LENGTH_LONG).show()

                    }
                }
            }
        }
    }
    private fun validateUserProfileDetails():Boolean
    {
        return when{
            TextUtils.isEmpty(binding.userProfileEdMobileNumber.text.toString().trim{it <= ' '})->
            {
                showErrorSnackBar("Please Enter A Valid Mobile Number",true)
                false
            }
            else ->
            {
                true
            }
        }
    }
    fun userProfileUpdateSuccess()
    {
        hideProgressBar()
        Toast.makeText(this,"Profile Updated Successfully",Toast.LENGTH_LONG).show()
        startActivity(Intent(this,DashBoardActivity::class.java))
        finish()
    }
    fun imageUploadSuccess(imageURL:String)
    {

        profileImageURL=imageURL
        uploadUserDetails()
    }


}