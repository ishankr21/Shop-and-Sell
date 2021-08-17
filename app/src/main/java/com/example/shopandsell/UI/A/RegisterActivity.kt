package com.example.shopandsell.UI.A

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.User
import com.example.shopandsell.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : BaseActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.txtLoginHere.setOnClickListener {
            onBackPressed()
        }
        setSupportActionBar(binding.CreateAccountToolbar)
        val actionBar=supportActionBar
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        binding.CreateAccountToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }
    private fun validateRegisterDetails():Boolean
    {
        return when
        {
            TextUtils.isEmpty(binding.edRegisterFirstName.text.toString().trim { it <= ' '})->{
                showErrorSnackBar("Please Enter A First Name",true)
                false
            }
            TextUtils.isEmpty(binding.edRegisterSecondName.text.toString().trim { it <= ' '})->{
                showErrorSnackBar("Please Enter A Second Name",true)
                false
            }
            TextUtils.isEmpty(binding.edRegisterEmailId.text.toString().trim { it <= ' '})->{
                showErrorSnackBar("Please Enter An Email ID",true)
                false
            }
            TextUtils.isEmpty(binding.edRegisterPassword.text.toString().trim { it <= ' '})->{
                showErrorSnackBar("Please Enter A Password",true)
                false
            }
            TextUtils.isEmpty(binding.edRegisterConformPassword.text.toString().trim { it <= ' '})->{
                showErrorSnackBar("Please Enter A Confirm Password",true)
                false
            }
            binding.edRegisterPassword.text.toString().trim { it <= ' '} != binding.edRegisterConformPassword.text.toString().trim{it <= ' '} ->{
                showErrorSnackBar("Passwords Don't Match",true)
                false
            }
            !binding.cbTermsAndConditions.isChecked -> {
                showErrorSnackBar("Please Accept Our Terms And Conditions.",true)

                false
            }
            else ->
            {
               //  showErrorSnackBar("Detail's Validated.",false)

                true
            }
        }
    }
    private fun registerUser()
    {
        if(validateRegisterDetails())
        {
            showProgressDialog()
            val email=binding.edRegisterEmailId.text.toString().trim(){ it<= ' '} //remove empty spaces
            val password=binding.edRegisterPassword.text.toString().trim(){ it<= ' '}
            auth = Firebase.auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {


                    val fireBaseuser = task.result!!.user!!
                    val user=User(
                        fireBaseuser.uid,
                        binding.edRegisterFirstName.text.toString().trim{ it <= ' '},
                        binding.edRegisterSecondName.text.toString().trim{ it <= ' '},
                        binding.edRegisterEmailId.text.toString().trim{ it <= ' '}

                    )
                    FirestoreClass().registerUser(this,user)
                    //showErrorSnackBar("Registration Success ${user!!.uid}",false)
                    //auth.signOut()
                    //finish() //go back to login page
                } else {

                    hideProgressBar()
                    showErrorSnackBar(task.exception!!.message.toString(),true)

                }
            }



        }

    }
    fun userRegistrationSuccess()
    {
        hideProgressBar()
        Toast.makeText(this,"Registered SuccessFully",Toast.LENGTH_LONG).show()
    }
}