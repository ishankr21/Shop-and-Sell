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
import com.example.shopandsell.R
import com.example.shopandsell.UI.A.ProductProfileActivity
import com.example.shopandsell.UI.A.ViewDashBoardItem
import com.example.shopandsell.utli.Constants
import com.example.shopandsell.utli.GlideLoadImage

class DashboardProductAdapter(var context: Context,var productList:ArrayList<Product>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return DashBoardProductViewHolder(
            LayoutInflater.from(context).inflate(R.layout.dashboardproductitem,parent,false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val product=productList[position]
        if(holder is DashBoardProductViewHolder)
        {
            GlideLoadImage(context).loadProductPicture(product.image,holder.dashboardproductImage)
            holder.dashboardproductName.text=product.title
            holder.dashboardproductPrice.text="Rs."+product.price
            holder.layout.setOnClickListener {
                val intent= Intent(context, ViewDashBoardItem::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_DETAILS,product)
                context.startActivity(intent)
            }

        }

    }

    override fun getItemCount(): Int {
        return productList.size
    }
}

class DashBoardProductViewHolder(view: View):RecyclerView.ViewHolder(view){
    var dashboardproductImage: ImageView =view.findViewById(R.id.dashboardproductitemimage)
    var dashboardproductName: TextView =view.findViewById(R.id.dashboardproductitemtitle)
    var dashboardproductPrice: TextView =view.findViewById(R.id.dashboardproductitemprice)
    val layout:ConstraintLayout=view.findViewById(R.id.layoutitemdashboardproduct)

}