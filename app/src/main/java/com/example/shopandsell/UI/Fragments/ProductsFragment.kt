    package com.example.shopandsell.UI.Fragments

    import android.content.Intent
    import android.os.Bundle
    import android.view.*
    import android.widget.Toast
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.example.shopandsell.Adapter.ProductAdapter
    import com.example.shopandsell.FireStore.FirestoreClass
    import com.example.shopandsell.Models.Product
    import com.example.shopandsell.R
    import com.example.shopandsell.UI.A.AddProducts
    import com.example.shopandsell.databinding.FragmentProductsBinding


        class ProductsFragment : BaseFragment() {

        private var _binding: FragmentProductsBinding? = null

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

            _binding = FragmentProductsBinding.inflate(inflater, container, false)
            val root: View = binding.root


            return root
        }

            override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
                inflater.inflate(R.menu.addproductmenu,menu)
                super.onCreateOptionsMenu(menu, inflater)
            }

            override fun onOptionsItemSelected(item: MenuItem): Boolean {
                val id=item.itemId
                when(id)
                {
                    R.id.action_add_products->
                    {
                        startActivity(Intent(activity, AddProducts::class.java))
                        return true
                    }
            }
            return super.onOptionsItemSelected(item)
        }
            fun deleteProduct(productid:String)
            {
                showProgressDialog()
                FirestoreClass().deleteProduct(this,productid)
            }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

            fun onSuccessfullyGettingTheProductList(productsList:ArrayList<Product>)
            {
                hideProgressBar()
                if(productsList.size>0)
                {
                    binding.productFragmentTextView.visibility=View.GONE
                    binding.ProductFragmentRecyclerView.visibility=View.VISIBLE
                    binding.ProductFragmentRecyclerView.layoutManager=LinearLayoutManager(activity)
                    binding.ProductFragmentRecyclerView.setHasFixedSize(true)
                    val adapter=ProductAdapter(requireActivity(),productsList,this)
                    binding.ProductFragmentRecyclerView.adapter=adapter


                }
                else
                {
                    binding.productFragmentTextView.visibility=View.VISIBLE
                    binding.ProductFragmentRecyclerView.visibility=View.GONE
                }
            }
            fun getProductsList()
            {
                showProgressDialog()
                FirestoreClass().getProductsList(this)
            }

            override fun onResume() {
                getProductsList()
                super.onResume()
            }
            fun productDeletedSuccessfully()
            {
                hideProgressBar()
                Toast.makeText(requireActivity(),"Product Deleted!",Toast.LENGTH_LONG).show()
                getProductsList()
            }

        }