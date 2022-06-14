package com.example.shopandsell.UI.A

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.Address
import com.example.shopandsell.Models.PincodeData
import com.example.shopandsell.R
import com.example.shopandsell.api.RetrofitInstance
import com.example.shopandsell.databinding.ActivityAddEditAddressBinding
import com.example.shopandsell.utli.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddEditAddressActivity : BaseActivity() {
    private lateinit var binding: ActivityAddEditAddressBinding
    private var addressDetails: Address?=null
    var verified=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.EditAddAddressToolbar)
        binding.txtVerify.setOnClickListener {
            if(TextUtils.isEmpty(binding.editAddAddressEdZipCode.text.toString().trim { it <= ' '}))
            {
                showErrorSnackBar("Please Enter the zip code",true)
            }
            else
            {

                binding.progressVerify.visibility= View.VISIBLE
                binding.btnSaveAddress.isEnabled=false
                binding.txtVerify.text=""
                val pincode = binding.editAddAddressEdZipCode.text.toString()
                val data = checkPincode(pincode)
                data.observe(this)
                {

                    binding.progressVerify.visibility= View.GONE
                    binding.btnSaveAddress.isEnabled=true
                    if (it!![0].PostOffice.isNullOrEmpty()) {
                        binding.txtVerify.text = "Not Verified"
                        binding.txtVerify.setTextColor(ContextCompat.getColor(this, R.color.Red))
                        verified = 0
                    } else {
                        binding.txtVerify.text = "Verified"
                        binding.txtVerify.setTextColor(ContextCompat.getColor(this, R.color.Green))
                        verified = 1
                        binding.stateCity.visibility=View.VISIBLE
                        binding.state.text=it[0].PostOffice[0].State
                        binding.city.text=it[0].PostOffice[0].District
                    }

                }
            }




        }
        val textWatcher=object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                verified=0
                binding.txtVerify.text="Verify"
                binding.txtVerify.setTextColor(ContextCompat.getColor(this@AddEditAddressActivity, R.color.black))
                binding.stateCity.visibility=View.GONE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                verified=0
                binding.txtVerify.text="Verify"
                binding.txtVerify.setTextColor(ContextCompat.getColor(this@AddEditAddressActivity, R.color.black))
                binding.stateCity.visibility=View.GONE


            }

            override fun afterTextChanged(s: Editable?) {
                verified=0
                binding.txtVerify.text="Verify"
                binding.txtVerify.setTextColor(ContextCompat.getColor(this@AddEditAddressActivity, R.color.black))
                binding.stateCity.visibility=View.GONE
            }

        }

        binding.editAddAddressEdZipCode.addTextChangedListener(textWatcher)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            addressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)!!


        }
        if (addressDetails != null) {
            binding.txtVerify.text = "Verified"
            binding.txtVerify.setTextColor(ContextCompat.getColor(this, R.color.Green))
            verified = 1
            if (addressDetails!!.id.isNotEmpty()) {
                binding.EditAddAddressToolbar.title = "Edit Address"
                binding.editAddAddressEdFullName.setText(addressDetails?.name)
                binding.editAddAddressEdPhoneNumber.setText(addressDetails?.mobileNumber)
                binding.editAddAddressEdAddress.setText(addressDetails?.address)
                binding.editAddAddressEdZipCode.setText(addressDetails?.zipCode)
                binding.editAddAddressEdAdditionalInformation.setText(addressDetails?.additionalNote)
                if (addressDetails?.type == Constants.HOME) {
                    binding.rbHomeAddress.isChecked = true

                } else if (addressDetails?.type == Constants.OFFICE) {
                    binding.rbOtherAddress.isChecked = true

                } else {
                    binding.rbOtherAddress.isChecked = true

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
            verified==0->{
                showErrorSnackBar("Please Verify the zip code",true)
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

    fun checkPincode(pincode:String):MutableLiveData<ArrayList<PincodeData>?>
    {
            val pincodeData= MutableLiveData<ArrayList<PincodeData>?>()
            RetrofitInstance("https://api.postalpincode.in/pincode/$pincode/").api.check().enqueue(
                object :Callback<ArrayList<PincodeData>>
                {
                    override fun onResponse(call: Call<ArrayList<PincodeData>>, response: Response<ArrayList<PincodeData>>) {
                        Log.d("ishan","${response.body()}")
                        pincodeData.value=response.body()

                    }

                    override fun onFailure(call: Call<ArrayList<PincodeData>>, t: Throwable) {
                        Log.d("ishan","error")
                        pincodeData.value = null
                    }
                }
            )

        return pincodeData

    }



}