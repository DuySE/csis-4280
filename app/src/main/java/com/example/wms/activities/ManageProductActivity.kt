package com.example.wms.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wms.R
import com.example.wms.adapters.ProductRecyclerViewAdapter
import com.example.wms.apis.ProductRepository
import com.example.wms.databinding.ActivityManageProductBinding
import com.example.wms.models.Product
import java.util.Locale

class ManageProductActivity : DrawerActivity(), ProductRecyclerViewAdapter.OnItemClickListener {
    private lateinit var productList: MutableList<Product>
    private lateinit var recyclerViewProduct: RecyclerView
    private lateinit var myAdapter: ProductRecyclerViewAdapter
    private lateinit var searchView: SearchView
    private lateinit var txtViewNoProduct: TextView
    private lateinit var filteredList: MutableList<Product>

    private lateinit var repository: ProductRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val manageProductBinding = ActivityManageProductBinding.inflate(layoutInflater)
        setContentView(manageProductBinding.root)
        allocateActivityTitle("Manage Products")

        // Initialize product repository with context
        repository = ProductRepository(this)

        // Initialize filtered product list
        productList = mutableListOf()
        filteredList = mutableListOf()

        // Set adapter for recycler view
        recyclerViewProduct = manageProductBinding.recyclerViewItems
        myAdapter = ProductRecyclerViewAdapter(productList, this)
        recyclerViewProduct.adapter = myAdapter
        recyclerViewProduct.layoutManager = GridLayoutManager(this, 2)

        txtViewNoProduct = manageProductBinding.textViewNoProductFound
        searchView = manageProductBinding.searchView
        val addNewProdImgView = manageProductBinding.startNewProdActivityImgView
        // Set listener for search view
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                // Set listener for text change in search view
                override fun onQueryTextChange(newText: String): Boolean {
                    filterProduct(newText, productList)
                    return true
                }
            })
        // Fetch all products from database
        repository.getAllProducts(onSuccess =
        { products ->
            productList = products.toMutableList()
            txtViewNoProduct.text =
                if (productList.isEmpty()) getString(R.string.txtNoProductFound) else ""
            myAdapter.setFilteredList(productList)
        }, onError =
        { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })
        // Set listener for add new product button
        addNewProdImgView.setOnClickListener {
            startActivity(
                Intent(this, NewProductActivity::class.java)
            )
        }
    }

    // Filter products based on search query
    private fun filterProduct(newText: String, products: List<Product>) {
        filteredList = products.filter {
            it.name.lowercase(Locale.getDefault())
                .contains(newText.lowercase(Locale.getDefault()))
        }.toMutableList()
        if (filteredList.isEmpty()) txtViewNoProduct.text = getString(R.string.txtNoProductFound)
        else txtViewNoProduct.text = ""
        myAdapter.setFilteredList(filteredList)
    }

    // Handle edit events on recycler view items
    override fun onItemEdit(i: Int) {
        val intent: Intent = Intent(this, EditProductActivity::class.java)
        val bundle: Bundle = Bundle()
        val id: String =
            (if (filteredList.isEmpty()) productList[i].id.toString() else filteredList[i].id).toString()
        bundle.putString("ID", id)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    // Handle delete events on recycler view items
    override fun onItemDelete(i: Int) {
        repository.deleteProduct(
            id = if (filteredList.isEmpty()) productList[i].id.toString() else filteredList[i].id.toString(),
            onSuccess = { product ->
                if (filteredList.isEmpty()) productList.removeAt(i)
                else filteredList.removeAt(i)
                myAdapter.notifyItemRemoved(i)
                txtViewNoProduct.text = getString(R.string.txtNoProductFound)
                Toast.makeText(this, "Product deleted successfully.", Toast.LENGTH_SHORT).show()
            }, onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            })
    }
}