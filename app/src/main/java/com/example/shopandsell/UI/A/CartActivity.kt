package com.example.shopandsell.UI.A

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopandsell.Adapter.CartItemAdapter
import com.example.shopandsell.Adapter.DashboardProductAdapter
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.Cart_Item
import com.example.shopandsell.Models.Product
import com.example.shopandsell.R
import com.example.shopandsell.databinding.ActivityCartBinding
import com.example.shopandsell.databinding.ActivityViewDashBoardItemBinding
import com.example.shopandsell.utli.Constants

class  CartActivity : BaseActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var mProductList:ArrayList<Product>
    private lateinit var mCartItemList:ArrayList<Cart_Item>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.CartToolBar)
        val actionBar=supportActionBar
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)


        }
        binding.CartToolBar.setNavigationOnClickListener { onBackPressed() }
        binding.btnCheckOut.setOnClickListener {
            val intent= Intent(this,AddressActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS,true)
            startActivity(intent)
        }
    }
    fun successProductListFromFireStore(productList:ArrayList<Product>)
    {
        hideProgressBar()
        mProductList=productList
        getCartItemList()

    }
    private fun getProductList()
    {
        showProgressDialog()
        FirestoreClass().getAllProductList(this)
    }

    fun getCartItemList()
    {
        //showProgressDialog()
        FirestoreClass().getCartItemList(this)
    }
    fun itemUpdatedSuccess()
    {
        hideProgressBar()
        getCartItemList()

    }
   fun onSuccessfullyGettingTheCartItemList(itemlist:ArrayList<Cart_Item>)
   {
       hideProgressBar()
       for(product in mProductList)
       {
           for(cart in itemlist)
           {
               if(product.product_id==cart.product_id)
               {
                   cart.stock_quantity=product.stock_quantity
                   if(product.stock_quantity.toInt()==0)
                       cart.stock_quantity=product.stock_quantity
               }
           }
       }
       mCartItemList=itemlist
       if(mCartItemList.size>0)
       {
           binding.txtNoItemFound.visibility= View.GONE
           binding.CartRecyclerView.visibility= View.VISIBLE
           binding.CartRecyclerView.layoutManager= LinearLayoutManager(this)
           binding.CartRecyclerView.setHasFixedSize(true)
           val adapter= CartItemAdapter(this,mCartItemList,true)
           binding.CartRecyclerView.adapter=adapter
           var subTotal=0.0
           for(i in mCartItemList)
           {
               val availableQuantity=i.stock_quantity.toInt()
               if(availableQuantity>0)
               {
                   val price=i.price.toDouble()
                   val cartQuantity=i.cart_quantity.toInt()
                   subTotal+=(price*cartQuantity)
               }

           }
           binding.txtItemCost.text="Rs. "+subTotal.toString()
           binding.txtShippingCost.text="Rs. 100.00"
           if(subTotal>0)
           {
               subTotal+=100.00;
               binding.txtTotalCost.text="Rs. "+subTotal.toString()
           }
           else
           {
               binding.btnCheckOut.visibility=View.GONE
               binding.llCostDetails.visibility=View.GONE
           }



       }
       else
       {
           binding.btnCheckOut.visibility=View.GONE
           binding.txtNoItemFound.visibility= View.VISIBLE
           binding.CartRecyclerView.visibility= View.GONE
           binding.llCostDetails.visibility=View.GONE
       }
   }

    override fun onResume() {
        super.onResume()
        //  getCartItemList()
        getProductList()
    }
    fun itemRemovedSuccess()
    {
        hideProgressBar()
        Toast.makeText(this,"Item Removed SuccessFully",Toast.LENGTH_LONG).show()
        getCartItemList()
    }
}