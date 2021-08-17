package com.example.shopandsell.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.shopandsell.Models.Product
import com.example.shopandsell.R
import com.example.shopandsell.UI.A.ProductProfileActivity
import com.example.shopandsell.UI.Fragments.ProductsFragment
import com.example.shopandsell.utli.Constants
import com.example.shopandsell.utli.GlideLoadImage

class ProductAdapter(var context: Context,var productList:ArrayList<Product>,var fragment:ProductsFragment ):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
         return ProductViewHolder(
             LayoutInflater.from(context).inflate(R.layout.product_item,parent,false)
         )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val product=productList[position]
        if(holder is ProductViewHolder)
        {
            GlideLoadImage(context).loadProductPicture(product.image,holder.productImage)
            holder.productName.text=product.title
            holder.productPrice.text="Rs."+product.price
            holder.itemproductlayout.setOnClickListener {
                val intent= Intent(context,ProductProfileActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_DETAILS,product)
                context.startActivity(intent)
            }
            holder.btnDelete.setOnClickListener {
                    fragment.deleteProduct(product.product_id)
            }

        }

    }

    override fun getItemCount(): Int {
        return productList.size
    }
}

class ProductViewHolder(view: View):RecyclerView.ViewHolder(view){
    var productImage: ImageView =view.findViewById(R.id.productitemimage)
    var productName: TextView =view.findViewById(R.id.productitemtitle)
    var productPrice: TextView =view.findViewById(R.id.productitemprice)
    var itemproductlayout:ConstraintLayout=view.findViewById(R.id.itemProductLayout)
    var btnDelete:ImageView=view.findViewById(R.id.btnDeleteProduct)

}