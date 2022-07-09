package com.example.shopandsell.UI.A

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.shopandsell.Models.SoldProducts
import com.example.shopandsell.databinding.ActivitySoldProductDetailsBinding
import com.example.shopandsell.utli.Constants
import com.example.shopandsell.utli.GlideLoadImage
import java.text.SimpleDateFormat
import java.util.*

class SoldProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySoldProductDetailsBinding
    private var soldProductDetails:SoldProducts?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoldProductDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbarSoldProductDetailsActivity)
        val actionBar=supportActionBar
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        if (intent.hasExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS))
        {
            soldProductDetails=intent.getParcelableExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)!!


        }
        binding.toolbarSoldProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }
        setupUI(soldProductDetails!!)

    }
    private fun setupUI(productDetails: SoldProducts) {

        binding.tvOrderDetailsId.text = productDetails.order_id

        // Date Format in which the date will be displayed in the UI.
        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = productDetails.order_date
        binding.tvOrderDetailsDate.text = formatter.format(calendar.time)

        GlideLoadImage(this@SoldProductDetailsActivity).loadProductPicture(
            productDetails.image,
            binding.ivProductItemImage
        )
        binding.tvProductItemName.text = productDetails.title
        binding.tvProductItemPrice.text ="Rs. ${productDetails.price}"
        binding.tvSoldProductQuantity.text = productDetails.sold_quantity

        binding.tvSoldDetailsAddressType.text = productDetails.address.type
        binding.tvSoldDetailsFullName.text = productDetails.address.name
        binding.tvSoldDetailsAddress.text =
            "${productDetails.address.address}, ${productDetails.address.zipCode}"
        binding.tvSoldDetailsAdditionalNote.text = productDetails.address.additionalNote


        binding.tvSoldDetailsMobileNumber.text = productDetails.address.mobileNumber

        binding.tvSoldProductSubTotal.text = "Rs. "+productDetails.sub_total_amount
        binding.tvSoldProductShippingCharge.text =  "Rs. "+productDetails.shipping_charge
        binding.tvSoldProductTotalAmount.text =  "Rs. "+productDetails.total_amount
    }
}