package com.example.shopandsell.UI.Fragments

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopandsell.Adapter.DashboardProductAdapter
import com.example.shopandsell.FireStore.FirestoreClass
import com.example.shopandsell.Models.Cart_Item
import com.example.shopandsell.Models.Product
import com.example.shopandsell.R
import com.example.shopandsell.UI.A.CartActivity
import com.example.shopandsell.UI.A.MainActivity
import com.example.shopandsell.UI.A.SettingsActivity
import com.example.shopandsell.dao.cart_item_dao
import com.example.shopandsell.database.Database
import com.example.shopandsell.databinding.FragmentDashBoardBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


class DashBoardFragment : BaseFragment() {

    private var _binding: FragmentDashBoardBinding? = null
    private var categories= arrayListOf<String>()
    private var productList= arrayListOf<Product>()
    private lateinit var adapter: DashboardProductAdapter
    private lateinit var dao:cart_item_dao
    private lateinit var cartItemList:LiveData<MutableList<Cart_Item>>
    var noOfItemsInCart=0
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
        dao=Database.initDatabase(requireContext()).cartDao

        cartItemList= dao.getAllCartItems()
        cartItemList.observe(viewLifecycleOwner)
        {
            noOfItemsInCart=it.size
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       binding.catFood.setOnClickListener {
            onClick(binding.catFood)


        }

        binding.catMobiles.setOnClickListener {
            onClick(binding.catMobiles)
        }
        binding.catElectronics.setOnClickListener {
            onClick(binding.catElectronics)
        }
        binding.catToys.setOnClickListener {
            onClick(binding.catToys)
        }
        binding.catFurniture.setOnClickListener {
            onClick(binding.catFurniture)
        }
        binding.catSports.setOnClickListener {
            onClick(binding.catSports)
        }
        binding.catKitchen.setOnClickListener {
            onClick(binding.catKitchen)
        }
        binding.catMakeup.setOnClickListener {
            onClick(binding.catMakeup)
        }

    }
    private fun  onClick(view: TextView)
    {

        if(categories.contains(view.text.toString()))
        {
            view.background = ContextCompat.getDrawable(requireContext(),R.drawable.corners_cureved_background2)
            categories.remove(view.text.toString())
            updateProducts()
        }
        else
        {
            view.background = ContextCompat.getDrawable(requireContext(),R.drawable.corners_cureved_background)

            categories.add(view.text.toString())
            updateProducts()
        }
    }
    private fun updateProducts() {
        if(categories.size==0)
        {
            adapter.updateProducts(productList)
        }
        else
        {
            val tempArr= arrayListOf<Product>()
            for(i in productList)
            {
                if(categories.contains(i.category))
                    tempArr.add(i)

            }
            adapter.updateProducts(tempArr)

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu,menu)

        val menuItem=menu.findItem(R.id.action_cart)
        val actionView=menuItem.actionView
        val tv:TextView=actionView.findViewById(R.id.cart_batch_textView)
        actionView.setOnClickListener{
            onOptionsItemSelected(menuItem)
        }
        tv.text=noOfItemsInCart.toString()
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
        productList.clear()
        if(DashboardProductsList.size>0)
        {
            productList.addAll(DashboardProductsList)
            binding.dashboardFragmentTextView.visibility=View.GONE
            binding.DashBoardFragmentRecyclerView.visibility=View.VISIBLE
            binding.llCategories.visibility=View.VISIBLE
            binding.DashBoardFragmentRecyclerView.layoutManager= LinearLayoutManager(activity)
            binding.DashBoardFragmentRecyclerView.setHasFixedSize(true)
            adapter= DashboardProductAdapter(requireActivity(),DashboardProductsList)
            binding.DashBoardFragmentRecyclerView.adapter=adapter


        }
        else
        {
            binding.dashboardFragmentTextView.visibility=View.VISIBLE
            binding.DashBoardFragmentRecyclerView.visibility=View.GONE
            binding.llCategories.visibility=View.GONE
        }
    }
    fun getDashboardProductsList()
    {
        showProgressDialog()
        FirestoreClass().getDashBoardProductsList(this)
    }

    override fun onResume() {
        getDashboardProductsList()

        cartItemList= dao.getAllCartItems()
        cartItemList.observe(viewLifecycleOwner)
        {
            noOfItemsInCart=it.size
            Log.d("ishan","${it}")
        }
        activity?.invalidateOptionsMenu()
        super.onResume()
    }
}