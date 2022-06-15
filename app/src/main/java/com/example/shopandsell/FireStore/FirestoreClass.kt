package com.example.shopandsell.FireStore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.shopandsell.Models.*
import com.example.shopandsell.UI.A.*
import com.example.shopandsell.UI.Fragments.DashBoardFragment
import com.example.shopandsell.UI.Fragments.OrdersFragment
import com.example.shopandsell.UI.Fragments.ProductsFragment
import com.example.shopandsell.UI.Fragments.SoldProductsFragment
import com.example.shopandsell.database.Database
import com.example.shopandsell.utli.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class FirestoreClass {

    private val mFirestore=FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity,userInfo: User)
    {
        mFirestore.collection(Constants.USERS)
            .document(userInfo.id)  //document id= user id
            .set(userInfo, SetOptions.merge())  //setdata and merge data,we can merge data later on
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressBar()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering",
                    e
                )
            }
    }
    fun addProduct(activity: AddProducts,productInfo: Product)
    {
        mFirestore.collection(Constants.PRODUCTS)
            .document()  //document id= user id
            .set(productInfo, SetOptions.merge())  //setdata and merge data,we can merge data later on
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressBar()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding product",
                    e
                )
            }
    }
    fun addAddress(activity: AddEditAddressActivity,addressInfo: Address)
    {
        mFirestore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())  //setdata and merge data,we can merge data later on
            .addOnSuccessListener {
                activity.addressUploadSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressBar()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding product",
                    e
                )
            }
    }
    fun getCurrentUserID():String{
        val currentUser=FirebaseAuth.getInstance().currentUser
        var currentUserID=""
        if(currentUser!=null)
        {
            currentUserID=currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity)
    {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
                Log.i(activity.javaClass.simpleName,document.toString())
                val user=document.toObject(User::class.java)!!

                val sharedPreferences=activity.getSharedPreferences(
                            Constants.SHOPANDSELL_PREFERENCES,
                            Context.MODE_PRIVATE  //to restrict access outside of application
                )

                val editor:SharedPreferences.Editor=sharedPreferences.edit()

                //key is the username
                //value is first name and second name
                editor.putString(
                    Constants.LOGGED_IN_UserNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()




                when(activity)
                {
                    is LoginActivity->
                    {
                        activity.userLoogedInSuccess(user)
                    }
                    is SettingsActivity->
                    {
                        activity.onUserReceived(user)
                    }
                }
            }
            .addOnFailureListener {
                when(activity)
                {
                    is LoginActivity->
                    {
                        activity.hideProgressBar()
                    }
                    is SettingsActivity->
                    {
                        activity.hideProgressBar()
                    }
                }
            }
    }
    fun getProductDetails(activity: Activity,productId: String)
    {
        mFirestore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document->
                Log.i(activity.javaClass.simpleName,document.toString())
                val product=document.toObject(Product::class.java)!!
                when(activity)
                {

                    is ProductProfileActivity->
                    {
                        activity.onProductReceived(product)
                    }
                    is ViewDashBoardItem->
                    {
                        activity.onProductReceived(product)
                    }
                }
            }
            .addOnFailureListener {
                when(activity)
                {
                    is ProductProfileActivity->
                    {
                        activity.hideProgressBar()
                    }
                    is ViewDashBoardItem->
                    {
                        activity.hideProgressBar()
                    }

                }
            }
    }
    fun addCartItems(activity:ViewDashBoardItem,addToCart:Cart_Item)
    {
        mFirestore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())  //updating not replacing
            .addOnSuccessListener {

                activity.addToCartSuccess()

            }
            .addOnFailureListener {
                e->
                activity.hideProgressBar()
            }
    }
    fun updateUserProfileData(activity:Activity,userHashMap: HashMap<String,Any>)
    {
        mFirestore.collection(Constants.USERS).document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {e->

                when(activity)
                {
                    is UserProflieActivity->
                    {
                        activity.userProfileUpdateSuccess()

                    }
                }


            }
            .addOnFailureListener {e->

                when(activity)
                {
                    is UserProflieActivity->
                    {
                        activity.hideProgressBar()

                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details",e
                        )

            }
    }
    fun updateProductProfileData(activity:Activity,productHashMap: HashMap<String,Any>,productId:String)
    {
        mFirestore.collection(Constants.PRODUCTS).document(productId)
            .update(productHashMap)
            .addOnSuccessListener {e->

                when(activity)
                {
                    is ProductProfileActivity->
                    {
                        activity.productProfileUpdateSuccess()

                    }
                }


            }
            .addOnFailureListener {e->

                when(activity)
                {
                    is ProductProfileActivity->
                    {
                        activity.hideProgressBar()

                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the product details",e
                )

            }
    }
    fun updateAddress(activity: AddEditAddressActivity,addressInfo: Address,addressId:String)
    {
        mFirestore.collection(Constants.ADDRESSES)
            .document(addressId)
            .set(addressInfo, SetOptions.merge())  //setdata and merge data,we can merge data later on
            .addOnSuccessListener {
                activity.addressUploadSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressBar()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding product",
                    e
                )
            }
    }

    fun uploadImageToCloudStorage(activity:Activity,imageFileURI: Uri?,imageType:String)
    {
        var fileNameOnCloud:StorageReference?=null
        //fancy way to give our file a name in the cloud storage
        if(imageType=="user")
        {
            fileNameOnCloud=FirebaseStorage.getInstance().reference.child(
                Constants.USER_PROFILE_IMAGE+System.currentTimeMillis()+","
                        + Constants.getFileExtension(
                    activity,
                    imageFileURI
                )
            )
        }
        else
        {
            fileNameOnCloud=FirebaseStorage.getInstance().reference.child(
                Constants.PRODUCT_IMAGE+System.currentTimeMillis()+","
                        + Constants.getFileExtension(
                    activity,
                    imageFileURI
                )
            )
        }

        fileNameOnCloud.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot->
            //to check successfull image storage
            Log.e(
                "FireBase ImageURI",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )

            //get downloadable link
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri->
                    Log.e("Dowloadable Image URI",uri.toString())
                    when(activity)
                    {
                        is UserProflieActivity->{
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddProducts->{
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is ProductProfileActivity->
                        {
                            activity.imageUploadSuccess(uri.toString())
                        }

                    }

                }

        }
            .addOnFailureListener{exception->
                when(activity)
                {
                    is UserProflieActivity->
                    {
                        activity.hideProgressBar()
                    }
                    is AddProducts->
                    {
                        activity.hideProgressBar()
                    }
                    is ProductProfileActivity->
                    {
                        activity.hideProgressBar()
                    }
                }

            }

    }
    fun getCartItemList(activity: Activity)
    {

        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())//allows us to get product of the current user
            .get()
            .addOnSuccessListener { document->
                var cartItemsList:ArrayList<Cart_Item> = ArrayList()
                for(i in document.documents)
                {
                    var item=i.toObject(Cart_Item::class.java)
                    item!!.id = i.id
                    cartItemsList.add(item)
                }
                when(activity) {
                    is CartActivity -> {
                        activity.onSuccessfullyGettingTheCartItemList(cartItemsList)
                    }
                    is MakePayment ->
                    {
                        activity.successCartItemsList(cartItemsList)
                    }
                    is CheckoutActivity->
                    {
                        activity.successCartItemsList(cartItemsList)
                    }
                    is ViewDashBoardItem->{
                        activity.afterGettingCartList(cartItemsList)
                    }
                }
            }
            .addOnFailureListener {
                when(activity)
                {
                    is CartActivity->
                    {
                        activity.hideProgressBar()
                    }
                    is CheckoutActivity->
                    {
                        activity.hideProgressBar()
                    }
                }
            }



    }

    fun getAddressItemList(activity: Activity)
    {

        mFirestore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())//allows us to get product of the current user
            .get()
            .addOnSuccessListener { document->
                val addressItemsList:ArrayList<Address> = ArrayList()
                for(i in document.documents)
                {
                    var item=i.toObject(Address::class.java)
                    item!!.id = i.id
                    addressItemsList.add(item)
                }
                when(activity)
                {
                    is AddressActivity->
                    {
                        activity.onSuccessfullyGettingTheAddressItemList(addressItemsList)
                    }
                }
            }
            .addOnFailureListener {
                when(activity)
                {
                    is AddEditAddressActivity->
                    {
                        activity.hideProgressBar()
                    }
                }
            }



    }






    fun getAllProductList(activity: Activity)
    {
        mFirestore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener {document->
                val productslist:ArrayList<Product> = ArrayList()
                for(i in document.documents)
                {
                    val product=i.toObject(Product::class.java)
                    product!!.product_id=i.id
                    productslist.add(product)
                }
                    when(activity)
                    {
                        is CartActivity->{
                            activity.successProductListFromFireStore(productslist)
                        }
                        is MakePayment->
                        {
                            activity.successProductsListFromFireStore(productslist)
                        }
                        is CheckoutActivity->
                        {
                            activity.successProductsListFromFireStore(productslist)
                        }
                    }


            }
            .addOnFailureListener {
                when(activity)
                {
                    is CartActivity->{
                        activity.hideProgressBar()
                    }
                    is CheckoutActivity->
                    {
                        activity.hideProgressBar()
                    }
                }
            }
    }
    fun getProductsList(fragment: Fragment)
    {

            mFirestore.collection(Constants.PRODUCTS)
                .whereEqualTo(Constants.USER_ID,getCurrentUserID())//allows us to get product of the current user
                .get()
                .addOnSuccessListener { document->
                    Log.e("ProductList",document.documents.toString())
                   var productList:ArrayList<Product> = ArrayList()
                    for(i in document.documents)
                    {
                        var product=i.toObject(Product::class.java)
                        product!!.product_id = i.id
                        productList.add(product)
                    }
                    when(fragment)
                    {
                        is ProductsFragment->
                        {
                                fragment.onSuccessfullyGettingTheProductList(productList)
                        }
                    }
                }



    }
    fun getDashBoardProductsList(fragment: DashBoardFragment) {

        mFirestore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e("ProductList", document.documents.toString())
                var DashBoardProductList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    var product = i.toObject(Product::class.java)
                    if(product!!.user_id==getCurrentUserID())
                    {
                        continue
                    }
                    product!!.product_id = i.id
                    DashBoardProductList.add(product)
                }

                        fragment.onSuccessfullyGettingTheDashBoardProductList(DashBoardProductList)

                }

            .addOnFailureListener {e->
                        fragment.hideProgressBar()

                }

            }
    fun deleteCartItem(context: Context,cartItemId:String)
    {
        mFirestore.collection(Constants.CART_ITEMS)
            .document(cartItemId)
            .delete()
            .addOnSuccessListener {
                when(context)
                {
                    is CartActivity ->
                    {
                        runBlocking {
                            val job = this.async {
                                Database.initDatabase(context).cartDao.deleteFromCart(cartItemId)
                            }
                            job.await()
                        }
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener {e->
                when(context)
                {
                    is CartActivity ->
                    {
                        context.hideProgressBar()
                    }
                }

            }
    }
    fun deleteAddressItem(context: Context,addressItemId:String)
    {
        mFirestore.collection(Constants.ADDRESSES)
            .document(addressItemId)
            .delete()
            .addOnSuccessListener {
                when(context)
                {
                    is AddressActivity ->
                    {
                        context.addressItemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener {e->
                when(context)
                {
                    is AddressActivity ->
                    {
                        context.hideProgressBar()
                    }
                }

            }
    }
    fun updateMyCart(context: Context,cartId:String,itemHashMap: HashMap<String,Any>)
    {
        mFirestore.collection(Constants.CART_ITEMS).document(cartId)
            .update(itemHashMap)
            .addOnSuccessListener {
                when(context)
                {
                    is CartActivity ->
                    {
                        context.itemUpdatedSuccess()
                    }
                }
            }
            .addOnFailureListener {
                when(context)
                {
                    is CartActivity ->
                    {
                        context.hideProgressBar()
                    }
                }
            }
    }
    fun deleteProduct(fragment: ProductsFragment,productId:String)
    {
        mFirestore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                    fragment.productDeletedSuccessfully()
            }
            .addOnFailureListener {e->
                fragment.hideProgressBar()

            }
    }
    fun checkIfItemExistsInCart(activity: ViewDashBoardItem,productId: String)
    {
        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID,productId)
            .get()
            .addOnSuccessListener { document->
                if(document.documents.size>0)
                {

                    activity.presentInCart()
                }
                else
                    activity.hideProgressBar()
            }
            .addOnFailureListener {
                activity.hideProgressBar()
            }
    }

    fun addOrder(activity: MakePayment,orderInfo:Order)
    {
        mFirestore.collection(Constants.ORDERS)
            .document()
            .set(orderInfo, SetOptions.merge())  //setdata and merge data,we can merge data later on
            .addOnSuccessListener {
                runBlocking {
                    val job = this.async {
                        Database.initDatabase(activity).cartDao.deleteAll()
                    }
                    job.await()
                }
                activity.orderUploadSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressBar()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding product",
                    e
                )
            }
    }
    fun updateAllDetails(activity:MakePayment,cartList:ArrayList<Cart_Item>,order:Order)
    {
            val writeBatch = mFirestore.batch()
        for(cart in cartList)
        {
//            val productHashMap= HashMap<String,Any>()
//            productHashMap[Constants.STOCK_QUANTITY] =
//                (
//                        cartItem.stock_quantity.toInt()-cartItem.cart_quantity.toInt()
//                        ).toString()
            val soldProduct = SoldProducts(
                // Here the user id will be of product owner.
                cart.product_owner_id,
                cart.title,
                cart.price,
                cart.cart_quantity,
                cart.image,
                order.title,
                order.order_datetime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address,
                category = cart.category
            )
            val documentRefence = mFirestore.collection(Constants.SOLD_PRODUCTS)
                .document(cart.product_id)
            writeBatch.set(documentRefence,soldProduct)

        }
        for (cart in cartList) {

            val productHashMap = HashMap<String, Any>()

            productHashMap[Constants.STOCK_QUANTITY] =
                (cart.stock_quantity.toInt() - cart.cart_quantity.toInt()).toString()

            val documentReference = mFirestore.collection(Constants.PRODUCTS)
                .document(cart.product_id)

            writeBatch.update(documentReference, productHashMap)
        }
        for( cartItem in cartList)
        {
            val documentReference= mFirestore.collection(Constants.CART_ITEMS)
                .document(cartItem.id)
            writeBatch.delete(documentReference)
        }
        writeBatch.commit()
            .addOnSuccessListener {
                    activity.allDetailsUpdateSuccessfully()
            }
            .addOnFailureListener {
                activity.hideProgressBar()
            }

    }
    fun getOrdersList(fragment: Fragment)
    {

        mFirestore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())//allows us to get product of the current user
            .get()
            .addOnSuccessListener { document->

                var orderList:ArrayList<Order> = ArrayList()
                for(i in document.documents)
                {
                    var order=i.toObject(Order::class.java)
                    order!!.id = i.id
                    orderList.add(order)
                }
                when(fragment)
                {
                    is OrdersFragment->
                    {
                        fragment.onSuccessfullyGettingTheOrderList(orderList)
                    }
                }
            }
            .addOnFailureListener {
                when(fragment)
                {
                    is OrdersFragment->
                    {
                        fragment.hideProgressBar()
                    }
                }
            }



    }
    fun getSoldProductList(fragment: SoldProductsFragment)
    {

        mFirestore.collection(Constants.SOLD_PRODUCTS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())//allows us to get product of the current user
            .get()
            .addOnSuccessListener { document->

                var soldItemList:ArrayList<SoldProducts> = ArrayList()
                for(i in document.documents)
                {
                    var soldItem=i.toObject(SoldProducts::class.java)
                    soldItem!!.id = i.id
                    soldItemList.add(soldItem)
                }

                        fragment.onSuccessfullyGettingTheSoldProductList(soldItemList)


            }
            .addOnFailureListener {

                        fragment.hideProgressBar()

                }




    }
}