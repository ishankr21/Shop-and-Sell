package com.example.shopandsell.UI.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopandsell.Adapter.DashboardProductAdapter
import com.example.shopandsell.Adapter.ProductAdapter
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.Product
import com.example.shopandsell.R
import com.example.shopandsell.UI.A.CartActivity
import com.example.shopandsell.UI.A.SettingsActivity
import com.example.shopandsell.databinding.FragmentDashBoardBinding

class DashBoardFragment : BaseFragment() {

    private var _binding: FragmentDashBoardBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDashBoardBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        when(id)
        {
            R.id.action_settings->
            {
                startActivity(Intent(activity,SettingsActivity::class.java))
                return true
            }
            R.id.action_cart->
            {
                startActivity(Intent(activity,CartActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun onSuccessfullyGettingTheDashBoardProductList(DashboardProductsList:ArrayList<Product>)
    {
        hideProgressBar()
        if(DashboardProductsList.size>0)
        {
            binding.dashboardFragmentTextView.visibility=View.GONE
            binding.DashBoardFragmentRecyclerView.visibility=View.VISIBLE
            binding.DashBoardFragmentRecyclerView.layoutManager= LinearLayoutManager(activity)
            binding.DashBoardFragmentRecyclerView.setHasFixedSize(true)
            val adapter= DashboardProductAdapter(requireActivity(),DashboardProductsList)
            binding.DashBoardFragmentRecyclerView.adapter=adapter


        }
        else
        {
            binding.dashboardFragmentTextView.visibility=View.VISIBLE
            binding.DashBoardFragmentRecyclerView.visibility=View.GONE
        }
    }
    fun getDashboardProductsList()
    {
        showProgressDialog()
        FirestoreClass().getDashBoardProductsList(this)
    }

    override fun onResume() {
        getDashboardProductsList()
        super.onResume()
    }
}