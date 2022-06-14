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
import com.example.shopandsell.Models.Product
import com.example.shopandsell.R
import com.example.shopandsell.UI.A.ProductProfileActivity
import com.example.shopandsell.UI.A.ViewDashBoardItem
import com.example.shopandsell.UI.Fragments.DashBoardFragment
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
            holder.category.text="Category : "+product.category
            if(product.stock_quantity.toInt()>0)
            {
                holder.instock.text="In-stock"
                holder.instock.setTextColor(ContextCompat.getColor(context,R.color.Green))
            }
            else
            {
                holder.instock.text="Out of stock"
                holder.instock.setTextColor(ContextCompat.getColor(context,R.color.Red))
            }
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
    fun updateProducts(productNewArr:ArrayList<Product>)
    {
        productList.clear()
        productList.addAll(productNewArr)
        notifyDataSetChanged()
    }
}

class DashBoardProductViewHolder(view: View):RecyclerView.ViewHolder(view){
    var dashboardproductImage: ImageView =view.findViewById(R.id.dashboardproductitemimage)
    var dashboardproductName: TextView =view.findViewById(R.id.dashboardproductitemtitle)
    var dashboardproductPrice: TextView =view.findViewById(R.id.dashboardproductitemprice)
    val layout:ConstraintLayout=view.findViewById(R.id.layoutitemdashboardproduct)
    var category:TextView=view.findViewById(R.id.dashboardproductitemcategory)
    var instock:TextView=view.findViewById(R.id.dashboardproductinstock)

}