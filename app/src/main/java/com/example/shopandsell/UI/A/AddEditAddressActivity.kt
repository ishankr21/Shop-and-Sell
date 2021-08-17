package com.example.shopandsell.UI.A

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopandsell.Adapter.DashboardProductAdapter
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.Address
import com.example.shopandsell.Models.Product
import com.example.shopandsell.R
import com.example.shopandsell.databinding.ActivityAddEditAddressBinding
import com.example.shopandsell.databinding.ActivityAddressBinding
import com.example.shopandsell.utli.Constants

class AddEditAddressActivity : BaseActivity() {
    private lateinit var binding: ActivityAddEditAddressBinding
    private var addressDetails: Address?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.EditAddAddressToolbar)
        val actionBar=supportActionBar
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS))
        {
           addressDetails=intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)!!


        }
        if(addressDetails!=null)
        {
            if(addressDetails!!.id.isNotEmpty())
            {
                binding.EditAddAddressToolbar.title="Edit Address"
                binding.editAddAddressEdFullName.setText(addressDetails?.name)
                binding.editAddAddressEdPhoneNumber.setText(addressDetails?.mobileNumber)
                binding.editAddAddressEdAddress.setText(addressDetails?.address)
                binding.editAddAddressEdZipCode.setText(addressDetails?.zipCode)
                binding.editAddAddressEdAdditionalInformation.setText(addressDetails?.additionalNote)
                if(addressDetails?.type==Constants.HOME)
                {
                    binding.rbHomeAddress.isChecked=true

                }
                else if(addressDetails?.type==Constants.OFFICE)
                {
                    binding.rbOtherAddress.isChecked=true

                }
                else
                {
                    binding.rbOtherAddress.isChecked=true

                }
            }
        }
        binding.EditAddAddressToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.btnSaveAddress.setOnClickListener {
            saveAddressOnCloud()
        }
    }
    private fun validateAddressDetails():Boolean
    {
        return when
        {
            TextUtils.isEmpty(binding.editAddAddressEdFullName.text.toString().trim { it <= ' '})->{
                showErrorSnackBar("Please Enter A Name",true)
                false
            }
            TextUtils.isEmpty(binding.editAddAddressEdPhoneNumber.text.toString().trim { it <= ' '})->{
                showErrorSnackBar("Please Enter A Phone Number",true)
                false
            }
            TextUtils.isEmpty(binding.editAddAddressEdAddress.text.toString().trim { it <= ' '})->{
                showErrorSnackBar("Please Enter the Address",true)
                false
            }
            TextUtils.isEmpty(binding.editAddAddressEdZipCode.text.toString().trim { it <= ' '})->{
                showErrorSnackBar("Please Enter the zip code",true)
                false
            }
            else ->
            {
                //  showErrorSnackBar("Detail's Validated.",false)

                true
            }
        }
    }
    fun saveAddressOnCloud()
    {

        val fullName=binding.editAddAddressEdFullName.text.toString().trim(){ it<= ' '} //remove empty spaces
        val phoneNumber=binding.editAddAddressEdPhoneNumber.text.toString().trim(){ it<= ' '}
        val address=binding.editAddAddressEdAddress.text.toString().trim(){ it<= ' '}
        val zipcode=binding.editAddAddressEdZipCode.text.toString().trim(){ it<= ' '}
        val additionInformation=binding.editAddAddressEdAdditionalInformation.text.toString().trim(){ it<= ' '}
        var type="Home"
        if(validateAddressDetails())
        {
            showProgressDialog()
            val addressType:String = when{
                binding.rbHomeAddress.isChecked->{
                    Constants.HOME
                }
                binding.rbOfficeAddress.isChecked->{
                    Constants.OFFICE
                }
                else->{
                    Constants.OTHER
                }
            }
            val addressModel=Address(
                FirestoreClass().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipcode,
                additionInformation,
                addressType
            )
            if(addressDetails!=null && addressDetails!!.id.isNotEmpty())
                FirestoreClass().updateAddress(this,addressModel,addressDetails!!.id)
            else
            FirestoreClass().addAddress(this,addressModel)


        }

    }
    fun addressUploadSuccess()
    {
        hideProgressBar()
        if (addressDetails!=null && addressDetails!!.id.isNotEmpty())
            Toast.makeText(this,"Address Updated SuccessFully", Toast.LENGTH_LONG).show()
            else
            Toast.makeText(this,"Address Uploaded SuccessFully", Toast.LENGTH_LONG).show()

        finish()

    }

}