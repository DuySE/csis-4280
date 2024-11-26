package com.example.wms

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wms.api.ProductRepository
import com.example.wms.databinding.ActivityManageProductBinding
import java.util.ArrayList
import java.util.Locale

class ManageProductActivity : DrawerActivity(), ProductRecyclerViewAdapter.OnItemClickListener {
    private lateinit var products: List<Product>
    private lateinit var recyclerViewProduct: RecyclerView
    private lateinit var myAdapter: ProductRecyclerViewAdapter
    private lateinit var searchView: SearchView
    private lateinit var txtViewNoProduct: TextView
    private lateinit var filteredList: MutableList<Product>

    private lateinit var repository: ProductRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val manageProductBinding = ActivityManageProductBinding.inflate(layoutInflater)
        setContentView(manageProductBinding.getRoot())
        allocateActivityTitle("Manage Products")

        // Initialize product repository with context
        repository = ProductRepository(this)

        txtViewNoProduct = manageProductBinding.textViewNoProductFound
        searchView = manageProductBinding.searchView
        val addNewProdImgView = manageProductBinding.startNewProdActivityImgView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterProduct(newText, products)
                return true
            }
        })

        repository.getAllProducts(onSuccess = { products ->
            if (products.isEmpty()) {
                txtViewNoProduct.setText(R.string.txtNoProductFound)
            } else {
                recyclerViewProduct = manageProductBinding.recyclerViewItems
                myAdapter = ProductRecyclerViewAdapter(products, this)
                recyclerViewProduct.setAdapter(myAdapter)
                recyclerViewProduct.setLayoutManager(GridLayoutManager(this, 2))
            }
        }, onError = { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })

        addNewProdImgView.setOnClickListener {
            startActivity(
                Intent(this@ManageProductActivity, NewProductActivity::class.java)
            )
        }
    }

    private fun filterProduct(newText: String, products: List<Product>) {
        filteredList = ArrayList<Product>()
        for (i in products.indices) {
            if (products[i].name.lowercase(Locale.getDefault())
                    .contains(newText.lowercase(Locale.getDefault()))
            ) {
                filteredList.add(products[i])
            }
        }
        if (filteredList.isEmpty()) {
            txtViewNoProduct.setText(R.string.txtNoProductFound)
        } else {
            txtViewNoProduct.text = ""
        }
        myAdapter.setFilteredList(filteredList)
    }

    override fun onItemClick(i: Int) {
        val bundle = Bundle()
        if (!filteredList.isEmpty()) bundle.putString("ID", filteredList[i].id)
        else bundle.putString("ID", products[i].id)
        val intent = Intent(this@ManageProductActivity, EditProductActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}