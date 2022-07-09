package `in`.indiahaat.beans.razorpay

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class RazorpayOrderRequest(
    var amount: Int = 0,
    var currency: String = "INR"
)