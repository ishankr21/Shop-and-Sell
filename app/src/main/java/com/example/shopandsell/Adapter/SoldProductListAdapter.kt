package com.example.shopandsell.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.shopandsell.Models.Product
import com.example.shopandsell.Models.SoldProducts
import com.example.shopandsell.R
import com.example.shopandsell.UI.A.ProductProfileActivity
import com.example.shopandsell.UI.A.SoldProductDetailsActivity
import com.example.shopandsell.UI.Fragments.ProductsFragment
import com.example.shopandsell.utli.Constants
import com.example.shopandsell.utli.GlideLoadImage

class SoldProductListAdapter(var context: Context, var soldProductsList:ArrayList<SoldProducts> ):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return newViewHolder(
            LayoutInflater.from(context).inflate(R.layout.product_item,parent,false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val soldProduct=soldProductsList[position]
        if(holder is newViewHolder)
        {
            GlideLoadImage(context).loadProductPicture(soldProduct.image,holder.productImage)
            holder.productName.text=soldProduct.title
            holder.productPrice.text="Rs."+soldProduct.price
            holder.category.text="Category : "+soldProduct.category
            holder.itemproductlayout.setOnClickListener {
                val intent= Intent(context, SoldProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS,soldProduct)
                context.startActivity(intent)
            }
            holder.btnDelete.visibility=View.GONE


        }

    }

    override fun getItemCount(): Int {
        return soldProductsList.size
    }
}

class newViewHolder(view: View): RecyclerView.ViewHolder(view) {
    var productImage: ImageView = view.findViewById(R.id.productitemimage)
    var productName: TextView = view.findViewById(R.id.productitemtitle)
    var productPrice: TextView = view.findViewById(R.id.productitemprice)
    var itemproductlayout: ConstraintLayout = view.findViewById(R.id.itemProductLayout)
    var btnDelete: ImageView = view.findViewById(R.id.btnDeleteProduct)
    var category:TextView=view.findViewById(R.id.productitemcategory)
}

