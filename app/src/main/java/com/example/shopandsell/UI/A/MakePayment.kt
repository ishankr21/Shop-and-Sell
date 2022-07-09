package com.example.shopandsell.UI.A

import `in`.indiahaat.beans.razorpay.RazorpayOrderRequest
import `in`.indiahaat.beans.razorpay.RazorpayOrderResponse
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.*
import com.example.shopandsell.R
import com.example.shopandsell.api.RetrofitInstance
import com.example.shopandsell.databinding.ActivityMakePaymentBinding
import com.example.shopandsell.utli.GlideLoadImage
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.squareup.okhttp.Credentials
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MakePayment : BaseActivity(), PaymentResultListener {

    private lateinit var binding: ActivityMakePaymentBinding
    var uuid:String=""
    var orderDetails=Order()
    private lateinit var CartItemList:ArrayList<Cart_Item>
    private lateinit var ProductsList:ArrayList<Product>
    var userDetails=User()
    private var orderResponse: RazorpayOrderResponse? = null
    val razrPayBaseUrl="https://api.razorpay.com"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakePaymentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbarMakePayment)
        val actionBar=supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        orderDetails=intent.getParcelableExtra<Order>("orderDetails")!!
        getUserDetails()
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
                    orderDetails.mode="Cash on delivery"
                        FirestoreClass().addOrder(this,orderDetails)
                }
                else
                {
                    showErrorSnackBar("Wrong Captcha",true)
                }
            }
        }
        Checkout.preload(applicationContext)
        binding.onlinePayment.setOnClickListener {
           val razorpayOrderRequest=RazorpayOrderRequest(
               amount = (orderDetails.total_amount.toDouble().toInt()*100),


           )
            getOrderDetail(razorpayOrderRequest).observe(this)
            {
                startPayment(it!!)
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

    private fun getCaptcha(): MutableLiveData<CaptchaGetResponse?>
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

    private fun startPayment(razorpayOrderResponse: RazorpayOrderResponse) {

        val activity: Activity = this
        val key=getString(R.string.razorpyakey)
        val co = Checkout()
        co.setKeyID(key);
        try {
            val options = JSONObject()
            options.put("name","Shop and Sell pvt. ltd")
            options.put("description","Payment for shopping")

            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("theme.color", "#3399cc");
            options.put("currency",razorpayOrderResponse.currency);
            options.put("order_id", razorpayOrderResponse.id);
            options.put("amount",razorpayOrderResponse.amount)

            val retryObj = JSONObject()
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("email",userDetails.email)
            prefill.put("contact",userDetails.mobile.toString())

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        showErrorSnackBar("Success",false)
        showProgressDialog()
        orderDetails.mode="Online Payment"
        FirestoreClass().addOrder(this,orderDetails)
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this,"Failure $p1",Toast.LENGTH_LONG).show()
    }

    fun getOrderDetail(order: RazorpayOrderRequest): MutableLiveData<RazorpayOrderResponse?> {
        val orderLiveData = MutableLiveData<RazorpayOrderResponse?>()
        val credentials = Credentials.basic("rzp_test_e5b9m2OpP4LYlx","JalfWPF2D8990NfDvovOlfGk")
        RetrofitInstance(razrPayBaseUrl).api.getOrderDetail(order, credentials)
            .enqueue(object : Callback<RazorpayOrderResponse> {
                override fun onResponse(call: Call<RazorpayOrderResponse>, response: Response<RazorpayOrderResponse>) {
                    orderLiveData.value = response.body()
                    Log.d("ishan","${response.body()}")
                }

                override fun onFailure(call: Call<RazorpayOrderResponse>, t: Throwable) {
                    orderLiveData.value = null
                }

            })
        return orderLiveData
    }
    fun getUserDetails()
    {

        FirestoreClass().getUserDetails(this)
    }
    fun onUserAaya(user:User)
    {
        hideProgressBar()
         userDetails = user


    }
}