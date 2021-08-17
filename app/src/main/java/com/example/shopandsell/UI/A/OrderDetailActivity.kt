package com.example.shopandsell.UI.A

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopandsell.Adapter.CartItemAdapter
import com.example.shopandsell.Models.Order
import com.example.shopandsell.R
import com.example.shopandsell.databinding.ActivityAddEditAddressBinding
import com.example.shopandsell.databinding.ActivityOrderDetailBinding
import com.example.shopandsell.utli.Constants
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class OrderDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityOrderDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbarMyOrderDetailsActivity)
        val actionBar=supportActionBar
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        binding.toolbarMyOrderDetailsActivity.setNavigationOnClickListener { onBackPressed() }
        var orderDetails=Order()
        if(intent.hasExtra(Constants.EXTRA_ORDER_DETAILS))
        {
            orderDetails=intent.getParcelableExtra(Constants.EXTRA_ORDER_DETAILS)!!
        }
        setupUI(orderDetails)
    }
    private fun setupUI(order:Order)
    {
        binding.tvOrderDetailsId.text=order.title
        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter= SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar:Calendar = Calendar.getInstance()
        calendar.timeInMillis=order.order_datetime
        val orderDateTime=formatter.format(calendar.time)
        binding.tvOrderDetailsDate.text=orderDateTime
        val diffInMilliSeconds:Long=System.currentTimeMillis()-order.order_datetime
        val diffInHours:Long= TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)

        when{
            diffInHours<1 ->
            {
                binding.tvOrderStatus.text="Pending"
            }
            diffInHours < 2 ->
            {
                binding.tvOrderStatus.text="In Transit"
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.Yellow
                    )
                )
            }
            else ->
            {
                binding.tvOrderStatus.text="Delivered"
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.Green
                    )
                )
            }

        }
        binding.rvMyOrderItemsList.layoutManager = LinearLayoutManager(this)
        binding.rvMyOrderItemsList.setHasFixedSize(true)

        val cartListAdapter= CartItemAdapter(this,order.item,false)
        binding.rvMyOrderItemsList.adapter=cartListAdapter
        binding.tvMyOrderDetailsAddressType.text=order.address.type
        binding.tvMyOrderDetailsFullName.text=order.address.name
        binding.tvMyOrderDetailsAddress.text=order.address.address
        binding.tvMyOrderDetailsAdditionalNote.text=order.address.additionalNote
        binding.tvMyOrderDetailsMobileNumber.text=order.address.mobileNumber

        binding.tvOrderDetailsSubTotal.text=order.sub_total_amount
        binding.tvOrderDetailsShippingCharge.text=order.shipping_charge
        binding.tvOrderDetailsTotalAmount.text=order.total_amount


    }
}