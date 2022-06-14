package com.example.shopandsell.UI.A

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.Cart_Item
import com.example.shopandsell.Models.Product
import com.example.shopandsell.R
import com.example.shopandsell.dao.cart_item_dao
import com.example.shopandsell.database.Database
import com.example.shopandsell.databinding.ActivityViewDashBoardItemBinding
import com.example.shopandsell.databinding.ImageDialogBinding
import com.example.shopandsell.utli.Constants
import com.example.shopandsell.utli.GlideLoadImage
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class ViewDashBoardItem : BaseActivity(){
    private lateinit var binding: ActivityViewDashBoardItemBinding
    private lateinit var productDetails: Product
    private lateinit var dao: cart_item_dao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewDashBoardItemBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        dao= Database.initDatabase(this).cartDao
        setSupportActionBar(binding.DashBoardItemToolbar)
        val actionBar=supportActionBar
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)


        }
        binding.DashBoardItemToolbar.setNavigationOnClickListener { onBackPressed() }
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_DETAILS))
        {
            productDetails=intent.getParcelableExtra(Constants.EXTRA_PRODUCT_DETAILS)!!
            getProductDetailsinProfile(productDetails.product_id)

        }
        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }
        binding.btnGoToCart.setOnClickListener {
            startActivity(Intent(this,CartActivity::class.java))

        }
        binding.DashBoardItemProductImage.setOnClickListener{

            val zoomPinchDialogBinding = ImageDialogBinding.inflate(
                LayoutInflater.from(this)
            )
            val mBuilder = AlertDialog.Builder(this)
                .setView(zoomPinchDialogBinding.root)
            val mAlertDialog = mBuilder.show()
            mAlertDialog.setCanceledOnTouchOutside(true)
            mAlertDialog.window?.setLayout(1024,1024)
            Glide.with(this)
                .load(productDetails.image)
                .into(zoomPinchDialogBinding.pinchToZoomImageView)
        }
    }
    fun presentInCart()
    {
        hideProgressBar()
        binding.btnAddToCart.visibility=View.GONE
        binding.btnGoToCart.visibility=View.VISIBLE
    }
    fun getProductDetailsinProfile(productId:String)
    {
        showProgressDialog()
        FirestoreClass().getProductDetails(this,productId)
    }
    fun onProductReceived(product:Product)
    {
        //hideProgressBar()

        GlideLoadImage(this).loadUserPicture(product.image,binding.DashBoardItemProductImage)
        binding.ViewItemProductTitle.setText(product.title)
        binding.ViewItemProductDesciption.setText(product.description)
        binding.ViewItemProductPrice.setText("Price : Rs. "+product.price)
        binding.ViewItemProductQuantity.setText("Stock Available : "+product.stock_quantity)
        binding.ViewItemProductCategory.text="Category : "+product.category
        if(product.stock_quantity.toInt()==0)
        {
            hideProgressBar()
            binding.btnAddToCart.visibility=View.GONE
            binding.ViewItemProductQuantity.text="Out Of Stock"
            binding.ViewItemProductQuantity.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.Red
                )
            )
        }
        else
        {
            FirestoreClass().checkIfItemExistsInCart(this,productDetails.product_id)
        }



    }
      fun addToCart()
    {
        val cardItem=Cart_Item(
            FirestoreClass().getCurrentUserID(),
            productDetails.user_id,
            productDetails.product_id,
            productDetails.title,
            productDetails.price,
            productDetails.image,
            Constants.DEFAULT_CART_QUANTITY,
            category = productDetails.category
        )
        showProgressDialog()
        FirestoreClass().addCartItems(this,cardItem )
        FirestoreClass().getCartItemList(this)


    }
    fun addToCartSuccess()
    {
        hideProgressBar()
        Toast.makeText(this,"Product Added To Cart",Toast.LENGTH_LONG).show()
        binding.btnAddToCart.visibility=View.GONE
        binding.btnGoToCart.visibility=View.VISIBLE
    }

    fun afterGettingCartList(arrList:ArrayList<Cart_Item>)
    {
        runBlocking {
            val job = this.async {
               dao.deleteAll()
                for (i in arrList)
                    dao.insertIntoCart(i)
            }
            job.await()
        }
    }
}