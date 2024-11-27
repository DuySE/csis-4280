package com.example.wms

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wms.api.ProductRepository
import com.example.wms.databinding.ActivityMainBinding
import java.util.ArrayList
import java.util.Locale

// Drawer activity must be extended to function with nav drawer
class MainActivity : DrawerActivity(),
    ItemRecyclerViewAdapter.OnItemClickListener {
    // Binding used for navigation drawer
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var productList: MutableList<Product>
    private lateinit var itemAdapter: ItemRecyclerViewAdapter
    private lateinit var txtViewNoProduct: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var currToast: Toast
    private lateinit var filteredList: MutableList<Product>

    private lateinit var productRepository: ProductRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.getRoot())
        allocateActivityTitle("Market")

        // Initialize product list
        productList = mutableListOf()
        filteredList = mutableListOf()

        // Initialize product repository with context
        productRepository = ProductRepository(this)

        setUpSearchView()
        setUpProductView()
    }

    private fun setUpProductView() {
        // Refactored some code from ManageProductActivity
        productRepository.getAllProducts(onSuccess = { products ->
            productList = products.toMutableList()
            txtViewNoProduct = mainBinding.textViewNoProductFound
            if (productList.isEmpty()) {
                txtViewNoProduct.setText(R.string.txtNoProductFound)
            } else {
                recyclerView = mainBinding.recyclerViewItems
                itemAdapter = ItemRecyclerViewAdapter(productList, this)
                recyclerView.adapter = itemAdapter
                recyclerView.layoutManager = GridLayoutManager(this, 2)
            }
        }, onError = { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })
    }

    private fun setUpSearchView() {
        val searchView = mainBinding.searchView
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(text: String) {
        filteredList = ArrayList<Product>()
        for (product in productList) {
            if (product.name.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                filteredList.add(product)
            }
        }
        if (filteredList.isEmpty()) {
            currToast = Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT)
            currToast.cancel()
            currToast.show()
        } else {
            itemAdapter.setFilteredList(filteredList)
        }
    }

    override fun onProductItemClick(i: Int) {
        val bundle = Bundle()
        if (filteredList.isNotEmpty()) bundle.putString("ID", filteredList[i].id)
        else bundle.putString("ID", productList[i].id)
        val intent = Intent(this, ProductInfoActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}