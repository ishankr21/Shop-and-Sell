package com.example.shopandsell.UI.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopandsell.Adapter.ProductAdapter
import com.example.shopandsell.Adapter.SoldProductListAdapter
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.SoldProducts
import com.example.shopandsell.R
import com.example.shopandsell.databinding.FragmentProductsBinding
import com.example.shopandsell.databinding.FragmentSoldProductsBinding


class SoldProductsFragment : BaseFragment() {
    private var _binding: FragmentSoldProductsBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoldProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    fun getSoldProductList()
    {
        showProgressDialog()
        FirestoreClass().getSoldProductList(this)

    }
    fun onSuccessfullyGettingTheSoldProductList(soldProductsList:ArrayList<SoldProducts>)
    {
        hideProgressBar()
        if(soldProductsList.size>0)
        {
            binding.soldProductFragmentTextView.visibility=View.GONE
            binding.soldProductsFragmentRecyclerView.visibility=View.VISIBLE
            binding.soldProductsFragmentRecyclerView.layoutManager= LinearLayoutManager(activity)
            binding.soldProductsFragmentRecyclerView.setHasFixedSize(true)
            val adapter= SoldProductListAdapter(requireActivity(),soldProductsList)
            binding.soldProductsFragmentRecyclerView.adapter=adapter


        }
        else
        {
            binding.soldProductFragmentTextView.visibility=View.VISIBLE
            binding.soldProductsFragmentRecyclerView.visibility=View.GONE
        }


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        getSoldProductList()
        super.onResume()
    }


}