package com.example.shopandsell.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.Cart_Item
import com.example.shopandsell.Models.Product
import com.example.shopandsell.R
import com.example.shopandsell.UI.A.CartActivity
import com.example.shopandsell.UI.A.ProductProfileActivity
import com.example.shopandsell.UI.Fragments.ProductsFragment
import com.example.shopandsell.utli.Constants
import com.example.shopandsell.utli.GlideLoadImage

class CartItemAdapter(val context: Context, var itemList:ArrayList<Cart_Item>,val updateCartItem:Boolean):RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CartProductViewHolder(
            LayoutInflater.from(context).inflate(R.layout.cartitem,parent,false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item=itemList[position]
        if(holder is CartProductViewHolder)
        {
            GlideLoadImage(context).loadProductPicture(item.image,holder.CartProductImage)
            holder.CartProductName.text=item.title
            holder.CartProductPrice.text="Rs."+item.price
            holder.CartQuantity.text=item.cart_quantity
            if(item.stock_quantity=="0")
            {
                holder.decrease.visibility=View.GONE
                holder.increase.visibility=View.GONE

                if(updateCartItem)
                {
                    holder.btnDelete.visibility=View.VISIBLE
                }
                else
                {
                    holder.btnDelete.visibility=View.GONE
                }

                holder.CartQuantity.text="Out Of Stock"
                holder.CartQuantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.Red
                    )
                )
            }
            else
            {
                if(updateCartItem)
                {
                    holder.decrease.visibility=View.VISIBLE
                    holder.increase.visibility=View.VISIBLE
                    holder.btnDelete.visibility=View.VISIBLE
                }
                else
                {
                    holder.decrease.visibility=View.GONE
                    holder.increase.visibility=View.GONE
                    holder.btnDelete.visibility=View.GONE
                }

            }
            holder.btnDelete.setOnClickListener {
                when(context)
                {
                    is CartActivity->{
                        context.showProgressDialog()

                    }
                }
                FirestoreClass().deleteCartItem(context,item.id)
            }
            holder.increase.setOnClickListener {
                if(item.cart_quantity.toInt() < item.stock_quantity.toInt())
                {
                    val cartQuantity:Int=item.cart_quantity.toInt()
                    val itemHashMap=HashMap<String,Any>()
                    itemHashMap[Constants.CART_QUANTITY]=(cartQuantity+1).toString()
                    if(context is CartActivity)
                    {
                        context.showProgressDialog()
                    }
                    FirestoreClass().updateMyCart(context,item.id,itemHashMap)
                }
                else
                {
                    if(context is CartActivity)
                    {
                        context.showErrorSnackBar("All the Available Items Added To Cart!",true)
                    }
                }
            }
            holder.decrease.setOnClickListener {
                    if(item.cart_quantity=="1")
                    {
                        FirestoreClass().deleteCartItem(context,item.id)
                    }
                else
                    {
                        val cartQuantity:Int=item.cart_quantity.toInt()
                        val itemHashMap=HashMap<String,Any>()
                        itemHashMap[Constants.CART_QUANTITY]=(cartQuantity-1).toString()
                        if(context is CartActivity)
                        {
                            context.showProgressDialog()
                        }
                        FirestoreClass().updateMyCart(context,item.id,itemHashMap)
                    }
            }

        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}

class CartProductViewHolder(view: View): RecyclerView.ViewHolder(view){
    var CartProductImage: ImageView =view.findViewById(R.id.productcartitemimage)
    var CartProductName: TextView =view.findViewById(R.id.productcartitemtitle)
    var CartProductPrice: TextView =view.findViewById(R.id.productCartItemCost)
    var CartQuantity:TextView=view.findViewById(R.id.productcartItemQuantity)
    val increase:ImageView=view.findViewById(R.id.btnIncreaseproduct)
    val decrease:ImageView=view.findViewById(R.id.btnRemoveProduct)
    var btnDelete:ImageView=view.findViewById(R.id.btnDeleteCartProduct)

}