package com.example.shopandsell.UI.A

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.Product
import com.example.shopandsell.R
import com.example.shopandsell.databinding.ActivityAddProductsBinding
import com.example.shopandsell.utli.Constants
import com.example.shopandsell.utli.GlideLoadImage
import java.io.IOException

class AddProducts : BaseActivity() {
    private lateinit var binding: ActivityAddProductsBinding
    var SelectedImageURI: Uri?=null
    var imageUrl:String=""
    var spinnerArrayCategory: Array<String> = arrayOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.AddProductsToolbar)
        spinnerArrayCategory= resources.getStringArray(R.array.categories)
        initSpinners()
        val actionBar=supportActionBar
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        binding.AddProductsToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.btnSubmitProduct.setOnClickListener {
             if(validateUserProductDetails())
             {
                 showProgressDialog()
                 FirestoreClass().uploadImageToCloudStorage(this,SelectedImageURI,"product")
             }




        }
        binding.addPhotoButton.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK)
        {
            if(requestCode==Constants.READ_IMAGE_REQUEST_CODE)
            {
                if(data!=null)
                {
                    binding.addPhotoButton.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_baseline_edit_24))
                    try {
                        SelectedImageURI=data.data!!
                        GlideLoadImage(this).loadUserPicture(SelectedImageURI!!,binding.productImage)
                    }catch (e: IOException)
                    {
                        e.printStackTrace()
                        Toast.makeText(this,"Some Error occured", Toast.LENGTH_LONG).show()

                    }
                }
            }
        }
    }
    private fun validateUserProductDetails():Boolean
    {
        return when{
            SelectedImageURI==null->{

                showErrorSnackBar("Please select the product image",true)
                false

            }
            TextUtils.isEmpty(binding.EdProductTitle.text.toString().trim{it <= ' '})->
            {
                showErrorSnackBar("Please Enter The Product Title",true)
                false
            }
            TextUtils.isEmpty(binding.EdProductPrice.text.toString().trim{it <= ' '})->
            {
                showErrorSnackBar("Please Enter The Product Price",true)
                false
            }
            TextUtils.isEmpty(binding.EdProductDescription.text.toString().trim{it <= ' '})->
            {
                showErrorSnackBar("Please Enter The Product Description",true)
                false
            }
            TextUtils.isEmpty(binding.EdProductQuantity.text.toString().trim{it <= ' '})->
            {
                showErrorSnackBar("Please Enter The Product Quantity",true)
                false
            }

            else ->
            {
                true
            }
        }
    }
    fun imageUploadSuccess(imageURL:String)
    {

         imageUrl=imageURL
        uploadProductDetails()



    }
    fun productUploadSuccess()
    {
        hideProgressBar()
        Toast.makeText(this,"Product Uploaded SucccessFully",Toast.LENGTH_LONG).show()
        finish()

    }
    fun uploadProductDetails()
    {
        val username=this.getSharedPreferences(Constants.SHOPANDSELL_PREFERENCES, MODE_PRIVATE).getString(Constants.LOGGED_IN_UserNAME,"")!!
        val product=Product(
            FirestoreClass().getCurrentUserID(),
            username,
            binding.EdProductTitle.text.toString().trim(){ it<= ' '},
            binding.EdProductPrice.text.toString().trim(){ it<= ' '},
            binding.EdProductDescription.text.toString().trim(){ it<= ' '},
            binding.EdProductQuantity.text.toString().trim(){ it<= ' '},
            imageUrl,
            category = binding.spnSelectProductCategory.selectedItem.toString()

        )
        FirestoreClass().addProduct(this,product)


    }

    private fun initSpinners() {

        val arrayAdapterGender = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            spinnerArrayCategory
        ) {

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val item = view as TextView
                item.run {
                    this.isSingleLine = false
                }

                return item
            }
        }

        binding.spnSelectProductCategory.adapter = arrayAdapterGender

    }




}