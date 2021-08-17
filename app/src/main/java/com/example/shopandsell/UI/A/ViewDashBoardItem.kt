package com.example.shopandsell.UI.A

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.Cart_Item
import com.example.shopandsell.Models.Product
import com.example.shopandsell.R
import com.example.shopandsell.databinding.ActivityProductProfileBinding
import com.example.shopandsell.databinding.ActivityViewDashBoardItemBinding
import com.example.shopandsell.utli.Constants
import com.example.shopandsell.utli.GlideLoadImage

class ViewDashBoardItem : BaseActivity(){
    private lateinit var binding: ActivityViewDashBoardItemBinding
    private lateinit var productDetails: Product
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewDashBoardItemBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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

        )
        showProgressDialog()
        FirestoreClass().addCartItems(this,cardItem )
    }
    fun addToCartSuccess()
    {
        hideProgressBar()
        Toast.makeText(this,"Product Added To Cart",Toast.LENGTH_LONG).show()
        binding.btnAddToCart.visibility=View.GONE
        binding.btnGoToCart.visibility=View.VISIBLE
    }
}