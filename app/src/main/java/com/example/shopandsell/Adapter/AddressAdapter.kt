package com.example.shopandsell.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.Address
import com.example.shopandsell.Models.Cart_Item
import com.example.shopandsell.R
import com.example.shopandsell.UI.A.AddEditAddressActivity
import com.example.shopandsell.UI.A.CheckoutActivity
import com.example.shopandsell.utli.Constants
import org.w3c.dom.Text

class AddressAdapter(private val context: Context, var itemList:ArrayList<Address>,var selectedAddress:Boolean):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AddressViewHolder(
            LayoutInflater.from(context).inflate(R.layout.adrdress_item,parent,false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item=itemList[position]
        if(holder is AddressViewHolder)
        {
            holder.address.text=item.address
            holder.fullName.text=item.name
            holder.phoneNumber.text=item.mobileNumber
            if(item.type==Constants.HOME)
            {
                holder.addressType.text=Constants.HOME
            }
            else if(item.type==Constants.OFFICE)
            {
                holder.addressType.text=Constants.OFFICE
            }
            else
            {
                holder.addressType.text=Constants.OTHER
            }
            if(selectedAddress)
            {
                holder.lladdressItem.setOnClickListener {
                    val intent=Intent(context,CheckoutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS,itemList[position])
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    fun notifyEditItem(activity: Activity,position: Int)
    {
        val intent= Intent(context,AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS,itemList[position])
        activity.startActivity(intent)
        notifyItemChanged(position)
    }

}
class AddressViewHolder(view: View): RecyclerView.ViewHolder(view){
     val fullName:TextView=view.findViewById(R.id.addressItemName)
     val address:TextView=view.findViewById(R.id.addressItemAddress)
     val phoneNumber:TextView=view.findViewById(R.id.addressItemPhoneNumber)
     val addressType:TextView=view.findViewById(R.id.addressItemAddressType)
     val lladdressItem:LinearLayout=view.findViewById(R.id.llAddressItem)

}