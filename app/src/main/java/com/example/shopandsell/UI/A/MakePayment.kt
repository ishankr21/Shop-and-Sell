package com.example.shopandsell.UI.A

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.*
import com.example.shopandsell.api.RetrofitInstance
import com.example.shopandsell.databinding.ActivityMakePaymentBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MakePayment : BaseActivity() {

    private lateinit var binding: ActivityMakePaymentBinding
    var uuid:String=""
    var orderDetails=Order()
    private lateinit var CartItemList:ArrayList<Cart_Item>
    private lateinit var ProductsList:ArrayList<Product>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakePaymentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbarMakePayment)
        val actionBar=supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        orderDetails=intent.getParcelableExtra<Order>("orderDetails")!!

        getProductList()
        binding.toolbarMakePayment.setNavigationOnClickListener { onBackPressed() }
        binding.imgRefreshCaptcha.setOnClickListener {
            showProgressDialog()
            val captchaData=getCaptcha()
            captchaData.observe(this)
            {
                hideProgressBar()
                binding.captcha.visibility= View.VISIBLE
                val bitmap=decodeBase64(it?.captcha!!.drop(23))
                binding.captchaImage.setImageBitmap(bitmap)
                uuid=it.uuid
            }
        }
        binding.codPayment.setOnClickListener{
            showProgressDialog()
            var captchaData=getCaptcha()
            captchaData.observe(this)
            {
                hideProgressBar()
                binding.captcha.visibility= View.VISIBLE
                var bitmap=decodeBase64(it?.captcha!!.drop(23))
                binding.captchaImage.setImageBitmap(bitmap)
                uuid=it.uuid
            }
        }

        binding.btnVerifyCaptcha.setOnClickListener {
            showProgressDialog()
            var captchaVerification=checkCaptcha(CaptchaCheckContent(uuid,binding.etEnterCaptcha.text.toString()))
            captchaVerification.observe(this)
            {
                hideProgressBar()
                if(it?.message=="CAPTCHA_SOLVED")
                {
                    showErrorSnackBar("Success",false)
                           showProgressDialog()

                        FirestoreClass().addOrder(this,orderDetails)
                }
                else
                {
                    showErrorSnackBar("Wrong Captcha",true)
                }
            }
        }
    }

    private fun checkCaptcha(captchaCheckContent: CaptchaCheckContent): MutableLiveData<CaptchaCheckResponse?> {
        Log.d("ishan","${captchaCheckContent}")
        val captchaCheckResponse= MutableLiveData<CaptchaCheckResponse?>()
        RetrofitInstance("https://captcha-api.akshit.me/v2/").api.checkCaptcha(captchaCheckContent).enqueue(
            object : Callback<CaptchaCheckResponse>
            {
                override fun onResponse(call: Call<CaptchaCheckResponse>, response: Response<CaptchaCheckResponse>) {
                    Log.d("ishan","success ${response.body()}")
                    captchaCheckResponse.value=response.body()

                }

                override fun onFailure(call: Call<CaptchaCheckResponse>, t: Throwable) {
                    Log.d("ishan","error")
                    captchaCheckResponse.value = null
                }
            }
        )

        return captchaCheckResponse

    }

    fun getCaptcha(): MutableLiveData<CaptchaGetResponse?>
    {
        val captchaGetResponse= MutableLiveData<CaptchaGetResponse?>()
        RetrofitInstance("https://captcha-api.akshit.me/v2/").api.getCaptcha().enqueue(
            object : Callback<CaptchaGetResponse>
            {
                override fun onResponse(call: Call<CaptchaGetResponse>, response: Response<CaptchaGetResponse>) {
                    Log.d("ishan","${response.body()}")
                    captchaGetResponse.value=response.body()

                }

                override fun onFailure(call: Call<CaptchaGetResponse>, t: Throwable) {
                    Log.d("ishan","error")
                    captchaGetResponse.value = null
                }
            }
        )

        return captchaGetResponse

    }
    private fun decodeBase64(input: String?): Bitmap? {
        val decodedByte: ByteArray = Base64.decode(input, Base64.DEFAULT)

        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
    }
    private fun getProductList()
    {
        showProgressDialog()
        FirestoreClass().getAllProductList(this)
    }
    fun successProductsListFromFireStore(productsList:ArrayList<Product>)
    {
        hideProgressBar()
        ProductsList= productsList
        getCartItemsList()

    }
    fun orderUploadSuccess() {
        Log.d("ishan","${CartItemList}")
        FirestoreClass().updateAllDetails(this,CartItemList ,orderDetails)
    }
    private fun getCartItemsList()
    {
        showProgressDialog()
        FirestoreClass().getCartItemList(this)
    }
    fun allDetailsUpdateSuccessfully() {
        hideProgressBar()
        Toast.makeText(this,"Order Placed SuccessFully!", Toast.LENGTH_LONG).show()
        val intent= Intent(this,DashBoardActivity::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun successCartItemsList(cartItemsList: ArrayList<Cart_Item>) {
        hideProgressBar()

        CartItemList=cartItemsList
        for(product in ProductsList)
        {
            for(cartItem in CartItemList)
            {
                if(product.product_id==cartItem.product_id)
                    cartItem.stock_quantity=product.stock_quantity
            }
        }


    }
}