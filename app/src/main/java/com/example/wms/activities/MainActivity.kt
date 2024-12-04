package com.example.wms.activities

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wms.R
import com.example.wms.adapters.ItemRecyclerViewAdapter
import com.example.wms.apis.ProductRepository
import com.example.wms.databinding.ActivityMainBinding
import com.example.wms.models.Product
import com.google.android.material.slider.RangeSlider
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
    private lateinit var filteredList: MutableList<Product>
    private lateinit var priceRangeSlider: RangeSlider

    private lateinit var productRepository: ProductRepository
    private lateinit var checkBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        allocateActivityTitle("Market")

        // Initialize product list
        productList = mutableListOf()
        filteredList = mutableListOf()

        // Initialize product repository with context
        productRepository = ProductRepository(this)

        // Set adapter for recycler view
        recyclerView = mainBinding.recyclerViewItems
        itemAdapter = ItemRecyclerViewAdapter(productList, this)
        recyclerView.adapter = itemAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Initialize range slider for price filtering
        priceRangeSlider = mainBinding.priceRangeSlider
        priceRangeSlider.stepSize = 0f
        priceRangeSlider.valueFrom = 1f
        priceRangeSlider.valueTo = 2000f

        // Setup check box for availability
        checkBox = mainBinding.availabilityCheckbox

        setUpProductView()
        setUpSearchView()
    }

    private fun applyFilters() {
        val minPrice = priceRangeSlider.values[0].toDouble()
        val maxPrice = priceRangeSlider.values[1].toDouble()
        filteredList = productList.filter { product ->
            (!checkBox.isChecked || product.quantity > 0) && product.price <= maxPrice
                    && product.price >= minPrice
        }.toMutableList()
        itemAdapter.setFilteredList(filteredList)
        txtViewNoProduct.text =
            if (filteredList.isEmpty()) getString(R.string.txtNoProductFound) else ""
    }

    private fun setUpProductView() {
        txtViewNoProduct = mainBinding.textViewNoProductFound
        // Refactored some code from ManageProductActivity
        productRepository.getAllProducts(onSuccess = { products ->
            productList = products.toMutableList()
            txtViewNoProduct.text =
                if (productList.isEmpty()) getString(R.string.txtNoProductFound) else ""
            itemAdapter.setFilteredList(productList)
        }, onError = { error -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show() })
        // Set listener for price range slider
        priceRangeSlider.addOnChangeListener { _, _, _ -> applyFilters() }
        // Set listener for check box
        checkBox.setOnCheckedChangeListener { _, isChecked -> applyFilters() }
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
        val query = text.lowercase(Locale.getDefault())
        filteredList = productList.filter {
            it.name.lowercase().contains(query) || it.category.lowercase().contains(query) ||
                    it.description.lowercase().contains(query)
        }.toMutableList()
        txtViewNoProduct.text =
            if (filteredList.isEmpty()) getString(R.string.txtNoProductFound) else ""
        itemAdapter.setFilteredList(filteredList)
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