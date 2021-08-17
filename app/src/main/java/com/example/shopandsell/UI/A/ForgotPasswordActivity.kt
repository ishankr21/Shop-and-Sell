package com.example.shopandsell.UI.A

import android.os.Bundle
import android.widget.Toast
import com.example.shopandsell.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.ForogotPasswordToolbar)
        val actionBar=supportActionBar
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        binding.ForogotPasswordToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.btnSubmitForgotPassword.setOnClickListener {
            val email=binding.edForgotPasswprdEmailID.text.toString().trim{ it <= ' '}
            if(email.isEmpty())
            {
                showErrorSnackBar("Please Enter An Email Address",true)
            }
            else
            {
                showProgressDialog()
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task->
                        hideProgressBar()
                        if(task.isSuccessful)
                        {
                            Toast.makeText(this,"Email Sent Successfully",Toast.LENGTH_LONG).show()
                            finish()
                        }
                        else
                        {
                            showErrorSnackBar(task.exception!!.message.toString(),true)
                        }
                    }

            }
        }
    }
}