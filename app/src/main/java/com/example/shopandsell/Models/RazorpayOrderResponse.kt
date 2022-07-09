package `in`.indiahaat.beans.razorpay

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class RazorpayOrderResponse(
    var amount: Int = 0,
    var currency: String = "INR",
    var entity: String = "",
    var id: String = "",
    var status: String = "",
    var created_at: String = ""
)