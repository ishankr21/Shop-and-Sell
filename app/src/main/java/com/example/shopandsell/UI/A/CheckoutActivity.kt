package com.example.shopandsell.UI.A

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopandsell.Adapter.CartItemAdapter
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.Address
import com.example.shopandsell.Models.Cart_Item
import com.example.shopandsell.Models.Order
import com.example.shopandsell.Models.Product
import com.example.shopandsell.R
import com.example.shopandsell.databinding.ActivityAddProductsBinding
import com.example.shopandsell.databinding.ActivityCheckoutBinding
import com.example.shopandsell.utli.Constants

class CheckoutActivity : BaseActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var addressDetails:Address
    private lateinit var ProductsList:ArrayList<Product>
    private lateinit var CartItemList:ArrayList<Cart_Item>
    private var mSubTotal:Double=0.0
    private var mTotalAmount:Double=0.0
    private lateinit var mOrderDetails:Order
    val CHANNEL_ID="channelID"
    val CHANNEL_NAME ="channelName"
    val NOTIFICATION_ID=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbarCheckoutActivity)
        val actionBar=supportActionBar
        createNotificationChannel()
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        binding.toolbarCheckoutActivity.setNavigationOnClickListener { onBackPressed() }
        if(intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS))
        {
            addressDetails= intent.getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS)!!
            binding.tvCheckoutFullName.setText(addressDetails?.name)
            binding.tvCheckoutAddress.setText(addressDetails?.address)
            binding.tvCheckoutAdditionalNote.setText(addressDetails?.additionalNote)
            binding.tvCheckoutAddressType.setText(addressDetails?.type)
            binding.tvCheckoutMobileNumber.setText(addressDetails?.mobileNumber)

        }
        getProductList()
        binding.btnPlaceOrder.setOnClickListener {
            placeAnOrder()
        }

    }
    fun successProductsListFromFireStore(productsList:ArrayList<Product>)
    {

        ProductsList= productsList
        getCartItemsList()
    }
    private fun getCartItemsList()
    {
        FirestoreClass().getCartItemList(this)
    }
    private fun getProductList()
    {
        showProgressDialog()
        FirestoreClass().getAllProductList(this)
    }
    fun successCartItemsList(cartList:ArrayList<Cart_Item>)
    {
        hideProgressBar()
        CartItemList=cartList
        for(product in ProductsList)
        {
            for(cartItem in CartItemList)
            {
                if(product.product_id==cartItem.product_id)
                    cartItem.stock_quantity=product.stock_quantity
            }
        }

        binding.rvCartListItems.layoutManager=LinearLayoutManager(this)
        binding.rvCartListItems.setHasFixedSize(true)
        val adapter=CartItemAdapter(this,CartItemList,false)
        binding.rvCartListItems.adapter=adapter
        for(item in CartItemList)
        {
            val availableQuantity=item.stock_quantity.toInt()
            if(availableQuantity>0)
            {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += price*quantity
            }
        }
        binding.tvCheckoutSubTotal.text= "Rs. $mSubTotal"
        binding.tvCheckoutShippingCharge.text="Rs 100"
        if(mSubTotal>0)
        {
            binding.llCheckoutPlaceOrder.visibility= View.VISIBLE
             mTotalAmount=mSubTotal+100.0
            binding.tvCheckoutTotalAmount.text="Rs. ${mTotalAmount}"


        }
        else
        {
            binding.llCheckoutPlaceOrder.visibility=View.GONE
        }
    }
    private fun placeAnOrder()
    {
        showProgressDialog()
        mOrderDetails= Order(
            FirestoreClass().getCurrentUserID(),
            CartItemList,
            addressDetails!!,
            "My order ${System.currentTimeMillis()}",
            CartItemList[0].image,
            mSubTotal.toString(),
            "100.0",
            mTotalAmount.toString(),
            System.currentTimeMillis(),




        )
        FirestoreClass().addOrder(this,mOrderDetails)

    }
    fun orderUploadSuccess()
    {
        val notification= NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("Order Placed!!")
            .setContentText("Congratulations!! Your order was successfully placed.")
            .setSmallIcon(R.drawable.ic_baseline_shopping_cart_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        val notificationManager= NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID,notification)
        FirestoreClass().updateAllDetails(this,CartItemList ,mOrderDetails)

    }
    fun allDetailsUpdateSuccessfully()
    {
        hideProgressBar()
        Toast.makeText(this,"Order Placed SuccessFully!",Toast.LENGTH_LONG).show()
        val intent= Intent(this,DashBoardActivity::class.java)
        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()

    }
    fun createNotificationChannel()
    {
        val channel = NotificationChannel(CHANNEL_ID,CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT).apply {
            enableLights(true)
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

}