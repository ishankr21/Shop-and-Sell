package com.example.shopandsell.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.shopandsell.Models.Order
import com.example.shopandsell.Models.Product
import com.example.shopandsell.R
import com.example.shopandsell.UI.A.OrderDetailActivity
import com.example.shopandsell.UI.A.ProductProfileActivity
import com.example.shopandsell.UI.A.ViewDashBoardItem
import com.example.shopandsell.utli.Constants
import com.example.shopandsell.utli.GlideLoadImage

class OrdersAdapter(var context: Context,var ordersList:ArrayList<Order>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return OrderViewHolder(
            LayoutInflater.from(context).inflate(R.layout.orders_item,parent,false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val order=ordersList[position]
        if(holder is OrderViewHolder)
        {

            holder.dashboardproductName.text="Order ID : "+order.order_datetime.toString()
            holder.dashboardproductPrice.text="Rs."+order.total_amount
            var no=0
            for(i in order.item)
                no+=i.cart_quantity.toInt()
            holder.number.text= "No of Products : $no"
            holder.layout.setOnClickListener {
                val intent= Intent(context, OrderDetailActivity::class.java)
                intent.putExtra(Constants.EXTRA_ORDER_DETAILS,order)
                context.startActivity(intent)
            }

        }

    }

    override fun getItemCount(): Int {
        return ordersList.size
    }
}

class OrderViewHolder(view: View):RecyclerView.ViewHolder(view){

    var dashboardproductName: TextView =view.findViewById(R.id.dashboardproductitemtitle)
    var dashboardproductPrice: TextView =view.findViewById(R.id.orderTotalCostOfProducts)
    var number=view.findViewById<TextView>(R.id.orderTotalNumberOfProducts)
    val layout:ConstraintLayout=view.findViewById(R.id.layoutitemdashboardproduct)

}