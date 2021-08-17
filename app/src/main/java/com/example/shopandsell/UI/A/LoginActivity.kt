package com.example.shopandsell.UI.A

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.User
import com.example.shopandsell.databinding.ActivityLoginBinding
import com.example.shopandsell.utli.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.txtRegisterHere.setOnClickListener {
            val intent= Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.txtForgotPassword.setOnClickListener {
            val intent= Intent(this,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            val intent = Intent(this, DashBoardActivity::class.java);
            startActivity(intent);
            finish();
        }
        binding.btnLogin.setOnClickListener {
            auth = Firebase.auth
            if(validateRegisterDetails())
            {
                showProgressDialog()
                val email=binding.edEmailID.text.toString().trim(){ it<= ' '} //remove empty spaces
                val password=binding.edPassword.text.toString().trim(){ it<= ' '}
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->

                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information

                            FirestoreClass().getUserDetails(this)
                        } else {
                            // If sign in fails, display a message to the user.
                            hideProgressBar()
                             showErrorSnackBar(task.exception!!.message.toString(),true)
                        }
                    }

            }

        }
    }
    private fun validateRegisterDetails():Boolean {
        return when {

            TextUtils.isEmpty(binding.edEmailID.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please Enter An Email ID", true)
                false
            }
            TextUtils.isEmpty(binding.edPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please Enter A Password", true)
                false
            }

            else -> {
                //  showErrorSnackBar("Detail's Validated.",false)

                true
            }
        }
    }

    fun userLoogedInSuccess(user: User) {
        hideProgressBar()

        Log.i("First Name",user.firstName)
        if(user.profileCompleted==0)
        {
            val intent = Intent(this,UserProflieActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
            finish()

        }
        else
        {
            startActivity(Intent(this,DashBoardActivity::class.java))
            finish()
        }


    }
}