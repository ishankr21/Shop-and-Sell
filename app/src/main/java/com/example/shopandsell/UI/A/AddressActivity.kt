package com.example.shopandsell.UI.A

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.Address
import com.example.shopandsell.databinding.ActivityAddressBinding
import com.example.shopandsell.utli.Constants
import com.example.shopandsell.utli.SwipeToDelete
import com.example.shopandsell.utli.SwipeToEdit


class AddressActivity : BaseActivity(){
    private lateinit var binding: ActivityAddressBinding
    private var selectedAddress:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.AddressToolbar)
        val actionBar=supportActionBar
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        binding.AddressToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.btnAddAddress.setOnClickListener {
            startActivity(Intent(this,AddEditAddressActivity::class.java))
        }
        if(intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS))
            selectedAddress=intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS,false)

        if(selectedAddress)
        {
            binding.AddressToolbar.title="Select Address"
        }

    }
    fun onSuccessfullyGettingTheAddressItemList(AddressList:ArrayList<Address>)
    {
        hideProgressBar()
        if(AddressList.size>0)
        {
            binding.txtNoAddresses.visibility= View.GONE
            binding.AddressRecyclerView.visibility= View.VISIBLE
            binding.AddressRecyclerView.layoutManager= LinearLayoutManager(this)
            binding.AddressRecyclerView.setHasFixedSize(true)
            val adapter= com.example.shopandsell.Adapter.AddressAdapter(
                this,
                AddressList,
                selectedAddress
            )
            binding.AddressRecyclerView.adapter=adapter
            if(!selectedAddress)
            {
                val editBySwipe=object : SwipeToEdit(this)
                {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter=binding.AddressRecyclerView.adapter as com.example.shopandsell.Adapter.AddressAdapter
                        adapter.notifyEditItem(this@AddressActivity,viewHolder.adapterPosition)
                    }
                }
                val editItemTouchHelper=ItemTouchHelper(editBySwipe)
                editItemTouchHelper.attachToRecyclerView(binding.AddressRecyclerView)

                val deleteBySwipe=object : SwipeToDelete(this)
                {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter=binding.AddressRecyclerView.adapter as com.example.shopandsell.Adapter.AddressAdapter
                        showProgressDialog()
                        FirestoreClass().deleteAddressItem(this@AddressActivity,AddressList[viewHolder.adapterPosition].id)
                    }
                }
                val deleteItemTouchHelper=ItemTouchHelper(deleteBySwipe)
                deleteItemTouchHelper.attachToRecyclerView(binding.AddressRecyclerView)
            }



        }
        else
        {
            binding.txtNoAddresses .visibility= View.VISIBLE
            binding.AddressRecyclerView.visibility= View.GONE
        }
    }
    fun getAddressList()
    {
        showProgressDialog()
        FirestoreClass().getAddressItemList(this)
    }

    override fun onResume() {
        super.onResume()
        getAddressList()
    }
    fun addressItemRemovedSuccess()
    {
        hideProgressBar()
        Toast.makeText(this,"Address Removed SuccessFully", Toast.LENGTH_LONG).show()
        getAddressList()
    }
}