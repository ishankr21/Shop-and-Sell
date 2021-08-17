package com.example.shopandsell.UI.Fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shopandsell.R



open class BaseFragment : Fragment() {
    private lateinit var mProgressDialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    fun showProgressDialog()
    {
        mProgressDialog= Dialog(requireActivity())
        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.setCancelable(false) //can't just click on it's left and cancel it
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }
    fun hideProgressBar()
    {
        mProgressDialog.dismiss()
    }
}