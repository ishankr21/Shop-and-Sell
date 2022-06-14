package com.example.shopandsell.UI.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopandsell.Adapter.DashboardProductAdapter
import com.example.shopandsell.Adapter.OrderViewHolder
import com.example.shopandsell.Adapter.OrdersAdapter
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.Order
import com.example.shopandsell.Models.Product
import com.example.shopandsell.R
import com.example.shopandsell.databinding.FragmentDashBoardBinding
import com.example.shopandsell.databinding.FragmentOrdersBinding

class OrdersFragment : BaseFragment() {

    private var _binding: FragmentOrdersBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun onSuccessfullyGettingTheOrderList(ordersList:ArrayList<Order>)
    {
        hideProgressBar()
        if(ordersList.size>0)
        {

            binding.ordersFragmentTextView.visibility=View.GONE
            binding.ordersFragmentRecyclerView.visibility=View.VISIBLE
            binding.ordersFragmentRecyclerView.layoutManager= LinearLayoutManager(activity)
            binding.ordersFragmentRecyclerView.setHasFixedSize(true)
            val adapter= OrdersAdapter(requireActivity(),ordersList)
            binding.ordersFragmentRecyclerView.adapter=adapter


        }
        else {
            binding.ordersFragmentTextView.visibility = View.VISIBLE
            binding.ordersFragmentRecyclerView.visibility = View.GONE
        }
    }

    fun getOrdersList()
    {
        showProgressDialog()
        FirestoreClass().getOrdersList(this)
    }

    override fun onResume() {
        getOrdersList()
        super.onResume()
    }
}