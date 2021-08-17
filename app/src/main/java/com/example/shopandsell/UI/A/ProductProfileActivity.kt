package com.example.shopandsell.UI.A

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.Product
import com.example.shopandsell.Models.User
import com.example.shopandsell.R
import com.example.shopandsell.UI.Fragments.ProductsFragment
import com.example.shopandsell.databinding.ActivityAddProductsBinding
import com.example.shopandsell.databinding.ActivityProductProfileBinding
import com.example.shopandsell.databinding.ActivityUserProflieBinding
import com.example.shopandsell.utli.Constants
import com.example.shopandsell.utli.GlideLoadImage
import java.io.IOException

class ProductProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityProductProfileBinding
    private lateinit var productDetails: Product
    var SelectedProductImageURI: Uri?=null
    var productImageURL:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.ProductProfileToolbar)
        val actionBar=supportActionBar
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)


        }
        binding.ProductProfileToolbar.setNavigationOnClickListener { onBackPressed() }
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_DETAILS))
        {
            productDetails=intent.getParcelableExtra(Constants.EXTRA_PRODUCT_DETAILS)!!
            getProductDetailsinProfile(productDetails.product_id)

        }
        binding.editProductPhoto.setOnClickListener {
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
        binding.btnSaveProduct.setOnClickListener {
            if(validateProductDetails())
            {
                showProgressDialog()
                if(SelectedProductImageURI!=null)
                    FirestoreClass().uploadImageToCloudStorage(this,SelectedProductImageURI,"image")
                else
                {
                    uploadProductDetails()
                }

            }
        }




    }
    fun getProductDetailsinProfile(productId:String)
    {
        showProgressDialog()
        FirestoreClass().getProductDetails(this,productId)
    }
    fun onProductReceived(product:Product)
    {
        hideProgressBar()

        GlideLoadImage(this).loadUserPicture(product.image,binding.ProductProfileImage)
        binding.EdProductProfileTitle.setText(product.title)
        binding.EdProductProfileDescription.setText(product.description)
        binding.EdProductProfilePrice.setText(product.price)
        binding.EdProductProfileQuantity.setText(product.stock_quantity)


    }
    private fun uploadProductDetails()
    {
        val productHashMap=HashMap<String,Any>()
        val productTitle=binding.EdProductProfileTitle.text.toString().trim{ it <= ' '}
        if(productTitle!=productDetails.title)
            productHashMap[Constants.TITLE]=productTitle
        val productPrice=binding.EdProductProfilePrice.text.toString().trim{ it <= ' '}
        if(productPrice!=productDetails.price)
            productHashMap[Constants.PRICE]=productPrice
        val productDescription=binding.EdProductProfileDescription.text.toString().trim{ it <= ' '}
        if(productDescription!=productDetails.description)
            productHashMap[Constants.DESCRIPTION]=productDescription
        val productQuantity=binding.EdProductProfileQuantity.text.toString().trim{ it <= ' '}
        if(productQuantity!=productDetails.stock_quantity)
            productHashMap[Constants.QUANTITY]=productQuantity
        if(productImageURL.isNotEmpty())
        {
            productHashMap[Constants.IMAGE]=productImageURL
        }


        FirestoreClass().updateProductProfileData(this,productHashMap,productDetails.product_id)

    }
    private fun validateProductDetails():Boolean
    {
        return when{

            TextUtils.isEmpty(binding.EdProductProfileTitle.text.toString().trim{it <= ' '})->
            {
                showErrorSnackBar("Please Enter The Product Title",true)
                false
            }
            TextUtils.isEmpty(binding.EdProductProfilePrice.text.toString().trim{it <= ' '})->
            {
                showErrorSnackBar("Please Enter The Product Price",true)
                false
            }
            TextUtils.isEmpty(binding.EdProductProfileDescription.text.toString().trim{it <= ' '})->
            {
                showErrorSnackBar("Please Enter The Product Description",true)
                false
            }
            TextUtils.isEmpty(binding.EdProductProfileQuantity.text.toString().trim{it <= ' '})->
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
    fun productProfileUpdateSuccess()
    {
        hideProgressBar()
        Toast.makeText(this,"Product Updated Successfully", Toast.LENGTH_LONG).show()
         
        finish()
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
        if(resultCode== Activity.RESULT_OK)
        {
            if(requestCode==Constants.READ_IMAGE_REQUEST_CODE)
            {
                if(data!=null)
                {
                    try {
                        SelectedProductImageURI=data.data!!
                        GlideLoadImage(this).loadUserPicture(SelectedProductImageURI!!,binding.ProductProfileImage)
                    }catch (e: IOException)
                    {
                        e.printStackTrace()
                        Toast.makeText(this,"Some Error occurred",Toast.LENGTH_LONG).show()

                    }
                }
            }
        }
    }
    fun imageUploadSuccess(imageURL:String)
    {

        productImageURL=imageURL
        uploadProductDetails()
    }
}